package com.youmai.project.wxapi;


//import com.umeng.weixin.callback.WXCallbackActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.weixin.view.WXCallbackActivity;
import com.youmai.project.R;
import com.youmai.project.application.MyApplication;
import com.youmai.project.http.HttpConstant;
import com.youmai.project.utils.LogUtils;
import com.youmai.project.utils.Util;

public class WXEntryActivity extends WXCallbackActivity implements IWXAPIEventHandler {
    private IWXAPI api;
    // 用于存储获取到的用户code
    private String myCode = "";
    private Context mContext = MyApplication.application;
    public static final String share_success="net.edaibu.easywalking.SHARE_SUCCESS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, HttpConstant.APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
            if (resp instanceof SendAuth.Resp) {
                SendAuth.Resp newResp = (SendAuth.Resp) resp;
                // 由于微信请求授权会回调两次resp，添加条件判断防止重复请求接口
                // 本地code不为空，并且本地code和获取到的code一致，返回
                if (!TextUtils.isEmpty(myCode) && myCode.equals(newResp.code)) {
                    return;
                }
                //获取微信传回的code
                myCode = newResp.code;
                final Intent intent = new Intent("AUTH_ACTION");
                intent.putExtra("auth_code", myCode);
                sendBroadcast(intent);
            } else {
                if (!Util.isFastClick()) {
                    return;
                }
                Toast.makeText(mContext, mContext.getString(R.string.share_success), Toast.LENGTH_SHORT).show();
            }
        } else {
            //一定要加super，实现我们的方法，否则不能回调
            super.onResp(resp);
        }
        finish();
    }

}

