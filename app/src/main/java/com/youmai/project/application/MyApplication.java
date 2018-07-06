package com.youmai.project.application;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.google.gson.Gson;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
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

        //初始化地图
        SDKInitializer.initialize(getApplicationContext());

        //初始化讯飞语音
        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=55d54ce6");

        registerActivityLifecycleCallbacks(ActivitysLifecycle.getInstance());
    }
}
