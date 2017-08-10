package com.nankai.clubmanager.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.nankai.clubmanager.R;

public class MyInformationActivity extends Activity {
    private TextView sureupdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_myinformation);
        sureupdate=(TextView) findViewById(R.id.update_my_sure);
        sureupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
