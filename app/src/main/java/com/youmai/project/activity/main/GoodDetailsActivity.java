package com.youmai.project.activity.main;

import android.content.Intent;
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
import com.youmai.project.application.MyApplication;
import com.youmai.project.bean.GoodsBean;
import com.youmai.project.utils.LogUtils;
import com.youmai.project.utils.StatusBarUtils;
import com.youmai.project.utils.SystemBarTintManager;
import com.youmai.project.utils.Util;

/**
 * 宝贝详情
 */
public class GoodDetailsActivity extends BaseActivity implements View.OnClickListener{

    private GoodsBean goodsBean;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        StatusBarUtils.transparencyBar(this);
        setContentView(R.layout.activity_good_details);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //系统版本大于19
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.color_FF4081);
        initView();
    }


    /**
     * 初始化控件
     */
    private void initView(){
        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            goodsBean= (GoodsBean) bundle.getSerializable("goodsBean");
        }
        TextView tvNickName=(TextView)findViewById(R.id.tv_agd_nickName);
        ImageView imageView=(ImageView)findViewById(R.id.img_agd_img);
        TextView tvCount=(TextView)findViewById(R.id.tv_agd_count);
        TextView tvContent=(TextView)findViewById(R.id.tv_agd_content);
        TextView tvPresentPrice=(TextView)findViewById(R.id.tv_agd_presentPrice);
        TextView tvAddress=(TextView)findViewById(R.id.tv_agd_address);
        tvNickName.setText(goodsBean.getNickname());
        if(null!=goodsBean.getImgList() && goodsBean.getImgList().size()>0){
            Glide.with(mContext).load(goodsBean.getImgList().get(0)).error(R.mipmap.icon).into(imageView);
        }
        tvCount.setText("1/"+goodsBean.getImgList().size());
        tvContent.setText(goodsBean.getDescription());
        tvPresentPrice.setText("现价："+ Util.setDouble(goodsBean.getPresentPrice()/100));
        tvAddress.setText(goodsBean.getAddress());

        imageView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_agd_img:
                 Intent intent=new Intent(mContext,ShowImgActivity.class);
                 intent.putExtra("imgs", MyApplication.gson.toJson(goodsBean.getImgList()));
                 startActivity(intent);
                 break;
                 default:
                     break;
        }

    }
}
