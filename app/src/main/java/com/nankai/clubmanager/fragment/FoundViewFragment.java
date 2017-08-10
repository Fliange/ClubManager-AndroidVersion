package com.nankai.clubmanager.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.nankai.clubmanager.R;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zhangjin on 2017/8/5.
 */

public class FoundViewFragment extends Fragment{

    private ListView messageListView;

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
        getAllMessage();
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

                /**将json字符串转换为List<Map<String,Object>> */
                List<Map<String, Object>> arrayList = JSON.parseObject(msg,
                        new TypeReference<List<Map<String, Object>>>() {});



                final SimpleAdapter simpleAdapter = new SimpleAdapter(
                    getActivity(),          //传入一个上下文作为参数
                    arrayList,              //传入相对应的数据源，这个数据源不仅仅是数据而且还是和界面相耦合的混合体。
                    R.layout.issue_tips,    //设置具体某个items的布局，需要是新的布局，而不是ListView控件的布局
                    new String[]{"MessageAuthor","MessageContent"}, //*传入上面定义的键值对的键名称,会自动根据传入的键找到对应的值*//*
                    new int[]{R.id.issuer,R.id.issue_content});//*传入items布局文件中需要指定传入的控件，这里直接上id即可*//*

                /*//加载页面
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AVLoadingIndicatorView avi;
                        avi= (AVLoadingIndicatorView) getActivity().findViewById(R.id.avi);
                        avi.hide();
                    }
                });*/

                // or avi.smoothToHide();

                //修改UI控件
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messageListView.setAdapter(simpleAdapter);
                    }
                });
            }
        });
    }
}
