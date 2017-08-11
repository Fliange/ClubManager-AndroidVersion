package com.nankai.clubmanager.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.nankai.clubmanager.BannerLayout;
import com.nankai.clubmanager.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by zhangjin on 2017/8/5.
 */

public class FoundViewFragment extends Fragment{

    private ListView messageListView;
    private EditText messageInput;

    //用来进行与后端通信的okHttpClient
    private OkHttpClient okHttpClient = new OkHttpClient();
    //获取登陆人信息
    private SharedPreferences sp;
    //登录人
    private int userId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.found_fragment, container,false);
        messageListView = (ListView) view.findViewById(R.id.found_listView);
        messageInput = (EditText) view.findViewById(R.id.message_input);

        //轮播图
        BannerLayout bannerLayout1 = (BannerLayout) view.findViewById(R.id.banner1);
        List<Integer> res = new ArrayList<>();
        res.add(R.drawable.lunbo1);
        res.add(R.drawable.lunbo2);
        res.add(R.drawable.lunbo3);
        List<String> titles = new ArrayList<>();
        titles.add("标题一");
        titles.add("标题二");
        titles.add("标题三");
        if (bannerLayout1 != null) {
            bannerLayout1.setViewRes(res, titles);
        }
        List<String> urls = new ArrayList<>();
        urls.add("http://www.ctsay.com/img/16/0321/56ef5ac94368c.jpeg");
        urls.add("http://www.ctsay.com/img/16/0331/56fc8af888536.jpg");
        urls.add("http://www.ctsay.com/img/16/0205/56b3f70240f6b.jpg");
        urls.add("http://www.ctsay.com/img/15/1215/566f81191b023.jpg");
        //轮播图结束


        getAllMessage();
        messageInput = (EditText) view.findViewById(R.id.message_input);
        //找到记录登录信息的文件
        sp = getActivity().getSharedPreferences("loginInfor",MODE_PRIVATE);
        String user = sp.getString("username", "");
        if (user.equals(""))
        {
            Toast.makeText(getActivity(),"游客状态,不能发言", Toast.LENGTH_SHORT).show();
            Log.i("游客状态------","不能发言");
        }
        else {
            //从文件里找到登录者的id
            userId = Integer.parseInt(sp.getString("username", ""));
            //显示出来所有的公告
            getAllMessage();
            //为发送公告按钮添加监听
            view.findViewById(R.id.send_message).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //监听按钮，按钮按下时，发送输入框里面的数据，同时从数据库查询，将查询的结果显示出来
                    //使用post的方式，向后端action发起传输请求
                    FormBody.Builder builder1 = new FormBody.Builder();
                    String text = messageInput.getText().toString();
                    FormBody formBody = builder1
                            .add("content", text)
                            .add("userId", String.valueOf(userId))
                            .build();

                    Request.Builder builder = new Request.Builder();
                    Request request = builder.url("http://192.168.40.72:8080/PClubManager/Chat_test")
                            .post(formBody)
                            .build();
                    exec(request);
                }
            });
        }
        return view;
    }

    //用于从数据库拿数据 并显示到收藏
    private void getAllMessage()
    {
        //从数据库读取活动信息
        FormBody.Builder builder1 = new FormBody.Builder();
        FormBody formBody = builder1
                .build();
        Request.Builder builder = new Request.Builder();
        Request request1 = builder.url("http://192.168.40.72:8080/PClubManager/Chat_findAll")
                .post(formBody)
                .build();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageInput.setText("");
            }
        });
        exec(request1);
    }

    //将发送request的过程和回调函数的定义封装成一个方法
    private void exec(Request request) {
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("失败：","-----"+e);
                final String error = e.toString();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("成功：","-----");

                //从服务器传回来的json字符串
                final String msg = response.body().string();
                Log.i("发回来的数据",msg);
                if(msg.equals("chatSuccess\r\n"))
                {
                    //重新拿数据
                    getAllMessage();
                }

                else {
                    /**将json字符串转换为List<Map<String,Object>> */
                    List<Map<String, Object>> arrayList = JSON.parseObject(msg,
                            new TypeReference<List<Map<String, Object>>>() {});



                    final SimpleAdapter simpleAdapter = new SimpleAdapter(
                        getActivity(),          //传入一个上下文作为参数
                        arrayList,              //传入相对应的数据源，这个数据源不仅仅是数据而且还是和界面相耦合的混合体。
                        R.layout.issue_tips,    //设置具体某个items的布局，需要是新的布局，而不是ListView控件的布局
                        new String[]{"MessageAuthor","MessageContent"}, //*传入上面定义的键值对的键名称,会自动根据传入的键找到对应的值*//*
                        new int[]{R.id.issuer,R.id.issue_content});//*传入items布局文件中需要指定传入的控件，这里直接上id即可*//*

                    //修改UI控件
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            messageListView.setAdapter(simpleAdapter);
                        }
                    });
                }
            }
        });
    }
}
