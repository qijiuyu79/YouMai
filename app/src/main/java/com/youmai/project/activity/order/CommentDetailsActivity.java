package com.youmai.project.activity.order;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.activity.main.ShowImgActivity;
import com.youmai.project.activity.photo.NetBigPhotoActivity;
import com.youmai.project.adapter.photo.NetGridImageAdapter;
import com.youmai.project.application.MyApplication;
import com.youmai.project.bean.Comment;
import com.youmai.project.bean.GoodsBean;
import com.youmai.project.bean.HttpBaseBean;
import com.youmai.project.http.HandlerConstant;
import com.youmai.project.http.HttpMethod;
import com.youmai.project.utils.DateUtil;
import com.youmai.project.utils.JsonUtils;
import com.youmai.project.utils.StatusBarUtils;
import com.youmai.project.utils.SystemBarTintManager;
import com.youmai.project.utils.Util;
import com.youmai.project.view.CircleImageView;
import com.youmai.project.view.MyGridView;
import java.util.ArrayList;
import java.util.List;

/**
 * 评价详情
 * Created by Administrator on 2018/11/10.
 */

public class CommentDetailsActivity extends BaseActivity implements View.OnClickListener{

    private TextView tvUserName,tvContent,tvMoney,tvCommUserName,tvCommTime,tvCommDes;
    private ImageView imgX1,imgX2,imgX3,imgX4,imgX5,imgGood,imgType,imgComm1,imgComm2,imgComm3,imgComm4,imgComm5;
    private CircleImageView imgUserPic;
    private MyGridView myGridView;
    private EditText etReply;
    private TextView tvReplyContent;
    private RelativeLayout relReply;
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
        getData();
    }


    /**
     * 初始化控件
     */
    private void initView(){
        goodsBean = (GoodsBean) getIntent().getSerializableExtra("goodsBean");
        if (null == goodsBean) {
            return;
        }
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
        myGridView=(MyGridView)findViewById(R.id.mg_commimg);
        etReply=(EditText)findViewById(R.id.et_reply);
        tvReplyContent=(TextView)findViewById(R.id.tv_reply_content);
        relReply=(RelativeLayout)findViewById(R.id.rel_reply);
        findViewById(R.id.tv_reply).setOnClickListener(this);
        findViewById(R.id.lin_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //答复
            case R.id.tv_reply:
                 final String content=etReply.getText().toString().trim();
                 if(TextUtils.isEmpty(content)){
                     showMsg("请输入要答复的内容！");
                     return;
                 }
                 reply(content);
                 break;
            case R.id.lin_back:
                finish();
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
                //答复回执
                case HandlerConstant.REPLY_SUCCESS:
                     final HttpBaseBean httpBaseBean= (HttpBaseBean) msg.obj;
                     if(null==httpBaseBean){
                         break;
                     }
                     if(httpBaseBean.isSussess()){
                         showMsg("答复成功");
                         tvReplyContent.setVisibility(View.VISIBLE);
                         tvReplyContent.setText("回复："+etReply.getText().toString());
                         relReply.setVisibility(View.GONE);
                     }else{
                         showMsg(httpBaseBean.getMsg());
                     }
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
    private Comment comment;
    private void showComment(List<Comment> list){
        if(null==list || list.size()==0){
            return;
        }
        comment=list.get(0);

        /**
         * 显示卖家信息
         */
        tvUserName.setText(comment.getS_nickname());
        //设置星级
        setXing(comment.getS_creditLevel());

        /**
         * 显示商品信息
         */
        Glide.with(mContext).load(comment.getImgList().get(0)).override(102,102).centerCrop().error(R.mipmap.icon).into(imgGood);
        tvContent.setText(comment.getDescription());
        tvMoney.setText(Util.setDouble(comment.getPresentPrice()/100));
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

        /**
         * 显示评论者信息
         */
        Glide.with(mContext).load(comment.getHead()).override(44,44).centerCrop().error(R.mipmap.icon).into(imgUserPic);
        tvCommUserName.setText(comment.getNickname());
        //设置星级
        setCommXing(comment.getScore());
        tvCommTime.setText(DateUtil.getData(comment.getCreateTime()));
        tvCommDes.setText(comment.getEvaluate());
        NetGridImageAdapter netGridImageAdapter=new NetGridImageAdapter(mContext,comment.getComm_imgs());
        myGridView.setAdapter(netGridImageAdapter);
        myGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent intent = new Intent(mContext, ShowImgActivity.class);
                intent.putExtra("imgs", MyApplication.gson.toJson(comment.getComm_imgs()));
                intent.putExtra("index",arg2);
                startActivity(intent);
            }
        });

        //显示卖家回复内容
        if(!TextUtils.isEmpty(comment.getReplyContent())){
            tvReplyContent.setVisibility(View.VISIBLE);
            tvReplyContent.setText("回复："+comment.getReplyContent());
            relReply.setVisibility(View.GONE);
        }
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


    /**
     * 回复评论
     */
    private void reply(String content){
        showProgress("提交中...");
        HttpMethod.reply(comment.getId(),content,mHandler);
    }

}
