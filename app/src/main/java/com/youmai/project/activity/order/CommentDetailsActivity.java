package com.youmai.project.activity.order;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.bean.GoodsBean;
import com.youmai.project.http.HandlerConstant;
import com.youmai.project.http.HttpMethod;
import com.youmai.project.utils.StatusBarUtils;
import com.youmai.project.utils.SystemBarTintManager;
import com.youmai.project.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * 评价详情
 * Created by Administrator on 2018/11/10.
 */

public class CommentDetailsActivity extends BaseActivity {

    private TextView tvUserName,tvContent,tvMoney;
    private ImageView imgX1,imgX2,imgX3,imgX4,imgX5,imgGood,imgType;
    private GoodsBean goodsBean;
    private List<ImageView> imgList=new ArrayList<>();
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        StatusBarUtils.transparencyBar(this);
        setContentView(R.layout.activity_comment_details);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //系统版本大于19
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.color_FF4081);
        initView();
        showData();
        getData();
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
        tvMoney=(TextView)findViewById(R.id.tv_oi_money);
        imgType=(ImageView)findViewById(R.id.img_oi_type);
    }



    /**
     * 展示数据
     */
    private void showData() {
        goodsBean = (GoodsBean) getIntent().getSerializableExtra("goodsBean");
        if (null == goodsBean) {
            return;
        }
        tvUserName.setText(goodsBean.getNickname());
        //设置星级
        setXing(goodsBean.getCreditLevel());
        Glide.with(mContext).load(goodsBean.getImgList().get(0)).override(102,102).centerCrop().error(R.mipmap.icon).into(imgGood);
        tvContent.setText(goodsBean.getDescription());
        tvMoney.setText(Util.setDouble(goodsBean.getPresentPrice()/100));
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


    private Handler mHandler=new Handler(new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            return false;
        }
    });


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
     * 查询评论列表数据
     */
    private void getData(){
        HttpMethod.getCommentList(null,goodsBean.getOrderId(),1, HandlerConstant.GET_COMMENT_LIST_SUCCESS,mHandler);
    }
}
