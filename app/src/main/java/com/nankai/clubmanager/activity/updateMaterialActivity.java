package com.nankai.clubmanager.activity;

import android.app.Activity;
import android.os.Bundle;

import com.nankai.clubmanager.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.x;

/**
 * Created by Administrator on 2017/8/9.
 */
@ContentView(R.layout.update_material)
public class updateMaterialActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
    }
}
