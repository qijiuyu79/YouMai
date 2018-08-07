package com.youmai.project.activity.user;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.youmai.project.application.MyApplication;
import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.bean.HttpBaseBean;
import com.youmai.project.bean.Login;
import com.youmai.project.http.HandlerConstant;
import com.youmai.project.http.HttpMethod;
import com.youmai.project.utils.SPUtil;
import com.youmai.project.utils.StatusBarUtils;
import com.youmai.project.utils.SystemBarTintManager;
import com.youmai.project.view.ClickTextView;
import com.youmai.project.view.MeterailEditText;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 登陆页面
 * Created by Administrator on 2018/5/17 0017.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private MeterailEditText etMobile,etCode;
    private TextView tvSendCode;
    //计数器
    private Timer mTimer;
    private int time = 0;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.transparencyBar(this);
        setContentView(R.layout.activity_login);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //系统版本大于19
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.color_FAFBFD);
        initView();
        //判断验证码秒数是否超过一分钟
        checkTime();
    }


    /**
     * 初始化控件
     */
    private void initView(){
        etMobile=(MeterailEditText)findViewById(R.id.et_al_mobile);
        etCode=(MeterailEditText)findViewById(R.id.et_al_code);
        tvSendCode=(ClickTextView)findViewById(R.id.tv_al_sendCode);
        ClickTextView tvLogin=(ClickTextView)findViewById(R.id.tv_al_login);
        etMobile.setFocusable(true);
        etMobile.setFocusableInTouchMode(true);
        etMobile.setInPutType(InputType.TYPE_CLASS_PHONE);
        etCode.setInPutType(InputType.TYPE_CLASS_PHONE);
        etMobile.setMaxLength(11);
        tvSendCode.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
        findViewById(R.id.lin_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final String mobile, code;
        switch (v.getId()){
            case R.id.tv_al_sendCode:
                 mobile=etMobile.getText().toString().trim();
                 if(TextUtils.isEmpty(mobile)){
                     showMsg("请输入手机号!");
                 }else if(mobile.length()<11){
                     showMsg("请输入完整的手机号!");
                 }else{
                     showProgress("获取验证码",false);
                     HttpMethod.sendCode(mobile,mHandler);
                 }
                 break;
            case R.id.tv_al_login:
                 mobile=etMobile.getText().toString().trim();
                 code=etCode.getText().toString().trim();
                 if(TextUtils.isEmpty(mobile)){
                    showMsg("请输入手机号!");
                 }else if(mobile.length()<11){
                    showMsg("请输入完整的手机号!");
                 }else if(TextUtils.isEmpty(code)){
                     showMsg("请输入验证码!");
                 }else{
                     showProgress("登录中",false);
                     HttpMethod.login(mobile,code,mHandler);
                 }
                 break;
            case R.id.lin_back:
                 finish();
                 break;
            default:
                break;
        }
    }


    private Handler mHandler=new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            clearTask();
            switch (msg.what){
                case HandlerConstant.SEND_CODE_SUCCESS:
                     HttpBaseBean httpBaseBean= (HttpBaseBean) msg.obj;
                     if(null==httpBaseBean){
                         return;
                     }
                     if(httpBaseBean.isSussess()){
                         //先保存计时时间
                         MyApplication.spUtil.addString("stoptime", String.valueOf((System.currentTimeMillis() + 60000)));
                         time = 60;
                         startTime();
                     }else{
                         showMsg(httpBaseBean.getMsg());
                     }
                     break;
                //动态改变验证码秒数
                case 0x001:
                    tvSendCode.setText(time + "秒");
                    break;
                case 0x002:
                    if (null != mTimer) {
                        mTimer.cancel();
                    }
                    tvSendCode.setText("发送验证码");
                    MyApplication.spUtil.removeMessage("stoptime");
                    break;
                //登陆返回
                case HandlerConstant.LOGIN_SUCCESS:
                     Login login= (Login) msg.obj;
                     if(login==null){
                         return;
                     }
                     if(login.isSussess()){
                         MyApplication.spUtil.addString(SPUtil.AUTH_TOKEN,login.getData().getAuth_token());
                         MyApplication.spUtil.addString(SPUtil.ACCESS_TOKEN,login.getData().getAccess_token());
                         //获取用户信息
                         getUserInfo();
                         finish();
                     }else{
                         showMsg(login.getMsg());
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
     * 动态改变验证码秒数
     */
    private void startTime() {
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (time <= 0) {
                    mHandler.sendEmptyMessage(0x002);
                } else {
                    --time;
                    mHandler.sendEmptyMessage(0x001);
                }
            }
        }, 0, 1000);
    }


    /**
     * 判断验证码秒数是否超过一分钟
     */
    private void checkTime() {
        String stoptime = MyApplication.spUtil.getString("stoptime");
        if (!TextUtils.isEmpty(stoptime)) {
            int t = (int) ((Double.parseDouble(stoptime) - System.currentTimeMillis()) / 1000);
            if (t > 0) {
                time = t;
                startTime();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
        super.onDestroy();
    }
}
