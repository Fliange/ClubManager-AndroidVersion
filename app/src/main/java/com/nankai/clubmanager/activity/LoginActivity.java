package com.nankai.clubmanager.activity;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nankai.clubmanager.JellyInterpolator;
import com.nankai.clubmanager.R;

import org.json.JSONException;
import org.json.JSONObject;
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
    @ViewInject(R.id.main_btn_login)
    private  TextView mBtnLogin;
    @ViewInject(R.id.input_layout_name)
    private  LinearLayout mName;
    @ViewInject(R.id.input_layout_psw)
    private  LinearLayout mPsw;
    @ViewInject(R.id.layout_progress)
    private View progress;
    @ViewInject(R.id.input_layout)
    private View mInputLayout;

    private SharedPreferences sp;
    OkHttpClient okHttpClient = new OkHttpClient();
    private float mWidth, mHeight;

    @Event(value={R.id.main_btn_login,R.id.main_update})
    private void doEvent(View view){
        switch(view.getId()){
            case R.id.main_btn_login:
                //动画效果
                mWidth = mBtnLogin.getMeasuredWidth();
                mHeight = mBtnLogin.getMeasuredHeight();
                mName.setVisibility(View.INVISIBLE);
                mPsw.setVisibility(View.INVISIBLE);
                inputAnimator(mInputLayout, mWidth, mHeight);
                break;
            case R.id.main_update:
                Intent intent=new Intent(LoginActivity.this,PasswordUpdateActivity.class);
                startActivity(intent);
                break;
        }
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                String result = (String) msg.obj;//返回他们的职位和所在的部门
                if(result.equals("fail")){
                    Toast.makeText(LoginActivity.this,"账号或密码错误",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(LoginActivity.this,LoginActivity.class);
                    startActivity(intent);
                }else{
                    //管理员或者是部门内部的人登录，把他们的职位和所在的部门存起来
                    Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    try {
                        JSONObject obj=new JSONObject(result);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("position",obj.getString("position"));//把职位存起来
                        editor.putInt("department",obj.getInt("department"));//把所在的部门存起来
                        editor.putBoolean("status",true);//登录的状态已经登录
                        editor.commit();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        //文件里存取了登录人的用户名和密码以及职位和是否登录的状态
        sp=getSharedPreferences("loginInfor",MODE_PRIVATE);
        //如果文件里已经有就拿出来显示
        String temp_username = sp.getString("username","");
        String temp_password = sp.getString("password","");
        username.setText(temp_username);
        password.setText(temp_password);
    }

    private void inputAnimator(final View view, float w, float h) {

        AnimatorSet set = new AnimatorSet();
        ValueAnimator animator = ValueAnimator.ofFloat(0, w);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                MarginLayoutParams params = (MarginLayoutParams) view
                        .getLayoutParams();
                params.leftMargin = (int) value;
                params.rightMargin = (int) value;
                view.setLayoutParams(params);
            }
        });

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mInputLayout,
                "scaleX", 1f, 0.5f);
        set.setDuration(800);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.playTogether(animator, animator2);
        set.start();
        set.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                progress.setVisibility(View.VISIBLE);
                progressAnimator(progress);
                mInputLayout.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // TODO Auto-generated method stub
            }
        });

    }

    private void progressAnimator(final View view) {



        PropertyValuesHolder animator = PropertyValuesHolder.ofFloat("scaleX",
                0.5f, 1f);
        PropertyValuesHolder animator2 = PropertyValuesHolder.ofFloat("scaleY",
                0.5f, 1f);
        ObjectAnimator animator3 = ObjectAnimator.ofPropertyValuesHolder(view,
                animator, animator2);
        animator3.setDuration(2000);
        animator3.setInterpolator(new JellyInterpolator());
        animator3.start();
        animator3.addListener(new AnimatorListener(){

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //与后台连接
                FormBody.Builder builder1 = new FormBody.Builder();
                String use = username.getText().toString();
                FormBody formBody = builder1.add("username",username.getText().toString())
                        .add("pwd", password.getText().toString()).build();
                Request.Builder builder = new Request.Builder();
                Request request1 = builder.url("http://192.168.40.70:8080/PClubManager/loginForAndroid")
                        .post(formBody)
                        .build();
                exec(request1);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
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
        SharedPreferences.Editor editor=sp.edit();
        String name=username.getText().toString();
        editor.putString("username",username.getText().toString());
        editor.putString("password",password.getText().toString());
        editor.commit();
    }
}
