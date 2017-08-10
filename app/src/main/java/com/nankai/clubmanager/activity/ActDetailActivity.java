package com.nankai.clubmanager.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.nankai.clubmanager.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import jp.wasabeef.richeditor.RichEditor;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@ContentView(R.layout.act_detail)
public class ActDetailActivity extends Activity {
    @ViewInject(R.id.activity_detail_Name)
    private TextView activityDetailName;/*
    @ViewInject(R.id.activity_detail_content)
    private TextView activityDetailContent;*/
    @ViewInject(R.id.activity_detail_Picture)
    private ImageView activityDetailPicture;
    @ViewInject(R.id.rich_activity_detail_content)
    private RichEditor richActivityDetailContent;
    @ViewInject(R.id.thumb_up)
    private ImageView thumbUp;
    @ViewInject(R.id.thumb_up_count)
    private TextView thumbUpCount;
    @ViewInject(R.id.collection)
    private  ImageView collection;
    @ViewInject(R.id.comment_listView)
    private ListView commentListView;
    @ViewInject(R.id.comment_input)
    private EditText commentInput;

    //点赞/取消
    private boolean like = false;
    //收藏/取消
    private boolean select = false;

    //获取登陆人信息
    private SharedPreferences sp;
    //登录人
    private int userId;
    //收藏的活动的信息
    private int ActivityId;
    //用来进行与后端通信的okHttpClient
    private OkHttpClient okHttpClient = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        //获取intent中的bundle对象
        Bundle bundle = this.getIntent().getExtras();
        //获取数据
        ActivityId = bundle.getInt("ActivityId");
        String ActivityName = bundle.getString("ActivityName");
        String ActivityPicture = bundle.getString("ActivityPicture");
        String ActivityContent = bundle.getString("ActivityContent");
        String ActivityOrganization = bundle.getString("ActivityOrganization");
        //找到记录登录信息的文件
        sp=getSharedPreferences("loginInfor",MODE_PRIVATE);


        activityDetailName.setText(ActivityName);
        richActivityDetailContent.setHtml(ActivityContent);
        richActivityDetailContent.setFocusable(false);
        richActivityDetailContent.setFocusableInTouchMode(false);

        //通过图片名字，将图片对象搞进去,新开线程
        final String IMAGE_URL = "http://192.168.40.72:8080/PClubManager/images/"+ActivityPicture;
        int activityId;
        new Thread(new Runnable() {
            @Override
            public void run() {
                //从服务器拿图片
                final Drawable drawable = loadImageFromNetwork(IMAGE_URL);
                //拿到图片就显示
                ActDetailActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activityDetailPicture.setImageDrawable(drawable);
                    }
                });
            }
        }).start();
        initCollection();
        getAllMessage();
    }

    //返回方法
    @Event(value = {R.id.back_activity},type = View.OnClickListener.class)
    private void doEvent(View view)
    {
        finish();
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
    //点赞方法
    @Event(value = {R.id.thumb_up},type = View.OnClickListener.class)
    private void praiz(View view){
        int count = Integer.parseInt(thumbUpCount.getText().toString());
        if (!like){
            like = true;
            thumbUp.setImageResource(R.drawable.zan2);
            //修改点赞数
            count++;
        }
        else{
            like = false;
            thumbUp.setImageResource(R.drawable.zan1);
            count--;
        }
        thumbUpCount.setText(count+"");
    }

    //初始化收藏状态
    private void initCollection(){
        String user = sp.getString("username", "");
        if (user.equals(""))
        {
            Toast.makeText(this,"游客状态游览",Toast.LENGTH_SHORT);
        }
        else {
            //从文件里找到登录者的id
            userId = Integer.parseInt(sp.getString("username", ""));
            //从数据库查询有没有这个人对这个活动的收藏记录
            FormBody.Builder builder1 = new FormBody.Builder();
            FormBody formBody = builder1
                    .add("activityId", String.valueOf(ActivityId))
                    .add("userId", String.valueOf(userId))
                    .build();
            Request.Builder builder = new Request.Builder();
            Request request = builder.url("http://192.168.40.72:8080/PClubManager/Collection_findCollectionByUserAndActivity")
                    .post(formBody)
                    .build();
            exec(request);
        }
    }

    //收藏方法
    @Event(value = {R.id.collection},type = View.OnClickListener.class)
    private void select(View view){
        String user = sp.getString("username", "");
        if (user.equals(""))
        {
            Toast.makeText(this,"未登录，无法收藏",Toast.LENGTH_SHORT);
        }
        else {
            //从文件里找到登录者的id
            userId = Integer.parseInt(user);
            //收藏状态
            boolean like;
            if (!select) {
                select = true;
                collection.setImageResource(R.drawable.like2);
                //使用post的方式，向后端action发起传输请求
                FormBody.Builder builder1 = new FormBody.Builder();
                FormBody formBody = builder1
                        .add("activityId", String.valueOf(ActivityId))
                        .add("userId", String.valueOf(userId))
                        .add("state", String.valueOf(select))
                        .build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url("http://192.168.40.72:8080/PClubManager/Collection_select")
                        .post(formBody)
                        .build();
                exec(request);
            } else {
                select = false;
                collection.setImageResource(R.drawable.like);
                //使用post的方式，向后端action发起传输请求
                FormBody.Builder builder1 = new FormBody.Builder();
                FormBody formBody = builder1
                        .add("activityId", String.valueOf(ActivityId))
                        .add("userId", String.valueOf(userId))
                        .add("state", String.valueOf(select))
                        .build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url("http://192.168.40.72:8080/PClubManager/Collection_select")
                        .post(formBody)
                        .build();
                exec(request);
            }
        }
    }

    //将发送request的过程和回调函数的定义封装成一个方法
    private void exec(Request request) {
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("失败：","-----"+e);
                final String error = e.toString();
                ActDetailActivity.this.runOnUiThread(new Runnable() {
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
                if(msg.equals("writesuccess\r\n"))
                {//有收藏记录
                    select = true;
                    ActDetailActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            collection.setImageResource(R.drawable.like2);
                            Toast.makeText(ActDetailActivity.this,"有收藏"+msg,Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else if(msg.equals("writefail\r\n"))
                {//没收藏记录
                    select = false;
                    ActDetailActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            collection.setImageResource(R.drawable.like);
                            Toast.makeText(ActDetailActivity.this,"没收藏"+msg,Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else if(msg.equals("chatSuccess\r\n"))
                {
                    //重新拿数据
                    getAllMessage();
                }
                else if(msg.equals(""))
                {

                }
                else {
                    /*将json字符串转换为List<Map<String,Object>> */
                    final List<Map<String, Object>> arrayList = JSON.parseObject(msg,
                            new TypeReference<List<Map<String, Object>>>() {});


                    //修改UI控件
                    ActDetailActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("图片",arrayList.toString());
                            SimpleAdapter simpleAdapter = new SimpleAdapter(
                                    ActDetailActivity.this,          //传入一个上下文作为参数
                                    arrayList,              //传入相对应的数据源，这个数据源不仅仅是数据而且还是和界面相耦合的混合体。
                                    R.layout.issue_tips,    //设置具体某个items的布局，需要是新的布局，而不是ListView控件的布局
                                    new String[]{"MessageAuthor","MessageContent"}, //*传入上面定义的键值对的键名称,会自动根据传入的键找到对应的值*//**//*
                                    new int[]{R.id.issuer,R.id.issue_content});//传入items布局文件中需要指定传入的控件，这里直接上id即可*//**//*
                            commentListView.setAdapter(simpleAdapter);
                        }
                    });
                }

            }
        });
    }

    //用于从数据库拿数据 并显示到评论
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
        ActDetailActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                commentInput.setText("");
            }
        });
        exec(request1);
    }

    //为发送公告按钮添加监听
    @Event(value = {R.id.send_comment},type = View.OnClickListener.class)
    private void sendComment(View v) {
            //监听按钮，按钮按下时，发送输入框里面的数据，同时从数据库查询，将查询的结果显示出来
            //使用post的方式，向后端action发起传输请求
            FormBody.Builder builder1 = new FormBody.Builder();
            String text = commentInput.getText().toString();
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
}
