package com.nankai.clubmanager.extra;

import android.app.Application;

import org.xutils.x;

/**
 * Created by Miles on 2017/8/4.
 */

public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(true);
    }
}
