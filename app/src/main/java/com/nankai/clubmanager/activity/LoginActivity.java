package com.nankai.clubmanager.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.nankai.clubmanager.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@ContentView(R.layout.activity_login)
public class LoginActivity extends Activity {
    @ViewInject(R.id.login_username)
    private EditText username;
    @ViewInject(R.id.login_password)
    private EditText password;

    private  SharedPreferences sp;
    OkHttpClient okHttpClient = new OkHttpClient();
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                String result = (String) msg.obj;//返回他们的职位
                if(result.equals("fail")){
                    Toast.makeText(LoginActivity.this,"账号或密码错误",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                    //管理员或者是部门内部的人登录，把他们的职位存起来
//                    SharedPreferences.Editor editor = sp.edit();
//                    editor.putString("position",result);
//                    editor.commit();
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
    }

    @Event(value={R.id.login_button,R.id.login_changePwd})
    private void doEvent(View view){
        switch(view.getId()){
            case R.id.login_button:
                //与后台连接
                FormBody.Builder builder1 = new FormBody.Builder();
                String use = username.getText().toString();
                FormBody formBody = builder1.add("username",username.getText().toString())
                        .add("pwd", password.getText().toString()).build();

                Request.Builder builder = new Request.Builder();
                Request request1 = builder.url("http://192.168.40.70:8080/PClubManager/login")
                        .post(formBody)
                        .build();
                exec(request1);
                break;
            case R.id.login_changePwd:
                break;
        }
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
                Message message = new Message();
                message.what = 1;
                message.obj = s;
                handler.sendMessage(message);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}
