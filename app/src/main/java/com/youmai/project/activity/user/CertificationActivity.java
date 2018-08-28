package com.youmai.project.activity.user;

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
import com.youmai.project.http.HandlerConstant;
import com.youmai.project.http.HttpMethod;
import com.youmai.project.utils.SystemBarTintManager;
import com.youmai.project.utils.Util;
import org.json.JSONObject;

/**
 * 认证
 * Created by Administrator on 2018/1/19 0019.
 */

public class CertificationActivity extends BaseActivity implements View.OnClickListener{

    private EditText etName,etCode;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_certification);
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
        TextView tvHead=(TextView)findViewById(R.id.tv_head);
        tvHead.setText("实名认证");
        etName=(EditText)findViewById(R.id.et_ac_name);
        etCode=(EditText)findViewById(R.id.et_ac_code);
        findViewById(R.id.lin_back).setOnClickListener(this);
        findViewById(R.id.tv_ac_submit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_ac_submit:
                 final String name=etName.getText().toString().trim();
                 final String code=etCode.getText().toString().trim();
                 if(TextUtils.isEmpty(name)){
                     showMsg("请输入您的姓名！");
                 }else if(Util.isEmoji(name)){
                     showMsg("姓名不支持表情符号！");
                 }else if(TextUtils.isEmpty(code)){
                     showMsg("请输入您的身份证号码！");
                 }else{
                     showProgress("认证中...",false);
                     HttpMethod.certification(name,code,mHandler);
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
                case HandlerConstant.CERIFICATION_SUCCESS:
                     String message= (String) msg.obj;
                     if(TextUtils.isEmpty(message)){
                         return;
                     }
                     try {
                         final JSONObject jsonObject=new JSONObject(message);
                         if(jsonObject.getInt("code")==200){
                             if(null== MyApplication.userInfoBean){
                                 MyApplication.userInfoBean.setReal(true);
                             }
                             showMsg("认证成功！");
                             finish();
                         }else{
                             showMsg(jsonObject.getString("msg"));
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
}
