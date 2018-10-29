package com.youmai.project.activity.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.activity.TabActivity;
import com.youmai.project.activity.center.CenterActivity;
import com.youmai.project.activity.order.OrderActivity;
import com.youmai.project.bean.GoodsBean;
import com.youmai.project.http.HandlerConstant;
import com.youmai.project.http.HttpMethod;
import com.youmai.project.utils.Util;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 支付成功
 * Created by Administrator on 2018/5/25 0025.
 */

public class BuyGoodSuccessActivity extends BaseActivity implements View.OnClickListener{

    private ImageView imageView,imgX1,imgX2,imgX3,imgX4,imgX5;
    private TextView tvNickName,tvContent,tvMoney,tvPayType,tvMoney2;
    private GoodsBean goodsBean;
    //订单id和支付方式
    private String orderId,payType;
    //卖家的手机号码
    private String mobile;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_goods_success);
        initView();
        showData();
        //查询订单详情
        HttpMethod.getOrderDetails(orderId,mHandler);
    }


    /**
     * 初始化控件
     */
    private void initView(){
        TextView tvHead=(TextView)findViewById(R.id.tv_head);
        tvHead.setText("购买宝贝");
        imageView=(ImageView)findViewById(R.id.img_abg_goods);
        tvContent=(TextView)findViewById(R.id.tv_abg_content);
        tvMoney=(TextView)findViewById(R.id.tv_abg_money);
        tvPayType=(TextView)findViewById(R.id.tv_abgs_paytype);
        tvMoney2=(TextView)findViewById(R.id.tv_abg_money2);
        tvNickName=(TextView)findViewById(R.id.tv_abg_name);
        imgX1=(ImageView)findViewById(R.id.img_au_x1);
        imgX2=(ImageView)findViewById(R.id.img_au_x2);
        imgX3=(ImageView)findViewById(R.id.img_au_x3);
        imgX4=(ImageView)findViewById(R.id.img_au_x4);
        imgX5=(ImageView)findViewById(R.id.img_au_x5);
        findViewById(R.id.lin_abgs_order).setOnClickListener(this);
        findViewById(R.id.lin_abgs_contact).setOnClickListener(this);
        findViewById(R.id.lin_back).setOnClickListener(this);
        final Bundle bundle=getIntent().getExtras();
        if(null!=bundle){
            goodsBean= (GoodsBean) bundle.getSerializable("goodsBean");
            payType=bundle.getString("payType");
            orderId=bundle.getString("orderId");
        }
    }


    private Handler mHandler=new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what== HandlerConstant.GET_ORDER_DETAILS_SUCCESS){
                final String message=msg.obj.toString();
                try {
                    final JSONObject jsonObject=new JSONObject(message);
                    if(jsonObject.getInt("code")!=200){
                        return;
                    }
                    final JSONObject jsonObject1=new JSONObject(jsonObject.getString("seller"));
                    mobile=jsonObject1.getString("mobile");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    };


    /**
     * 显示商品数据
     */
    private void showData(){
        if(null==goodsBean){
            return;
        }
        tvNickName.setText(goodsBean.getNickname());
        //设置星级
        setXing(goodsBean.getCreditLevel());
        if(goodsBean.getImgList().size()>0){
            Glide.with(mContext).load(goodsBean.getImgList().get(0)).override(116,116).centerCrop().error(R.mipmap.icon).into(imageView);
        }
        tvContent.setText(goodsBean.getDescription());
        tvMoney.setText("¥"+ Util.setDouble(goodsBean.getPresentPrice()/100));
        tvMoney2.setText("¥"+ Util.setDouble(goodsBean.getPresentPrice()/100));
        switch (payType){
            case "BALANCE":
                tvPayType.setText("余额支付");
                break;
            case "WECHAT":
                tvPayType.setText("微信支付");
                break;
            case "ALIPAY":
                tvPayType.setText("支付宝支付");
                break;
            default:
                break;
        }
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
                imgList.get(i).setImageDrawable(getResources().getDrawable(R.mipmap.yes_select_x));
            }else{
                imgList.get(i).setImageDrawable(getResources().getDrawable(R.mipmap.no_select_x));
            }
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent=new Intent();
        switch (v.getId()){
            //查看订单
            case R.id.lin_abgs_order:
                 intent.setClass(BuyGoodSuccessActivity.this,OrderActivity.class);
                 intent.putExtra("type",0);
                 startActivity(intent);
                 break;
            //联系卖家
            case R.id.lin_abgs_contact:
                  Intent intent2 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobile));
                  startActivity(intent2);
                 break;
            case R.id.lin_back:
                 finish();
                 break;
            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeHandler(mHandler);
    }

}
