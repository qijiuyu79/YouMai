package com.youmai.project.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import com.youmai.project.R;
import com.youmai.project.application.MyApplication;
import com.youmai.project.bean.Login;
import com.youmai.project.http.HandlerConstant;
import com.youmai.project.http.HttpMethod;
import com.youmai.project.utils.SPUtil;

public class WelcomeActivity extends BaseActivity {

    private RelativeLayout relativeLayout;
    private Animation myAnimation_Alpha;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.activity_welcome);
        getAccessToken();
        initView();
        initAnim();

    }

    private void initView() {
        relativeLayout=(RelativeLayout)findViewById(R.id.lin_start);
    }

    private void initAnim() {
        myAnimation_Alpha = new AlphaAnimation(0.1f, 1.0f);
        myAnimation_Alpha.setDuration(3000);
        myAnimation_Alpha.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setClass(TabActivity.class);
                finish();
            }
        });
        relativeLayout.setAnimation(myAnimation_Alpha);
        myAnimation_Alpha.start();
    }


    private Handler mHandler=new Handler(new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            if(msg.what== HandlerConstant.GET_ACCESS_TOKEN_SUCCESS){
                Login login= (Login) msg.obj;
                if(login==null){
                    return false;
                }
                if(login.isSussess()){
                    MyApplication.spUtil.addString(SPUtil.AUTH_TOKEN,login.getData().getAuth_token());
                    MyApplication.spUtil.addString(SPUtil.ACCESS_TOKEN,login.getData().getAccess_token());
                }
            }
            return false;
        }
    });


    /**
     * 获取最新的accessToken
     */
    private void getAccessToken(){
        final String auth_token= MyApplication.spUtil.getString(SPUtil.AUTH_TOKEN);
        if(!TextUtils.isEmpty(auth_token)){
            HttpMethod.getAccessToken(auth_token,mHandler);
        }
    }

}
