package com.youmai.project.activity.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.application.MyApplication;
import com.youmai.project.bean.HttpBaseBean;
import com.youmai.project.http.HandlerConstant;
import com.youmai.project.http.HttpMethod;
import com.youmai.project.utils.StatusBarUtils;
import com.youmai.project.utils.SystemBarTintManager;
import com.youmai.project.utils.Util;

/**
 * 转账
 */
public class WithdrawalActivity extends BaseActivity implements View.OnClickListener{

    private EditText etAccount,etMoney;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        StatusBarUtils.transparencyBar(this);
        setContentView(R.layout.activity_withdrawal);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //系统版本大于19
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.color_FF4081);
        initView();
        //注册广播
        registerBroadcastReceiver();
    }


    /**
     * 初始化控件
     */
    private void initView(){
        etAccount=(EditText)findViewById(R.id.et_aw_account);
        etMoney=(EditText)findViewById(R.id.et_aw_money);
        TextView tvBalance=(TextView)findViewById(R.id.tv_aw_balance);
        tvBalance.setText("¥"+ Util.setDouble(MyApplication.userInfoBean.getBalance()/100));
        findViewById(R.id.lin_aw_wx).setOnClickListener(this);
        findViewById(R.id.lin_aw_zfb).setOnClickListener(this);
        findViewById(R.id.lin_back).setOnClickListener(this);
    }


    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            clearTask();
            if(msg.what== HandlerConstant.WITHDRAWAL_SUCCESS){
                HttpBaseBean httpBaseBean= (HttpBaseBean) msg.obj;
                if(null==httpBaseBean){
                    return;
                }
                if(httpBaseBean.isSussess()){
                    showMsg("转入成功！");
                    finish();
                }else{
                    showMsg(httpBaseBean.getMsg());
                }
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lin_aw_wx:
                 showMsg("微信转账暂未开通！");
                 break;
            case R.id.lin_aw_zfb:
                 final String account=etAccount.getText().toString().trim();
                 final String money=etMoney.getText().toString().trim();
                 if(TextUtils.isEmpty(account)){
                     showMsg("请输入转入的账号！");
                 }else if(TextUtils.isEmpty(money)){
                     showMsg("请输入转入金额！");
                 }else{
                     showProgress("转入中...");
                     HttpMethod.withdrawal(money,"ALIPAY",account,mHandler);
                 }
                 break;
            case R.id.lin_back:
                 finish();
                 break;
                 default:
                     break;
        }

    }


    /**
     * 注册广播
     */
    private void registerBroadcastReceiver() {
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("AUTH_ACTION");
        registerReceiver(mBroadCastReceiver, mIntentFilter);
    }

    private BroadcastReceiver mBroadCastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals("AUTH_ACTION")) {
                return;
            }
            String code = intent.getStringExtra("auth_code");
            if (null == code) {
                return;
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadCastReceiver);
    }
}
