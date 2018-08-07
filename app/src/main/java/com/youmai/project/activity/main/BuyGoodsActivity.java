package com.youmai.project.activity.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.application.MyApplication;
import com.youmai.project.bean.GoodsBean;
import com.youmai.project.bean.PayResult;
import com.youmai.project.http.HandlerConstant;
import com.youmai.project.http.HttpMethod;
import com.youmai.project.utils.LogUtils;
import com.youmai.project.utils.PayUtils;
import com.youmai.project.utils.Util;
import org.json.JSONObject;

/**
 * 购买商品
 * Created by Administrator on 2018/5/25 0025.
 */

public class BuyGoodsActivity extends BaseActivity implements View.OnClickListener{

    private ImageView imgBalance,imgWeixin,imgZhifu;
    private GoodsBean goodsBean;
    private String payStr="BALANCE";
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_goods);
        initView();
        //注册广播
        registerReceiver();
    }


    /**
     * 初始化控件
     */
    private void initView(){
        TextView tvHead=(TextView)findViewById(R.id.tv_head);
        tvHead.setText("商品支付");
        ImageView imageView=(ImageView)findViewById(R.id.img_abg_goods);
        TextView tvContent=(TextView)findViewById(R.id.tv_abg_content);
        TextView tvMoney=(TextView)findViewById(R.id.tv_abg_money);
        TextView tvMoney2=(TextView)findViewById(R.id.tv_abg_money2);
        TextView tvBalance=(TextView)findViewById(R.id.tv_abg_balance);
        imgBalance=(ImageView)findViewById(R.id.img_abg_balance_select);
        imgWeixin=(ImageView)findViewById(R.id.img_abg_wei_select);
        imgZhifu=(ImageView)findViewById(R.id.img_abg_zhi_select);
        findViewById(R.id.lin_back).setOnClickListener(this);
        findViewById(R.id.rel_abg_balance).setOnClickListener(this);
        findViewById(R.id.rel_abg_weixin).setOnClickListener(this);
        findViewById(R.id.rel_abg_zhi).setOnClickListener(this);
        findViewById(R.id.tv_abg_buy).setOnClickListener(this);
        final Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            goodsBean= (GoodsBean) bundle.getSerializable("goodsBean");
            if(null==goodsBean){
                return;
            }
            if(MyApplication.userInfoBean!=null){
                tvBalance.setText("(余额¥"+Util.setDouble(MyApplication.userInfoBean.getBalance()/100)+")");
            }
            Glide.with(mContext).load(goodsBean.getImgList().get(0)).error(R.mipmap.icon).into(imageView);
            tvContent.setText(goodsBean.getDescription());
            tvMoney.setText("¥"+ Util.setDouble(goodsBean.getPresentPrice()/100));
            tvMoney2.setText("实付款："+Util.setDouble(goodsBean.getPresentPrice()/100)+"元");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //余额支付
            case R.id.rel_abg_balance:
                 payStr="BALANCE";
                 imgBalance.setImageDrawable(getResources().getDrawable(R.mipmap.select_yes));
                 imgWeixin.setImageDrawable(getResources().getDrawable(R.mipmap.select_no));
                 imgZhifu.setImageDrawable(getResources().getDrawable(R.mipmap.select_no));
                 break;
            //微信支付
            case R.id.rel_abg_weixin:
                 payStr="WECHAT";
                 imgBalance.setImageDrawable(getResources().getDrawable(R.mipmap.select_no));
                 imgWeixin.setImageDrawable(getResources().getDrawable(R.mipmap.select_yes));
                 imgZhifu.setImageDrawable(getResources().getDrawable(R.mipmap.select_no));
                 break;
            //支付宝支付
            case R.id.rel_abg_zhi:
                 payStr="ALIPAY";
                 imgBalance.setImageDrawable(getResources().getDrawable(R.mipmap.select_no));
                 imgWeixin.setImageDrawable(getResources().getDrawable(R.mipmap.select_no));
                 imgZhifu.setImageDrawable(getResources().getDrawable(R.mipmap.select_yes));
                 break;
            //支付
            case R.id.tv_abg_buy:
                 showProgress("购买中...",false);
                 HttpMethod.buy(payStr,goodsBean.getId(),mHandler);
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
                case HandlerConstant.BUY_SUCCESS:
                     String message= (String) msg.obj;
                     if(TextUtils.isEmpty(message)){
                        return;
                     }
                     try {
                        final JSONObject jsonObject=new JSONObject(message);
                        if(jsonObject.getInt("code")==200){
                            switch (payStr){
                                case "BALANCE":
                                     showMsg("购买成功！");
                                     finish();
                                     break;
                                case "WECHAT":
                                     PayUtils.getInstance(BuyGoodsActivity.this).weipay(jsonObject.getString("data"),mHandler);
                                     break;
                                case "ALIPAY":
                                     PayUtils.getInstance(BuyGoodsActivity.this).alippay(jsonObject.getString("data"),mHandler);
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
                BuyGoodsActivity.this.finish();
            }
        }
    };


    @Override
    protected void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }
}
