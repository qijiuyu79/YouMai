package com.youmai.project.activity;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.youmai.project.R;
import com.youmai.project.application.MyApplication;
import com.youmai.project.utils.SPUtil;

import java.util.ArrayList;

/**
 * 导航页
 */
public class GuideActivity extends BaseActivity {
    private ViewPager viewPager;
    //用来存放导航图片实例
    private ArrayList<ImageView> imageViews;
    //导航页资源
    private int[] images = new int[]{
            R.mipmap.guide_1,
            R.mipmap.guide_2,
            R.mipmap.guide_3,
            R.mipmap.guide_4
    };
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(MyApplication.spUtil.getBoolean(SPUtil.IS_FIRST_OPEN)){
            setClass(WelcomeActivity.class);
            finish();
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.activity_guide);
        startBanner();
    }

    /**
     * 开始引导页
     */
    private void startBanner(){
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        imageViews = new ArrayList<>();
        //初始化导航页面
        for (int i = 0; i < images.length; i++) {
            ImageView iv = new ImageView(this);
            iv.setImageResource(images[i]);
            imageViews.add(iv);
        }

        //为ViewPager添加适配器
        viewPager.setAdapter(new MyAdapter());
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrolled(int i, float v, int i1) {

            }
            public void onPageSelected(int i) {
            }
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    //PagerAdapter有四个方法
    class MyAdapter extends PagerAdapter {
        //返回导航页的个数
        @Override
        public int getCount() {
            return images.length;
        }

        //判断是否由对象生成
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        //加载页面
        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView iv = imageViews.get(position);
            container.addView(iv);
            iv.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(position==3){
                        setClass(WelcomeActivity.class);
                        MyApplication.spUtil.addBoolean(SPUtil.IS_FIRST_OPEN,true);
                        finish();
                    }
                }
            });
            return iv;
        }

        //移除页面
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
