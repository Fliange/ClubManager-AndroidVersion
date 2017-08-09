package com.nankai.clubmanager.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

import com.nankai.clubmanager.R;

public class FirstWelcomeActivity extends Activity {

    private Button start_btn;
    //private WelcomeViewPager welcomeViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_welcome);
    }
}
