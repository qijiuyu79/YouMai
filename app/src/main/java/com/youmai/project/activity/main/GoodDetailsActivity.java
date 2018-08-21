package com.youmai.project.activity.main;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.adapter.GoodsListAdapter;
import com.youmai.project.application.MyApplication;
import com.youmai.project.bean.GoodsBean;
import com.youmai.project.http.HandlerConstant;
import com.youmai.project.http.HttpMethod;
import com.youmai.project.utils.JsonUtils;
import com.youmai.project.utils.StatusBarUtils;
import com.youmai.project.utils.SystemBarTintManager;
import com.youmai.project.utils.Util;
import com.youmai.project.view.MyGridView;
import java.util.ArrayList;
import java.util.List;

/**
 * 宝贝详情
 */
public class GoodDetailsActivity extends BaseActivity implements View.OnClickListener{

    private GoodsBean goodsBean;
    private MyGridView myGridView;
    private List<GoodsBean> listBeanAll=new ArrayList<>();
    private GoodsListAdapter goodsListAdapter;
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
        //根据storeId查询卖家商品
        getGoods();
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
        myGridView=(MyGridView)findViewById(R.id.mg_agd_goods);
        tvNickName.setText(goodsBean.getNickname());
        if(null!=goodsBean.getImgList() && goodsBean.getImgList().size()>0){
            Glide.with(mContext).load(goodsBean.getImgList().get(0)).centerCrop().error(R.mipmap.icon).into(imageView);
        }
        tvCount.setText("1/"+goodsBean.getImgList().size());
        tvContent.setText(goodsBean.getDescription());
        tvPresentPrice.setText("现价："+ Util.setDouble(goodsBean.getPresentPrice()/100));
        tvAddress.setText(goodsBean.getAddress());
        imageView.setOnClickListener(this);
        findViewById(R.id.tv_agd_buy).setOnClickListener(this);
        findViewById(R.id.lin_back).setOnClickListener(this);
    }


    private Handler mHandler=new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HandlerConstant.GET_GOODS_BY_STOREID_SUCCESS:
                     final String message=msg.obj.toString();
                     parsingData(message);
                     break;
                case HandlerConstant.REQUST_ERROR:
                    showMsg(getString(R.string.http_error));
                    break ;
                default:
                    break;
            }
        }
    };


    /**
     * 解析数据
     * @param msg
     */
    private void parsingData(String msg){
        List<GoodsBean> list= JsonUtils.getGoods(msg);
        listBeanAll.addAll(list);
        goodsListAdapter=new GoodsListAdapter(mContext,listBeanAll);
        myGridView.setAdapter(goodsListAdapter);
    }

    @Override
    public void onClick(View v) {
        Intent intent=new Intent();
        switch (v.getId()){
            case R.id.img_agd_img:
                 intent.setClass(mContext,ShowImgActivity.class);
                 intent.putExtra("imgs", MyApplication.gson.toJson(goodsBean.getImgList()));
                 startActivity(intent);
                 break;
            //购买
            case R.id.tv_agd_buy:
                 intent.setClass(mContext,BuyGoodsActivity.class);
                 Bundle bundle=new Bundle();
                 bundle.putSerializable("goodsBean",goodsBean);
                 intent.putExtras(bundle);
                 startActivity(intent);
                 break;
            case R.id.lin_back:
                 finish();
                 break;
             default:
                 break;
        }

    }

    /**
     * 根据storeId查询卖家商品
     */
    private void getGoods(){
        if(null==goodsBean){
            return;
        }
        HttpMethod.getGoodsByStoreId(1,goodsBean.getStoreId(),mHandler);
    }
}
