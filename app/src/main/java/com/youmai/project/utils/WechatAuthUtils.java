package com.youmai.project.utils;

import android.app.Activity;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.youmai.project.R;
import com.youmai.project.http.HttpConstant;

/**
 * 微信授权登录
 */

public class WechatAuthUtils {
    private static WechatAuthUtils wechatAuthUtils;
    private static Activity mActivity;
    private static IWXAPI api;

    /**
     * 工具类
     *
     * @return
     */
    public static WechatAuthUtils getInstance(Activity activity) {
        if (wechatAuthUtils == null) {
            wechatAuthUtils = new WechatAuthUtils();
        }
        mActivity = activity;
        return wechatAuthUtils;
    }

    /**
     * 请求微信授权
     */
    public void wechatAuth() {
        api = WXAPIFactory.createWXAPI(mActivity, HttpConstant.APP_ID);
        if (!api.isWXAppInstalled()) {
            Toast.makeText(mActivity, "请先安装微信客户端！", Toast.LENGTH_SHORT).show();
            return;
        }
        // 将该app注册到微信
        api.registerApp(HttpConstant.APP_ID);
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "zxbike_wx_auth";
        api.sendReq(req);
    }
}
