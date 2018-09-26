package com.youmai.project.activity;

import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import com.youmai.project.R;

public class WelcomeActivity extends BaseActivity {

    private RelativeLayout relativeLayout;
    private Animation myAnimation_Alpha;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.activity_welcome);
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

}
