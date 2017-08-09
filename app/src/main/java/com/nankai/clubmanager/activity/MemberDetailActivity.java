package com.nankai.clubmanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.nankai.clubmanager.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/8/8.
 */
@ContentView(R.layout.update_member)
public class MemberDetailActivity extends Activity {

    @ViewInject(R.id.update_member_id)
    private EditText number;
    @ViewInject(R.id.update_member_name)
    private EditText name;
    @ViewInject(R.id.update_member_sex)
    private EditText sex;
    @ViewInject(R.id.update_member_college)
    private EditText major;
    @ViewInject(R.id.update_member_home)
    private EditText hometown;
    @ViewInject(R.id.update_member_phone)
    private EditText phone;
    @ViewInject(R.id.update_member_position)
    private EditText position;
    @ViewInject(R.id.update_member_birthday)
    private EditText birthday;
    @ViewInject(R.id.update_member_departmentid)
    private EditText department;
    @ViewInject(R.id.picture)
    private ImageView picture;

    String imgPath, IMAGE_URL;
    Drawable drawable;
    OkHttpClient okHttpClient = new OkHttpClient();
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                String result = (String) msg.obj;
                if(result.equals("success")){
                    Toast.makeText(MemberDetailActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
//                    Intent intent=new Intent(MemberDetailActivity.this, MemberFragment.class);
//                    startActivity(intent);
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        Intent intent = getIntent();
        number.setText(String.valueOf(intent.getIntExtra("number",0)));
        name.setText(intent.getStringExtra("name"));
        sex.setText(intent.getStringExtra("gender"));
        major.setText(intent.getStringExtra("major"));
        hometown.setText(intent.getStringExtra("hometown"));
        phone.setText(intent.getStringExtra("phone"));
        position.setText(intent.getStringExtra("position"));
        birthday.setText(intent.getStringExtra("birthday"));
        department.setText(intent.getStringExtra("department"));
        imgPath = intent.getStringExtra("picture");
        IMAGE_URL = "http://192.168.40.70:8080/PClubManager/images/" + imgPath;
        new Thread(new Runnable() {
            @Override
            public void run() {
                drawable = loadImageFromNetwork(IMAGE_URL);
                MemberDetailActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        picture.setImageDrawable(drawable);
                    }
                });
            }
        }).start();
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
    @Event({R.id.update_member_sure})
    private void doEvent(View v){
       /* FormBody.Builder builder3 = new FormBody.Builder();
        FormBody formBody1 = builder3
                .add("number",number.getText().toString())
                .add("name", name.getText().toString())
                .add("phone",phone.getText().toString())
                .add("sex",sex.getText().toString())
                .add("hometown",hometown.getText().toString())
                .add("major",major.getText().toString())
                .add("birthday",birthday.getText().toString())
                .add("position",position.getText().toString())
                .add("department",department.getText().toString())
                .add("picturePath",imgPath)
                .build();
        Request.Builder builder4 = new Request.Builder();
        Request request4 = builder4.url("http://192.168.40.70:8080/PClubManager/Member_updateMemberForAndroid")
                .post(formBody1)
                .build();*/

        //从数据库读取活动信息
        FormBody.Builder builder1 = new FormBody.Builder();
        FormBody formBody = builder1
                .add("number",number.getText().toString())
                .add("name", name.getText().toString())
                .add("phone",phone.getText().toString())
                .add("sex",sex.getText().toString())
                .add("hometown",hometown.getText().toString())
                .add("major",major.getText().toString())
                .add("birthday",birthday.getText().toString())
                .add("position",position.getText().toString())
                .add("dpt",department.getText().toString())
                .add("picturePath",imgPath)
                .build();
        Request.Builder builder = new Request.Builder();
        Request request1 = builder.url("http://192.168.40.70:8080/PClubManager/Member_updateMemberForAndroid")
                .post(formBody)
                .build();
        exec(request1);
        //exec(request4);
    }
    private void exec(Request request) {
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //失败
                Log.i("异常：","--->"+e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //成功
                Log.i("成功：","--->");
                String s = response.body().string();
                Log.i("结果：","--->"+s);
                Message message = new Message();
                message.what = 1;
                message.obj = s;
                handler.sendMessage(message);
            }
        });
    }
}
