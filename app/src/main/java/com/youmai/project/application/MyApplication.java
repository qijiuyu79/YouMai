package com.youmai.project.application;

import android.app.Application;
import android.text.TextUtils;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.google.gson.Gson;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.youmai.project.bean.UserInfo;
import com.youmai.project.http.HttpConstant;
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

        //初始化友盟
        UMShareAPI.get(this);
        com.umeng.socialize.Config.DEBUG = true;

        String str=spUtil.getString(SPUtil.USER_INFO);
        if(!TextUtils.isEmpty(str)) {
            userInfoBean = gson.fromJson(str, UserInfo.UserInfoBean.class);
        }

        //初始化地图
        SDKInitializer.initialize(getApplicationContext());
        SDKInitializer.setCoordType(CoordType.BD09LL);

        //初始化讯飞语音
        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=55d54ce6");

        registerActivityLifecycleCallbacks(ActivitysLifecycle.getInstance());
    }


    /**
     * 设置分享
     */

    {
        //微信
        PlatformConfig.setWeixin(HttpConstant.APP_ID, "2c663c8c57a04d9063bc08fb941415d2");
        //新浪微博
//        PlatformConfig.setSinaWeibo("620570357", "d7bed770e0574a2d92f083883179edc8", "http://sns.whalecloud.com");
//        /*最新的版本需要加上这个回调地址，可以在微博开放平台申请的应用获取，必须要有*/
//        //QQ
//        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");
    }
}
