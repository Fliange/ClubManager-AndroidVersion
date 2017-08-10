package com.nankai.clubmanager.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
 * Created by Administrator on 2017/8/10.
 */
@ContentView(R.layout.new_regist_manage)
public class RegisterDetailActivity extends Activity {

    @ViewInject(R.id.new_regist_id)
    private EditText number;
    @ViewInject(R.id.new_redist_name)
    private EditText name;
    @ViewInject(R.id.new_redist_sex)
    private EditText sex;
    @ViewInject(R.id.new_redist_college)
    private EditText major;
    @ViewInject(R.id.new_redist_home)
    private EditText hometown;
    @ViewInject(R.id.new_redist_phone)
    private EditText phone;
    @ViewInject(R.id.new_redist_position)
    private EditText position;
    @ViewInject(R.id.new_redist_department)
    private EditText department;
    @ViewInject(R.id.new_redist_birthday)
    private EditText birthday;
    @ViewInject(R.id.new_redist_departmentid1)
    private EditText department1;
    @ViewInject(R.id.new_redist_departmentid2)
    private EditText department2;
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
                    Toast.makeText(RegisterDetailActivity.this,"添加成功，新成员get",Toast.LENGTH_SHORT).show();
//                    Intent intent=new Intent(RegisterDetailActivity.this, MainActivity.class);
//                    startActivity(intent);
                }
            }
            if(msg.what==2){
                String result = (String) msg.obj;
                if(result.equals("success")){
                    Toast.makeText(RegisterDetailActivity.this,"缘分天注定~~",Toast.LENGTH_SHORT).show();
//                    Intent intent=new Intent(RegisterDetailActivity.this, ManageViewFragment.class);
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
        birthday.setText(intent.getStringExtra("birthday"));
        department1.setText(intent.getStringExtra("department1"));
        department2.setText(intent.getStringExtra("department2"));
        imgPath = intent.getStringExtra("picture");
        IMAGE_URL = "http://192.168.40.70:8080/PClubManager/images/" + imgPath;
        new Thread(new Runnable() {
            @Override
            public void run() {
                drawable = loadImageFromNetwork(IMAGE_URL);
                RegisterDetailActivity.this.runOnUiThread(new Runnable() {
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
    @Event({R.id.welcome_new_regist,R.id.delete_new_regist})
    private void doEvent(View v){
        switch (v.getId()){
            case R.id.welcome_new_regist:
                String s=department.getText().toString();
                FormBody.Builder builder1 = new FormBody.Builder();
                FormBody formBody = builder1
                        .add("rnumber",number.getText().toString())
                        .add("rname", name.getText().toString())
                        .add("rphone",phone.getText().toString())
                        .add("rsex",sex.getText().toString())
                        .add("rhometown",hometown.getText().toString())
                        .add("rmajor",major.getText().toString())
                        .add("rbirthday",birthday.getText().toString())
                        .add("rposition",position.getText().toString())
                        .add("dpt1",s)
                        .add("rpicturePath",imgPath)
                        .build();
                Request.Builder builder = new Request.Builder();
                Request request1 = builder.url("http://192.168.40.70:8080/PClubManager/Member_addForAndroid")
                        .post(formBody)
                        .build();
                exec(request1,1);
                break;
            case R.id.delete_new_regist:
                AlertDialog.Builder builder2=new AlertDialog.Builder(RegisterDetailActivity.this);
                builder2.setTitle("啊哈");
                builder2.setIcon(R.drawable.ah);
                builder2.setMessage("确定要删除吗？");
                builder2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FormBody.Builder builder1 = new FormBody.Builder();
                        FormBody formBody = builder1
                                .add("registerNumber",number.getText().toString())
                                .build();
                        Request.Builder builder = new Request.Builder();
                        Request request = builder.url("http://192.168.40.70:8080/PClubManager/register_deleteForAndroid")
                                .post(formBody)
                                .build();
                        exec(request,2);
                    }
                });
                builder2.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder2.show();
                break;
        }
        //从数据库读取活动信息

        //exec(request4);
    }
    private void exec(Request request, final int which) {
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
                if(which==1){
                    message.what = 1;
                }
                if(which==2){
                    message.what = 2;
                }
                message.obj = s;
                handler.sendMessage(message);
            }
        });
    }
}
