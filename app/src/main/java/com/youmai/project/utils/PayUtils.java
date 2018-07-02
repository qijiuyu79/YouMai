package com.youmai.project.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.youmai.project.Application.MyApplication;
import com.youmai.project.bean.EAppWinXinPayResVO;
import com.youmai.project.http.HttpConstant;
import com.youmai.project.http.HttpMethod;

/**支付工具类
 * Created by Administrator on 2017/1/13 0013.
 */

public class PayUtils {
    private static PayUtils payUtils;
    private static Activity mActivity;
    private static IWXAPI api;

    /**
     * 工具类
     * @return
     */
    public static PayUtils getInstance(Activity activity){
        if(payUtils==null){
            payUtils=new PayUtils();
        }
        mActivity=activity;
        return payUtils;
    }

    /**
     * 支付宝支付
     * @param handler
     */
    public  void alippay(final String paystr,final Handler handler){
        Runnable payRunnable = new Runnable() {
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(mActivity);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(paystr, true);
                Message msg = new Message();
                msg.what = 0x001;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }


    /**
     * 微信支付
     * @param paystr
     * @param handler
     */
    public void weipay(final String paystr,final Handler handler){
        EAppWinXinPayResVO wxpay = MyApplication.gson.fromJson(paystr, EAppWinXinPayResVO.class);
        wxpay(wxpay);
    }

    //微信支付
    public static void wxpay(EAppWinXinPayResVO wxpayresult){
        if(wxpayresult==null){
            return;
        }
        api= WXAPIFactory.createWXAPI(mActivity, HttpConstant.APP_ID);
        if( !api.isWXAppInstalled()){
            Toast.makeText(mActivity, "请先安装微信客户端！",Toast.LENGTH_SHORT).show();
            return;
        }
        // 将该app注册到微信
        api.registerApp(HttpConstant.APP_ID);
        PayReq req = new PayReq();
        req.appId			= HttpConstant.APP_ID;
        req.partnerId		= HttpConstant.MCH_ID;
        req.prepayId		= wxpayresult.getPrepayId();
        req.nonceStr		= wxpayresult.getNonceStr();
        req.timeStamp		= wxpayresult.getTimeStamp();
//		req.packageValue	= wxpayresult.getPackageValue();
        req.packageValue	= "Sign=WXPay";
        req.sign			= wxpayresult.getSign();
//		req.extData			= "app data"; // optional
        api.sendReq(req);
    }
}
