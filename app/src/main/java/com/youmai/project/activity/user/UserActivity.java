package com.youmai.project.activity.user;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.application.MyApplication;
import com.youmai.project.http.HandlerConstant;
import com.youmai.project.http.HttpMethod;
import com.youmai.project.utils.StatusBarUtils;
import com.youmai.project.utils.Util;
import com.youmai.project.utils.photo.PicturesUtil;
import com.youmai.project.view.CircleImageView;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 我的
 * Created by Administrator on 2018/1/18 0018.
 */

public class UserActivity extends BaseActivity implements View.OnClickListener{

    private CircleImageView imgUserPic;
    private RelativeLayout relCertifcation;
    private TextView tvNickName,tvBalance,tvIngetral,tvCredit,tvCertifcation;
    private ImageView imgX1,imgX2,imgX3,imgX4,imgX5;
    private String nickName;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        StatusBarUtils.transparencyBar(this);
        setContentView(R.layout.activity_user);
        initView();
        register();//注册广播
    }


    /**
     * 初始化控件
     */
    private void initView(){
        imgUserPic=(CircleImageView)findViewById(R.id.img_ri_pic);
        tvNickName=(TextView)findViewById(R.id.tv_au_nickName);
        tvBalance=(TextView)findViewById(R.id.tv_au_balance);
        tvIngetral=(TextView)findViewById(R.id.tv_au_integral);
        tvCredit=(TextView)findViewById(R.id.tv_au_credit);
        tvCertifcation=(TextView)findViewById(R.id.tv_au_certification);
        relCertifcation=(RelativeLayout)findViewById(R.id.rel_au_cer);
        imgX1=(ImageView)findViewById(R.id.img_au_x1);
        imgX2=(ImageView)findViewById(R.id.img_au_x2);
        imgX3=(ImageView)findViewById(R.id.img_au_x3);
        imgX4=(ImageView)findViewById(R.id.img_au_x4);
        imgX5=(ImageView)findViewById(R.id.img_au_x5);
        imgUserPic.setOnClickListener(this);
        relCertifcation.setOnClickListener(this);
        findViewById(R.id.rel_myAddress).setOnClickListener(this);
        findViewById(R.id.img_au_setName).setOnClickListener(this);
        findViewById(R.id.tv_au_recharge).setOnClickListener(this);
        findViewById(R.id.tv_au_transfer).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //设置用户头像
            case R.id.img_ri_pic:
                 PicturesUtil.selectPhoto(UserActivity.this,2);
                 break;
            //设置昵称
            case R.id.img_au_setName:
                View view = getLayoutInflater().inflate(R.layout.set_name, null);
                final Dialog dialogs = new Dialog(this);
                dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogs.setContentView(view);
                dialogs.show();
                final EditText etName = (EditText) view.findViewById(R.id.et_setName);
                TextView tvConfirm = (TextView) view.findViewById(R.id.tv_confirm);
                TextView tvCancle = (TextView) view.findViewById(R.id.tv_cancle);
                tvConfirm.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        nickName = etName.getText().toString().trim();
                        if (TextUtils.isEmpty(nickName)) {
                            showMsg("请输入您的昵称！");
                        }else if(!Util.stringFilter(nickName)){
                            showMsg("昵称只能输入中文，字母，数字，* ！");
                        }else {
                            dialogs.dismiss();
                            showProgress("设置中...",false);
                            HttpMethod.setNickName(nickName,mHandler);
                        }
                    }
                });
                tvCancle.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialogs.dismiss();
                    }
                });
                 break;
            //充值
            case R.id.tv_au_recharge:
                 setClass(RechargeActivity.class);
                 break;
            //转账
            case R.id.tv_au_transfer:
                setClass(WithdrawalActivity.class);
                 break;
            //我的地址
            case R.id.rel_myAddress:
                 setClass(MyAddressActivity.class);
                 break;
            //实名认证
            case R.id.rel_au_cer:
                setClass(CertificationActivity.class);
                 break;
            default:
                break;
        }

    }


    private Handler mHandler=new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            clearTask();
            String message;
            switch (msg.what){
                //设置头像返回
                case HandlerConstant.SET_USERPIC_SUCCESS:
                     message= (String) msg.obj;
                     if(TextUtils.isEmpty(message)){
                        return;
                     }
                     try {
                        final JSONObject jsonObject=new JSONObject(message);
                        if(jsonObject.getInt("code")==200){
                            Glide.with(mContext).load(jsonObject.getString("data")).error(R.mipmap.icon).into(imgUserPic);
                        }
                     }catch (Exception e){
                        e.printStackTrace();
                     }
                     break;
                //设置昵称返回
                case HandlerConstant.SET_NICKNAME_SUCCESS:
                     message= (String) msg.obj;
                     if(TextUtils.isEmpty(message)){
                        return;
                     }
                    try {
                        final JSONObject jsonObject=new JSONObject(message);
                        if(jsonObject.getInt("code")==200){
                            tvNickName.setText(nickName);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
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


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //返回拍照图片
            case PicturesUtil.CODE_CAMERA_REQUEST:
                if (resultCode == RESULT_OK) {
                    File tempFile = new File(PicturesUtil.pai);
                    if (tempFile.isFile()) {
                        cropRawPhoto(Uri.fromFile(tempFile));
                    }
                }
                break;
            //返回相册选择图片
            case PicturesUtil.CODE_GALLERY_REQUEST:
                if (data != null) {
                    cropRawPhoto(data.getData());
                }
                break;
            //返回裁剪的图片
            case PicturesUtil.CODE_RESULT_REQUEST:
                final File f = new File(PicturesUtil.crop);
                if(null!=f){
                    List<File> list=new ArrayList<>();
                    list.add(f);
                    showProgress("头像上传中...",false);
                    HttpMethod.setUserPic(list,mHandler);
                }
                break;
            default:
                break;

        }
    }


    /**
     * 裁剪原始的图片
     */
    public void cropRawPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("output", Uri.fromFile(new File(PicturesUtil.crop)));
        // 设置裁剪
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 100);
        intent.putExtra("return-data", false);
        startActivityForResult(intent, PicturesUtil.CODE_RESULT_REQUEST);
    }


    /**
     * 注册广播
     */
    private void register() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(UPDATE_USER_INFO);
        // 注册广播监听
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(UPDATE_USER_INFO)) {
                if(null==MyApplication.userInfoBean){
                    return;
                }
                //更新用户信息
                Glide.with(mContext).load(MyApplication.userInfoBean.getHead()).error(R.mipmap.icon).into(imgUserPic);
                tvNickName.setText(MyApplication.userInfoBean.getNickname());
                tvBalance.setText("¥"+Util.setDouble(MyApplication.userInfoBean.getBalance()/100));
                tvIngetral.setText(MyApplication.userInfoBean.getIntegral()+"");
                tvCredit.setText(MyApplication.userInfoBean.getCredit()+"");
                setXing(MyApplication.userInfoBean.getCreditLevel());
                if(MyApplication.userInfoBean.isReal()){
                    tvCertifcation.setText("已认证");
                    relCertifcation.setClickable(false);
                }else{
                    tvCertifcation.setText("未认证");
                }

            }
        }
    };


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
    protected void onResume() {
        super.onResume();
        //获取用户信息
        getUserInfo();
    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}
