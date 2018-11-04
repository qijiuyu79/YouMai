package com.youmai.project.activity.order;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.activity.map.RoutePlanActivity;
import com.youmai.project.bean.GoodsBean;
import com.youmai.project.utils.StatusBarUtils;
import com.youmai.project.utils.SystemBarTintManager;
import com.youmai.project.utils.Util;
import com.youmai.project.utils.ZXingUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单详情
 */
public class OrderDetailsActivity  extends BaseActivity implements View.OnClickListener{

    private TextView tvUserName,tvContent,tvMoney1,tvPayType,tvMoney2;
    private ImageView imgX1,imgX2,imgX3,imgX4,imgX5,imgGood,imgOrder,imgType;
    private GoodsBean goodsBean;
    private List<ImageView> imgList=new ArrayList<>();
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        StatusBarUtils.transparencyBar(this);
        setContentView(R.layout.activity_order_details);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //系统版本大于19
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.color_FF4081);
        initView();
        showData();
        createQR();
    }


    /**
     * 初始化控件
     */
    private void initView(){
        tvUserName=(TextView)findViewById(R.id.tv_aod_name);
        imgX1=(ImageView)findViewById(R.id.img_ri_x1);
        imgX2=(ImageView)findViewById(R.id.img_ri_x2);
        imgX3=(ImageView)findViewById(R.id.img_ri_x3);
        imgX4=(ImageView)findViewById(R.id.img_ri_x4);
        imgX5=(ImageView)findViewById(R.id.img_ri_x5);
        imgGood=(ImageView)findViewById(R.id.img_psi_icon);
        tvContent=(TextView)findViewById(R.id.tv_psi_des);
        tvMoney1=(TextView)findViewById(R.id.tv_oi_money);
        tvPayType=(TextView)findViewById(R.id.tv_abgs_paytype);
        tvMoney2=(TextView)findViewById(R.id.tv_abg_money2);
        imgOrder=(ImageView)findViewById(R.id.img_order);
        imgType=(ImageView)findViewById(R.id.img_oi_type);
        findViewById(R.id.lin_abgs_contact).setOnClickListener(this);
        findViewById(R.id.lin_abgs_location).setOnClickListener(this);
        findViewById(R.id.lin_back).setOnClickListener(this);
    }


    /**
     * 展示数据
     */
    private void showData(){
        goodsBean= (GoodsBean) getIntent().getSerializableExtra("goodsBean");
        if(null==goodsBean){
            return;
        }
        tvUserName.setText(goodsBean.getNickname());
        //设置星级
        setXing(goodsBean.getCreditLevel());
        Glide.with(mContext).load(goodsBean.getImgList().get(0)).override(102,102).centerCrop().error(R.mipmap.icon).into(imgGood);
        tvContent.setText(goodsBean.getDescription());
        tvMoney1.setText(Util.setDouble(goodsBean.getPresentPrice()/100));
        tvMoney2.setText("¥"+Util.setDouble(goodsBean.getPresentPrice()/100));
        switch (goodsBean.getPayment()){
            case 0:
                tvPayType.setText("余额支付");
                 break;
            case 1:
                tvPayType.setText("微信支付");
                break;
            case 2:
                tvPayType.setText("支付宝支付");
                break;
            default:
                break;
        }
        switch (goodsBean.getStated()){
            case 1:
                imgType.setImageDrawable(getResources().getDrawable(R.mipmap.yizhifu));
                break;
            case 2:
                imgType.setImageDrawable(getResources().getDrawable(R.mipmap.yiwancheng));
                break;
            case 4:
                imgType.setImageDrawable(getResources().getDrawable(R.mipmap.yiquxiao));
                break;
            default:
                break;
        }

        
    }


    /**
     * 设置星级
     */
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


    /**
     * 生成二维码
     */
    private Bitmap bitmap;
    private void createQR(){
        bitmap= ZXingUtils.createQRImage(goodsBean.getQrCodeText(),183,183);
        imgOrder.setImageBitmap(bitmap);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lin_abgs_contact:
                 Intent intent2 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + goodsBean.getMobile()));
                 startActivity(intent2);
                 break;
            case R.id.lin_abgs_location:
                 Intent intent=new Intent(mContext, RoutePlanActivity.class);
                 intent.putExtra("latitude",goodsBean.getLatitude());
                 intent.putExtra("longtitude",goodsBean.getLongitude());
                 startActivity(intent);
                 break;
            case R.id.lin_back:
                 finish();
                 break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null!=bitmap){
            bitmap.recycle();;
            bitmap=null;
        }
    }

}
