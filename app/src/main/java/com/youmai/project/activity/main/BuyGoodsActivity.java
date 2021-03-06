package com.youmai.project.activity.main;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.activity.user.MyAddressActivity;
import com.youmai.project.adapter.MyAddressAdapter;
import com.youmai.project.application.MyApplication;
import com.youmai.project.bean.Address;
import com.youmai.project.bean.GoodsBean;
import com.youmai.project.bean.PayResult;
import com.youmai.project.http.HandlerConstant;
import com.youmai.project.http.HttpMethod;
import com.youmai.project.utils.LogUtils;
import com.youmai.project.utils.PayUtils;
import com.youmai.project.utils.Util;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * 购买商品
 * Created by Administrator on 2018/5/25 0025.
 */

public class BuyGoodsActivity extends BaseActivity implements View.OnClickListener{

    private ImageView imgBalance,imgWeixin,imgZhifu;
    private ImageView imgX1,imgX2,imgX3,imgX4,imgX5;
    private TextView tvZFBIntegral,tvWXIntegral,tvAddress,tvMobile;
    private GoodsBean goodsBean;
    private String payStr="BALANCE";
    //购买成功后的广播
    public final static String ACTION_GOODS_PAYSUCCESS = "net.youmai.adminapp.action.goods.paysuccess";
    //订单id
    private String orderId;
    //收获地址对象
    private Address.AddressBean addressBean;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_goods);
        initView();
        //查询地址列表数据
        getAddressList();
        //注册广播
        registerReceiver();
    }


    /**
     * 初始化控件
     */
    private void initView(){
        TextView tvHead=(TextView)findViewById(R.id.tv_head);
        tvHead.setText("购买宝贝");
        TextView tvNickName=(TextView)findViewById(R.id.tv_abg_name);
        ImageView imageView=(ImageView)findViewById(R.id.img_abg_goods);
        TextView tvContent=(TextView)findViewById(R.id.tv_abg_content);
        TextView tvMoney=(TextView)findViewById(R.id.tv_abg_money);
        TextView tvMoney2=(TextView)findViewById(R.id.tv_abg_money2);
        TextView tvBalance=(TextView)findViewById(R.id.tv_abg_balance);
        imgBalance=(ImageView)findViewById(R.id.img_abg_balance_select);
        imgWeixin=(ImageView)findViewById(R.id.img_abg_wei_select);
        imgZhifu=(ImageView)findViewById(R.id.img_abg_zhi_select);
        imgX1=(ImageView)findViewById(R.id.img_au_x1);
        imgX2=(ImageView)findViewById(R.id.img_au_x2);
        imgX3=(ImageView)findViewById(R.id.img_au_x3);
        imgX4=(ImageView)findViewById(R.id.img_au_x4);
        imgX5=(ImageView)findViewById(R.id.img_au_x5);
        tvZFBIntegral=(TextView)findViewById(R.id.tv_zfb_integral);
        tvWXIntegral=(TextView)findViewById(R.id.tv_wx_integral);
        tvAddress=(TextView)findViewById(R.id.tv_address);
        tvMobile=(TextView)findViewById(R.id.tv_mobile);
        findViewById(R.id.rel_address).setOnClickListener(this);
        findViewById(R.id.lin_back).setOnClickListener(this);
        findViewById(R.id.rel_abg_balance).setOnClickListener(this);
        findViewById(R.id.rel_abg_weixin).setOnClickListener(this);
        findViewById(R.id.rel_abg_zhi).setOnClickListener(this);
        findViewById(R.id.tv_abg_buy).setOnClickListener(this);
        goodsBean= (GoodsBean) getIntent().getSerializableExtra("goodsBean");
        if(null==goodsBean){
            return;
        }
        if(MyApplication.userInfoBean!=null){
            tvBalance.setText("(余额¥"+Util.setDouble(MyApplication.userInfoBean.getBalance()/100)+")");
        }
        tvNickName.setText(goodsBean.getNickname());
        //设置星级
        setXing(goodsBean.getCreditLevel());
        if(goodsBean.getImgList().size()>0){
            Glide.with(mContext).load(goodsBean.getImgList().get(0)).override(116,116).centerCrop().error(R.mipmap.icon).into(imageView);
        }
        tvContent.setText(goodsBean.getDescription());
        tvMoney.setText("¥"+ Util.setDouble(goodsBean.getPresentPrice()/100));
        tvMoney2.setText("实付款："+ Html.fromHtml("<font color='#FF4181'>"+Util.setDouble(goodsBean.getPresentPrice()/100)+"元</font>"));
        tvZFBIntegral.setText("(赠送"+goodsBean.getPresentPrice()/100+"积分)");
        tvWXIntegral.setText("(赠送"+goodsBean.getPresentPrice()/100+"积分)");
    }


    /**
     * 设置星级
     */
    private List<ImageView> imgList=new ArrayList<>();
    private void setXing(int index){
        imgList.add(imgX1);
        imgList.add(imgX2);
        imgList.add(imgX3);
        imgList.add(imgX4);
        imgList.add(imgX5);
        for (int i=0;i<imgList.size();i++){
            if(i<index){
                imgList.get(i).setImageDrawable(getResources().getDrawable(R.mipmap.yes_hselect_x));
            }else{
                imgList.get(i).setImageDrawable(getResources().getDrawable(R.mipmap.no_hselect_x));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //选择收货地址
            case R.id.rel_address:
                 Intent intent=new Intent(mContext, MyAddressActivity.class);
                 intent.putExtra("type",1);
                 startActivityForResult(intent,1);
                 break;
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
                 if(null==addressBean){
                     showMsg("请选择收货地址！");
                     return;
                 }
                 showProgress("购买中...");
                 HttpMethod.buy(payStr,goodsBean.getId(),String.valueOf(addressBean.getIndex()),mHandler);
                 break;
            case R.id.lin_back:
                 finish();
                 break;
            default:
                break;
        }
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler=new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            clearTask();
            switch (msg.what){
                //获取地址列表
                case HandlerConstant.GET_ADDRESS_LIST_SUCCESS:
                     Address address= (Address) msg.obj;
                     if(null==address){
                        break;
                     }
                     if(address.isSussess() && address.getData().size()>0){
                         addressBean=address.getData().get(0);
                         if(null!=addressBean){
                             tvAddress.setText(addressBean.getAddress());
                             tvMobile.setText(addressBean.getName()+"    "+addressBean.getMobile());
                             tvMobile.setVisibility(View.VISIBLE);
                         }
                     }
                     break;
                //购买
                case HandlerConstant.BUY_SUCCESS:
                     String message= (String) msg.obj;
                     if(TextUtils.isEmpty(message)){
                        return;
                     }
                     try {
                        final JSONObject jsonObject=new JSONObject(message);
                        if(jsonObject.getInt("code")==200){
                            final JSONObject jsonObject1=new JSONObject(jsonObject.getString("data"));
                            orderId=jsonObject1.getString("orderId");
                            switch (payStr){
                                case "BALANCE":
                                     paySuccess();
                                     break;
                                case "WECHAT":
                                     PayUtils.getInstance(BuyGoodsActivity.this).weipay(jsonObject1.getString("payData"),mHandler);
                                     break;
                                case "ALIPAY":
                                     PayUtils.getInstance(BuyGoodsActivity.this).alippay(jsonObject1.getString("payData"),mHandler);
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
                        paySuccess();
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
                        //取消订单接口
                        cancleOrder();
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
            if (action.equals("PAY_ACTION")) {
                if(type==1){
                    paySuccess();
                }else{
                    //取消订单接口
                    cancleOrder();
                }
            }
        }
    };


    /**
     * 取消订单接口
     */
    private void cancleOrder(){
        HttpMethod.setOrderCancle(orderId,mHandler);
    }


    /**
     * 支付成功后跳转
     */
    private void paySuccess(){
        Intent intent=new Intent(mContext,BuyGoodSuccessActivity.class);
        intent.putExtra("goodsBean",goodsBean);
        intent.putExtra("payType",payStr);
        intent.putExtra("orderId",orderId);
        intent.putExtra("addressBean",addressBean);
        startActivity(intent);
        //发送广播
        Intent broadcastIntent=new Intent(ACTION_GOODS_PAYSUCCESS);
        broadcastIntent.putExtra("goodsBean",goodsBean);
        sendBroadcast(broadcastIntent);
        BuyGoodsActivity.this.finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==1){
            addressBean= (Address.AddressBean) data.getSerializableExtra("addressBean");
            if(null!=addressBean){
                tvAddress.setText(addressBean.getAddress());
                tvMobile.setText(addressBean.getName()+"    "+addressBean.getMobile());
                tvMobile.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 查询地址列表数据
     */
    private void getAddressList(){
        showProgress("数据加载中");
        HttpMethod.getAddressList(mHandler);
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);
        removeHandler(mHandler);
        super.onDestroy();
    }
}
