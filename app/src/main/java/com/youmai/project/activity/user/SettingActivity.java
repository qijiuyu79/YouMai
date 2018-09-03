package com.youmai.project.activity.user;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.application.MyApplication;
import com.youmai.project.utils.SPUtil;
import com.youmai.project.utils.StatusBarUtils;
import com.youmai.project.utils.SystemBarTintManager;
import com.youmai.project.view.DialogView;

/**
 * 设置页面
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener{

    private DialogView dialogView;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setting);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //系统版本大于19
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.gray_bg);
        initView();
    }


    /**
     * 初始化控件
     */
    private void initView(){
        TextView tvHead=(TextView)findViewById(R.id.tv_head);
        tvHead.setText("设置");
        findViewById(R.id.rel_loginOut).setOnClickListener(this);
        findViewById(R.id.lin_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rel_loginOut:
                dialogView = new DialogView(this, "确定退出登录吗？", "确定", "取消", new View.OnClickListener() {
                    public void onClick(View v) {
                        dialogView.dismiss();
                        MyApplication.spUtil.removeMessage(SPUtil.ACCESS_TOKEN);
                        MyApplication.spUtil.removeMessage(SPUtil.AUTH_TOKEN);
                        Intent logoutIntent = new Intent(getApplicationContext(), LoginActivity.class);
                        logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(logoutIntent);
                        finish();
                    }
                }, null);
                dialogView.show();
                 break;
            case R.id.lin_back:
                 finish();
                 break;
                 default:
                     break;
        }
    }
}
