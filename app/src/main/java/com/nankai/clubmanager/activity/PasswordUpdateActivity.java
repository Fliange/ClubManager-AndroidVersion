package com.nankai.clubmanager.activity;

import android.app.Activity;
import android.content.Intent;
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

@ContentView(R.layout.password_update)
public class PasswordUpdateActivity extends Activity {

    @ViewInject(R.id.updatepassword_username)
    private EditText username;
    @ViewInject(R.id.update_oldpassword)
    private EditText oldPwd;
    @ViewInject(R.id.update_newpassword)
    private EditText newPwd;
    @ViewInject(R.id.update_repassword)
    private EditText confirmPwd;

    OkHttpClient okHttpClient = new OkHttpClient();
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 2){
                String result = (String) msg.obj;
                if(result.equals("success")){
                    Toast.makeText(PasswordUpdateActivity.this,"修改成功！",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(PasswordUpdateActivity.this,MainActivity.class);
                    startActivity(intent);
                }else{
                    if(result.equals("difference")){
                        Toast.makeText(PasswordUpdateActivity.this,"两次密码不同！",Toast.LENGTH_SHORT).show();
                    }
                    if(result.equals("none")){
                        Toast.makeText(PasswordUpdateActivity.this,"用户名不存在！",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };
    @Event(value={R.id.main_btn_login})
    private void doEvent(View view){
        FormBody.Builder builder1 = new FormBody.Builder();
        FormBody formBody = builder1.add("username",username.getText().toString())
                .add("oldPwd", oldPwd.getText().toString())
                .add("newPwd",newPwd.getText().toString())
                .add("confirmPwd",confirmPwd.getText().toString())
                .build();
        Request.Builder builder = new Request.Builder();
        Request request1 = builder.url("http://192.168.40.70:8080/PClubManager/Member_updatePasswordForAndroid")
                .post(formBody)
                .build();
        exec(request1);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
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
                message.what = 2;
                message.obj = s;
                handler.sendMessage(message);
            }
        });
    }
}
