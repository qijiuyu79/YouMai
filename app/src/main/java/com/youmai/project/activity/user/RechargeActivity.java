package com.youmai.project.activity.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.bean.PayResult;
import com.youmai.project.http.HandlerConstant;
import com.youmai.project.http.HttpMethod;
import com.youmai.project.utils.PayUtils;
import com.youmai.project.view.MeterailEditText;

import org.json.JSONObject;

/**
 * 充值
 * Created by Administrator on 2018/6/4 0004.
 */

public class RechargeActivity extends BaseActivity implements View.OnClickListener{

    private MeterailEditText etMoney;
    private ImageView imgWeixin,imgZhifu;
    private String payStr="WECHAT";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        initView();
        //注册广播
        registerReceiver();
    }


    /**
     * 初始化控件
     */
    private void initView() {
        TextView tvHead = (TextView) findViewById(R.id.tv_head);
        tvHead.setText("充值");
        etMoney=(MeterailEditText)findViewById(R.id.et_ar_money);
        etMoney.setInPutType(InputType.TYPE_CLASS_PHONE);
        etMoney.setFocusable(true);
        etMoney.setFocusableInTouchMode(true);
        imgWeixin=(ImageView)findViewById(R.id.img_abg_wei_select);
        imgZhifu=(ImageView)findViewById(R.id.img_abg_zhi_select);
        findViewById(R.id.lin_back).setOnClickListener(this);
        findViewById(R.id.rel_abg_weixin).setOnClickListener(this);
        findViewById(R.id.rel_abg_zhi).setOnClickListener(this);
        findViewById(R.id.tv_ar_recharge).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //微信支付
            case R.id.rel_abg_weixin:
                payStr="WECHAT";
                imgWeixin.setImageDrawable(getResources().getDrawable(R.mipmap.select_yes));
                imgZhifu.setImageDrawable(getResources().getDrawable(R.mipmap.select_no));
                break;
            //支付宝支付
            case R.id.rel_abg_zhi:
                payStr="ALIPAY";
                imgWeixin.setImageDrawable(getResources().getDrawable(R.mipmap.select_no));
                imgZhifu.setImageDrawable(getResources().getDrawable(R.mipmap.select_yes));
                break;
            case R.id.tv_ar_recharge:
                 final String money=etMoney.getText().toString().trim();
                 if(TextUtils.isEmpty(money)){
                     showMsg("请输入要充值的金额！");
                 }else if(Double.parseDouble(money)==0){
                     showMsg("金额不能小于0元！");
                 }else{
                     showProgress("充值中...",false);
                     HttpMethod.recharge(money,payStr,mHandler);
                 }
                 break;
            case R.id.lin_back:
                 finish();
                 break;
            default:
                break;

        }
    }


    private Handler mHandler=new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            clearTask();
            switch (msg.what){
                case HandlerConstant.RECHARGE_SUCCESS:
                    String message= (String) msg.obj;
                    if(TextUtils.isEmpty(message)){
                        return;
                    }
                    try {
                        final JSONObject jsonObject=new JSONObject(message);
                        if(jsonObject.getInt("code")==200){
                            switch (payStr){
                                case "WECHAT":
                                    PayUtils.getInstance(RechargeActivity.this).weipay(jsonObject.getString("data"),mHandler);
                                    break;
                                case "ALIPAY":
                                    PayUtils.getInstance(RechargeActivity.this).alippay(jsonObject.getString("data"),mHandler);
                                    break;
                                default:
                                    break;
                            }
                        }else{
                            showMsg(jsonObject.getString("msg"));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case 0x001:
                    PayResult payResult = new PayResult(msg.obj.toString());
                    String resultStatus = payResult.getResultStatus();
                    if (TextUtils.equals(resultStatus, "9000")) {
                        showMsg(getString(R.string.payment_success));
                        finish();
                    } else {
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            showMsg(getString(R.string.payment_confirming));
                        } else if (TextUtils.equals(resultStatus, "6001")) {
                            showMsg(getString(R.string.paymnet_canceled));
                        } else if(TextUtils.equals(resultStatus, "4000")) {
                            showMsg(getString(R.string.Please_install_alipay_client_first));
                        }else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            showMsg(getString(R.string.payment_failed));
                        }
                    }
                    break;
                case HandlerConstant.REQUST_ERROR:
                    showMsg(getString(R.string.http_error));
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * 注册广播
     */
    private void registerReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("PAY_ACTION");
        // 注册广播监听
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            final int type=intent.getIntExtra("type",0);
            if (action.equals("PAY_ACTION") && type==1) {
                RechargeActivity.this.finish();
            }
        }
    };


    @Override
    protected void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }
}
