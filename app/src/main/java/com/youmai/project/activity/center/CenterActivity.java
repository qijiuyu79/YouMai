package com.youmai.project.activity.center;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import com.youmai.project.R;
import com.youmai.project.bean.MyGoods;
import com.youmai.project.fragment.BuyerSpeakFragment;
import com.youmai.project.fragment.MyGoodsFragment;
import com.youmai.project.utils.LogUtils;
import com.youmai.project.view.PagerSlidingTabStrip;

public class CenterActivity extends FragmentActivity {

    private MyGoodsFragment myGoodsFragment;
    private BuyerSpeakFragment buyerSpeakFragment;
    private PagerSlidingTabStrip tabs;
    private DisplayMetrics dm;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center);
        dm = getResources().getDisplayMetrics();
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        tabs.setViewPager(pager);
        setTabsValue();
    }

    /**
     * 对PagerSlidingTabStrip的各项属性进行赋值。
     */
    private void setTabsValue() {
        // 设置Tab是自动填充满屏幕的
        tabs.setShouldExpand(true);
        // 设置Tab的分割线是透明的
        tabs.setDividerColor(Color.TRANSPARENT);
        // 设置Tab底部线的高度
        tabs.setUnderlineHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1, dm));
        // 设置Tab Indicator的高度
        tabs.setIndicatorHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 2, dm));
        // 设置Tab标题文字的大小
        tabs.setTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 14, dm));
        // 设置Tab Indicator的颜色
        tabs.setIndicatorColorResource(R.color.color_FF4081);
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        tabs.setTextColorResource(R.color.color_525252);
        tabs.setSelectedTextColorResource(R.color.color_FF4081);
        // 取消点击Tab时的背景色
        tabs.setTabBackground(0);

        findViewById(R.id.lin_ac_add).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent=new Intent(CenterActivity.this,AddShopActivity.class);
                startActivityForResult(intent,1);
            }
        });
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = { "我的宝贝","买家留言"};

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Fragment getItem(int position) {
            if(position==0){
                if(myGoodsFragment==null){
                    myGoodsFragment=new MyGoodsFragment();
                }
                return myGoodsFragment;
            }else{
                if(buyerSpeakFragment==null){
                    buyerSpeakFragment=new BuyerSpeakFragment();
                }
                return buyerSpeakFragment;
            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data==null){
            return;
        }
        switch (requestCode){
            //添加商品后的返回
            case 1:
                 Bundle bundle=data.getExtras();
                 if(bundle==null){
                     return;
                 }
                 MyGoods myGoods= (MyGoods) bundle.getSerializable("myGoods");
                 if(null!=myGoods){
                     myGoodsFragment.addGoods(myGoods);
                 }
                 break;
            default:
                break;
        }
    }
}

