package com.nankai.clubmanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.nankai.clubmanager.R;

public class FirstWelcomeActivity extends Activity {

    private TextView start_btn;
    //private WelcomeViewPager welcomeViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_welcome);
        start_btn=(TextView) findViewById(R.id.btn_start);
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FirstWelcomeActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
