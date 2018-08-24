package com.youmai.project.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.youmai.project.R;

public class ExpandView extends FrameLayout{


    private Animation mExpandAnimation,mCollapseAnimation;
    public ExpandView(Context context) {
        this(context,null);
    }
    public ExpandView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    public ExpandView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initExpandView();
    }
    private void initExpandView() {
        mExpandAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.expand);
        mExpandAnimation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(View.VISIBLE);
            }
        });

        mCollapseAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.collapse);
        mCollapseAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(View.INVISIBLE);
            }
        });
    }

    public void expand() {
        clearAnimation();
        startAnimation(mExpandAnimation);
    }

    public void collapse() {
        clearAnimation();
        startAnimation(mCollapseAnimation);
    }
}