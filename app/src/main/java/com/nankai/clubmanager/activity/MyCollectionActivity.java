package com.nankai.clubmanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.nankai.clubmanager.R;
import com.wang.avi.AVLoadingIndicatorView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@ContentView(R.layout.my_collection)
public class MyCollectionActivity extends Activity {

    @ViewInject(R.id.mycollection_listview)
    private ListView mycollectionListview;
    //用来进行与后端通信的okHttpClient
    private OkHttpClient okHttpClient = new OkHttpClient();
    //获取登陆人信息
    private SharedPreferences sp;
    //登录人
    private int userId;
    //定义一个适配器对象
    private List<Map<String,Object>> list_map = new ArrayList<Map<String,Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        //找到记录登录信息的文件
        sp=getSharedPreferences("loginInfor",MODE_PRIVATE);
        String user = sp.getString("username", "");
        if (user == null)
        {
            Toast.makeText(this,"游客状态,没有收藏",Toast.LENGTH_SHORT);
        }
        else {
            //从文件里找到登录者的id
            userId = Integer.parseInt(sp.getString("username", ""));
            setHomePage();
        }
    }

    //用于从数据库拿数据 并显示到收藏
    private void setHomePage()
    {
        //从数据库读取活动信息
        FormBody.Builder builder1 = new FormBody.Builder();
        FormBody formBody = builder1
                .add("userId", String.valueOf(userId))
                .build();
        Request.Builder builder = new Request.Builder();
        Request request1 = builder.url("http://192.168.40.72:8080/PClubManager/Act_showAllCollection")
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
                MyCollectionActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //chatroomContent.setText(error);
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

                //遍历json数组,将图片对象搞进去

                String activityName, activityPic,IMAGE_URL,activityOrganization,activityIntroduction,activityContent;
                int activityId;
                Drawable drawable;
                //清空之前的列表
                list_map.clear();
                for (int i = 0; i < arrayList.size(); i++) {
                    Map<String, Object> activity = arrayList.get(i);
                    activityPic = (String) activity.get("ActivityPicture");
                    IMAGE_URL = "http://192.168.40.72:8080/PClubManager/images/"+activityPic;
                    drawable = loadImageFromNetwork(IMAGE_URL);
                    activity.put("pic",drawable);
                    list_map.add(activity);
                }
                final SimpleAdapter simpleAdapter = new SimpleAdapter(
                        MyCollectionActivity.this,//传入一个上下文作为参数
                        list_map,         //传入相对应的数据源，这个数据源不仅仅是数据而且还是和界面相耦合的混合体。
                        R.layout.homepage_list_item, //设置具体某个items的布局，需要是新的布局，而不是ListView控件的布局
                        new String[]{"pic","ActivityName","ActivityOrganization","ActivityIntroduction"}, //*传入上面定义的键值对的键名称,会自动根据传入的键找到对应的值*//*
                        new int[]{R.id.item_img,R.id.item_title,R.id.item_author,R.id.item_text});//*传入items布局文件中需要指定传入的控件，这里直接上id即可*//*
                //加载页面
                try
                {
                    Thread.currentThread().sleep(1000);//毫秒
                }
                catch(Exception e){}
                MyCollectionActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AVLoadingIndicatorView avi;
                        avi= (AVLoadingIndicatorView) findViewById(R.id.avi);
                        avi.hide();
                    }
                });

                // or avi.smoothToHide();
                //ViewBinder该类可以帮助SimpleAdapter加载图片(Drawable)
                simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                    @Override
                    public boolean setViewValue(View view, Object data, String textRepresentation) {
                        if(view instanceof ImageView && data instanceof Drawable){
                            ImageView iv = (ImageView)view;
                            iv.setImageDrawable((Drawable)data);
                            return true;
                        }else{
                            return false;
                        }
                    }
                });


                //修改UI控件
                MyCollectionActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mycollectionListview.setAdapter(simpleAdapter);
                        mycollectionListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //TextView activityDetailName = (TextView) view.findViewById(R.id.activity_detail_Name);
                                //String s = activityDetailName.getText().toString();
                                Map<String,Object> map = list_map.get(position);
                                int actId = (int) map.get("ActivityId");
                                Log.i("活动ID-----",""+actId);
                                Intent intent = new Intent(MyCollectionActivity.this,ActDetailActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putInt("ActivityId", actId);
                                bundle.putString("ActivityPicture", (String) map.get("ActivityPicture"));
                                bundle.putString("ActivityName", (String) map.get("ActivityName"));
                                bundle.putString("ActivityContent", (String) map.get("ActivityContent"));
                                bundle.putString("ActivityOrganization", (String) map.get("ActivityOrganization"));
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        });
                    }
                });
            }
        });
    }

    //获取图片的方法
    private Drawable loadImageFromNetwork(String imageUrl)
    {
        Drawable drawable = null;
        try {
            // 可以在这里通过文件名来判断，是否本地有此图片
            drawable = Drawable.createFromStream(new URL(imageUrl).openStream(), "image.jpg");
        } catch (IOException e) {
            Log.d("test", e.getMessage());
        }
        if (drawable == null) {
            Log.d("test", "null drawable");
        } else {
            Log.d("test", "not null drawable");
        }
        return drawable ;
    }
}
