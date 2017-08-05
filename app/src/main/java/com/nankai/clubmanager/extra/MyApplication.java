package com.nankai.clubmanager.extra;

import org.litepal.LitePalApplication;
import org.xutils.x;

/**
 * Created by winnie on 2017/7/29.
 */

public class MyApplication extends LitePalApplication {

    private String name;
    //http://localhost:8080/

    @Override
    public void onCreate() {
        super.onCreate();
        setName("hello");
        x.Ext.init(this);//初始化xutils
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
