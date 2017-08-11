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
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

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

    //第三方登录
    private static final String TAG = "ThirdAppActivity";
    private static final String APP_ID = "1106344422";//官方获取的APPID
    private Tencent mTencent;
    private BaseUiListener mIUiListener;
    private UserInfo mUserInfo;

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
                    finish();
                    try {
                        JSONObject obj=new JSONObject(result);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("position",obj.getString("position"));//把职位存起来
                        editor.putInt("department",obj.getInt("department"));//把所在的部门存起来
                        editor.putBoolean("status",true);//登录的状态已经登录
                        editor.putString("name",obj.getString("name"));
                        editor.putString("dptName",obj.getString("dptName"));
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
        //传入参数APPID和全局Context上下文
        mTencent = Tencent.createInstance(APP_ID,LoginActivity.this.getApplicationContext());

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

    //第三方登录
    /**
     * 自定义监听器实现IUiListener接口后，需要实现的3个方法
     * onComplete完成 onError错误 onCancel取消
     */
    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            Toast.makeText(LoginActivity.this, "授权成功", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "response:" + response);
            JSONObject obj = (JSONObject) response;
            Log.i("登录结果-------------",obj.toString());
            try {
                String openID = obj.getString("openid");
                String accessToken = obj.getString("access_token");
                String expires = obj.getString("expires_in");
                mTencent.setOpenId(openID);
                mTencent.setAccessToken(accessToken,expires);
                QQToken qqToken = mTencent.getQQToken();
                mUserInfo = new UserInfo(getApplicationContext(),qqToken);
                mUserInfo.getUserInfo(new IUiListener() {
                    @Override
                    public void onComplete(Object response) {
                        Log.e(TAG,"登录成功"+response.toString());
                        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(UiError uiError) {
                        Log.e(TAG,"登录失败"+uiError.toString());
                        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onCancel() {
                        Log.e(TAG,"登录取消");
                        Intent intent=new Intent(LoginActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError uiError) {
            Toast.makeText(LoginActivity.this, "授权失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(LoginActivity.this, "授权取消", Toast.LENGTH_SHORT).show();
        }

    }
    @Event(value = {R.id.third_app_login},type = View.OnClickListener.class)
    private void thirdAppLogin(View view){
        /**通过这句代码，SDK实现了QQ的登录，这个方法有三个参数，第一个参数是context上下文，第二个参数SCOPO 是一个String类型的字符串，表示一些权限
         官方文档中的说明：应用需要获得哪些API的权限，由“，”分隔。例如：SCOPE = “get_user_info,add_t”；所有权限用“all”
         第三个参数，是一个事件监听器，IUiListener接口的实例，这里用的是该接口的实现类 */
        mIUiListener = new BaseUiListener();
        //all表示获取所有权限
        mTencent.login(LoginActivity.this,"all", mIUiListener);
    }

    /**
     * 在调用Login的Activity或者Fragment中重写onActivityResult方法
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constants.REQUEST_LOGIN){
            Tencent.onActivityResultData(requestCode,resultCode,data,mIUiListener);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
