package com.youmai.project.activity.main;

import android.content.Intent;
import android.os.Bundle;
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
import com.youmai.project.utils.Util;

/**
 * 支付成功
 * Created by Administrator on 2018/5/25 0025.
 */

public class BuyGoodSuccessActivity extends BaseActivity implements View.OnClickListener{

    private GoodsBean goodsBean;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_goods_success);
        initView();
    }


    /**
     * 初始化控件
     */
    private void initView(){
        TextView tvHead=(TextView)findViewById(R.id.tv_head);
        tvHead.setText("购买宝贝");
        ImageView imageView=(ImageView)findViewById(R.id.img_abg_goods);
        TextView tvContent=(TextView)findViewById(R.id.tv_abg_content);
        TextView tvMoney=(TextView)findViewById(R.id.tv_abg_money);
        TextView tvPayType=(TextView)findViewById(R.id.tv_abgs_paytype);
        TextView tvMoney2=(TextView)findViewById(R.id.tv_abg_money2);
        findViewById(R.id.lin_abgs_order).setOnClickListener(this);
        findViewById(R.id.lin_back).setOnClickListener(this);
        final Bundle bundle=getIntent().getExtras();
        if(null!=bundle){
            goodsBean= (GoodsBean) bundle.getSerializable("goodsBean");
            final String payType=bundle.getString("payType");
            if(null==goodsBean){
                return;
            }
            if(goodsBean.getImgList().size()>0){
                Glide.with(mContext).load(goodsBean.getImgList().get(0)).error(R.mipmap.icon).into(imageView);
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //查看订单
            case R.id.lin_abgs_order:
                 Intent intent=new Intent(BuyGoodSuccessActivity.this,OrderActivity.class);
                 intent.putExtra("type",0);
                 startActivity(intent);
                 break;
            //联系卖家
            case R.id.lin_abgs_contact:
                 break;
            case R.id.lin_back:
                 finish();
                 break;
            default:
                break;
        }
    }


}
