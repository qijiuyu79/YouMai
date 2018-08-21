package com.youmai.project.activity.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.application.MyApplication;
import com.youmai.project.utils.StatusBarUtils;
import com.youmai.project.utils.SystemBarTintManager;
import com.youmai.project.utils.Util;
import com.youmai.project.utils.WechatAuthUtils;

/**
 * 转账
 */
public class WithdrawalActivity extends BaseActivity implements View.OnClickListener{

    @Override
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
        TextView tvBalance=(TextView)findViewById(R.id.tv_aw_balance);
        tvBalance.setText("¥"+ Util.setDouble(MyApplication.userInfoBean.getBalance()/100));
        findViewById(R.id.lin_aw_wx).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lin_aw_wx:
                 WechatAuthUtils.getInstance(WithdrawalActivity.this).wechatAuth();
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
