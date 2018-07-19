package com.youmai.project.application;

import android.app.Application;
import android.text.TextUtils;

import com.baidu.mapapi.SDKInitializer;
import com.google.gson.Gson;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.youmai.project.bean.UserInfo;
import com.youmai.project.utils.ActivitysLifecycle;
import com.youmai.project.utils.SPUtil;

/**
 * Created by Administrator on 2018/1/18 0018.
 */

public class MyApplication extends Application {

    public static MyApplication application;
    public static Gson gson;
    public static SPUtil spUtil;
    public static UserInfo.UserInfoBean userInfoBean;
    public void onCreate() {
        super.onCreate();
        application=this;
        gson = new Gson();
        spUtil = SPUtil.getInstance(this);

        String str=spUtil.getString(SPUtil.USER_INFO);
        if(!TextUtils.isEmpty(str)) {
            userInfoBean = gson.fromJson(str, UserInfo.UserInfoBean.class);
        }

        //初始化地图
        SDKInitializer.initialize(getApplicationContext());

        //初始化讯飞语音
        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=55d54ce6");

        registerActivityLifecycleCallbacks(ActivitysLifecycle.getInstance());
    }
}
