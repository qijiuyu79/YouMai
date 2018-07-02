package com.youmai.project.Application;

import android.app.Application;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.google.gson.Gson;
import com.youmai.project.utils.ActivitysLifecycle;
import com.youmai.project.utils.SPUtil;

/**
 * Created by Administrator on 2018/1/18 0018.
 */

public class MyApplication extends Application {

    public static MyApplication application;
    public static Gson gson;
    public static SPUtil spUtil;
    public void onCreate() {
        super.onCreate();
        application=this;
        gson = new Gson();
        spUtil = SPUtil.getInstance(this);

        SDKInitializer.initialize(getApplicationContext());

        registerActivityLifecycleCallbacks(ActivitysLifecycle.getInstance());
    }
}
