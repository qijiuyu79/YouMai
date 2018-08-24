package com.youmai.project.activity.main;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.application.MyApplication;
import com.youmai.project.utils.LogUtils;
import com.youmai.project.view.TouchImageView;

import java.util.List;

/**
 * 展示图片
 */
public class ShowImgActivity extends BaseActivity {

    private ViewPager viewPager;
    private LinearLayout layoutPoint;
    private List<String> listImg;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_img);
        getImages();
        initView();
    }


    /**
     * 获取图片数据
     */
    private void getImages(){
        final String imgs=getIntent().getStringExtra("imgs");
        if(TextUtils.isEmpty(imgs)){
            finish();
        }
        listImg= MyApplication.gson.fromJson(imgs,List.class);
    }


    /**
     * 初始化
     */
    private void initView(){
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        layoutPoint = (LinearLayout) findViewById(R.id.layout_point);
        //ViewPager相关
        ViewPagerAdater myAdater = new ViewPagerAdater(this);
        viewPager.setAdapter(myAdater);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //在滑动完成后向布局中添加小圆点
                setLayoutPoint(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setLayoutPoint(0);
    }

    /**
     * 设置小圆点布局
     * @param position
     */
    private void setLayoutPoint(int position){
        layoutPoint.removeAllViews();
        for (int i = 0; i <listImg.size() ; i++) {
            ImageView imageView = new ImageView(this);
            //设置ImageView
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, 10);
            params.setMargins(10,0,10,0);
            imageView.setLayoutParams(params);
            //设置小圆点样式
            if (position==i){
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.point_gray));
            }else {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.point_white));
            }
            layoutPoint.addView(imageView);
        }
    }

    public class ViewPagerAdater extends PagerAdapter {
        private Context context;

        public ViewPagerAdater(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return listImg.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TouchImageView imageView = new TouchImageView(context);
            Glide.with(context).load(listImg.get(position)).into(imageView);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ImageView) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
