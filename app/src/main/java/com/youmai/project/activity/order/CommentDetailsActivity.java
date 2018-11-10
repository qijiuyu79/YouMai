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
import com.youmai.project.bean.Comment;
import com.youmai.project.bean.GoodsBean;
import com.youmai.project.http.HandlerConstant;
import com.youmai.project.http.HttpMethod;
import com.youmai.project.utils.DateUtil;
import com.youmai.project.utils.JsonUtils;
import com.youmai.project.utils.StatusBarUtils;
import com.youmai.project.utils.SystemBarTintManager;
import com.youmai.project.utils.Util;
import com.youmai.project.view.CircleImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * 评价详情
 * Created by Administrator on 2018/11/10.
 */

public class CommentDetailsActivity extends BaseActivity {

    private TextView tvUserName,tvContent,tvMoney,tvCommUserName,tvCommTime,tvCommDes;
    private ImageView imgX1,imgX2,imgX3,imgX4,imgX5,imgGood,imgType,imgComm1,imgComm2,imgComm3,imgComm4,imgComm5;
    private CircleImageView imgUserPic;
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
        imgUserPic=(CircleImageView)findViewById(R.id.img_acd_pic);
        tvCommUserName=(TextView)findViewById(R.id.tv_acd_name);
        imgComm1=(ImageView)findViewById(R.id.img_acd_x1);
        imgComm2=(ImageView)findViewById(R.id.img_acd_x2);
        imgComm3=(ImageView)findViewById(R.id.img_acd_x3);
        imgComm4=(ImageView)findViewById(R.id.img_acd_x4);
        imgComm5=(ImageView)findViewById(R.id.img_acd_x5);
        imgGood=(ImageView)findViewById(R.id.img_psi_icon);
        tvCommTime=(TextView)findViewById(R.id.tv_comm_time);
        tvCommDes=(TextView)findViewById(R.id.tv_comment_des);
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
            clearTask();
            switch (msg.what){
                case HandlerConstant.GET_COMMENT_LIST_SUCCESS:
                    String message= (String) msg.obj;
                    List<Comment> list= JsonUtils.getCommentList(message);
                    showComment(list);
                    break;
                case HandlerConstant.REQUST_ERROR:
                    showMsg(getString(R.string.http_error));
                    break ;
                default:
                    break;
            }
            return false;
        }
    });


    /**
     * 展示评论信息
     */
    private void showComment(List<Comment> list){
        if(null==list || list.size()==0){
            return;
        }
        final Comment comment=list.get(0);
        Glide.with(mContext).load(comment.getHead()).override(44,44).centerCrop().error(R.mipmap.icon).into(imgUserPic);
        tvCommUserName.setText(comment.getNickname());
        //设置星级
        setCommXing(comment.getScore());
        tvCommTime.setText(DateUtil.getData(comment.getCreateTime()));
        tvCommDes.setText(comment.getEvaluate());
    }


    /**
     * 设置卖家星级
     */
    private void setXing(int index){
        imgList.clear();
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
     * 设置评论星级
     */
    private void setCommXing(int index){
        imgList.clear();
        imgList.add(imgComm1);
        imgList.add(imgComm2);
        imgList.add(imgComm3);
        imgList.add(imgComm4);
        imgList.add(imgComm5);
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
