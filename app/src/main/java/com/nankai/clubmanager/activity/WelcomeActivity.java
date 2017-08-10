package com.nankai.clubmanager.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.nankai.clubmanager.R;

import java.io.OutputStream;

public class WelcomeActivity extends Activity {
    private Button jump;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private OutputStream os;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 判断是否是第一次开启应用
//        boolean isFirstOpen = SharedPreferencesUtil.getBoolean(this, SharedPreferencesUtil.FIRST_OPEN, true);
//        // 如果是第一次启动，则先进入功能引导页
//        if (isFirstOpen) {
//            Intent intent = new Intent(this, FirstWelcomeActivity.class);
//            startActivity(intent);
//            finish();
//            return;
//        }
        preferences = getSharedPreferences("phone", Context.MODE_PRIVATE);
        //判断是不是首次登录，
        if (preferences.getBoolean("firststart", true)) {
            editor = preferences.edit();
            //将登录标志位设置为false，下次登录时不在显示首次登录界面
            editor.putBoolean("firststart", false);
            editor.commit();
            Intent intent = new Intent(WelcomeActivity.this,FirstWelcomeActivity.class);
            startActivity(intent);
            finish();
        }


        // 如果不是第一次启动app，则正常显示启动屏
        setContentView(R.layout.activity_welcome);
        //ButterKnife.bind(this);

        jump = (Button) findViewById(R.id.jump_click);

        final Runnable myRun=new Runnable(){
            @Override
            public void run(){
                //recLen--;
                //jump.setText(recLen+"秒 点击跳过");
                Intent in2 = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(in2);
                finish();
            }
        };
        final Handler handler=new Handler();
        //使用handler对象来定时启动线程运行
        handler.postDelayed(myRun,5000);
        //time.start();
        jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //移除handler延迟加载里面的线程
                handler.removeCallbacks(myRun);
                Intent in2 = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(in2);
                finish();
            }
        });
        new CountDownTimer(5000,1000){
            public void onTick(long millisUtilFinish){
                jump.setText((millisUtilFinish/1000+1)+"秒 跳过");
            }
            public void onFinish(){
                jump.setText("1秒 跳过");
            }
        }.start();
    }
}
