package com.youmai.project.activity.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import com.youmai.project.activity.user.LoginActivity;
import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.callback.ViewPagerCallBack;
import com.youmai.project.fragment.RecommendedFragment;
import com.youmai.project.http.HandlerConstant;
import com.youmai.project.http.HttpMethod;
import com.youmai.project.utils.Util;
import com.youmai.project.utils.map.GetLocation;
import com.youmai.project.utils.UpdateVersionUtils;
import com.youmai.project.view.PagerSlidingTabStrip;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
public class MainActivity extends BaseActivity{

    private TextView tvCount;
    private PagerSlidingTabStrip tabs;
    private DisplayMetrics dm;
    private ViewPager pager;
    //切换fragment的位置
    public static int index;
    //是否定位成功
    private boolean isFirst=false;
    public static List<String> keyList=new ArrayList<>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        //开始定位
        getLocation();
        //查询版本
        new UpdateVersionUtils().getVersion(MainActivity.this);
    }


    /**
     * 初始化控件
     */
    private void initView(){
        tvCount=(TextView)findViewById(R.id.tv_location_count);
        dm = getResources().getDisplayMetrics();
        pager = (ViewPager) findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        //搜索
        findViewById(R.id.rel_am_search).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setClass(SearchKeyActivity.class);
            }
        });
        //扫一扫
        findViewById(R.id.img_scan).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!Util.isLogin()){
                    Intent intent=new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                    return;
                }
                setClass(ScanActivity.class);
            }
        });

        keyList.add("USED");
        keyList.add("NEW");
        keyList.add("PET");
    }

    /**
     * 定位
     */
    private void getLocation(){
        showProgress("定位中");
        GetLocation.getInstance().setLocation(null,MainActivity.this,mHandler);
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
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = { "新品","二手","植宠"};

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Fragment getItem(int position) {
            return new RecommendedFragment();
        }

    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler=new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                //定位失败
                case -1:
                    clearTask();
                    GetLocation.getInstance().isOPen(mContext);
                    break;
                //定位成功
                case 0x00:
                    clearTask();
                    if(isFirst){
                        return;
                    }
                    isFirst=true;
                    pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
                    pager.setOffscreenPageLimit(3);
                    tabs.setViewPager(pager);
                    tabs.setViewPagerCallBack(viewPagerCallBack);
                    setTabsValue();
                    //查询附近宝贝的数量
                    getLocationGoodsCount();
                     break;
                //查询附近宝贝的数量
                case HandlerConstant.GET_LOCATION_COUNT_SUCCESS:
                     final String message= (String) msg.obj;
                     if(TextUtils.isEmpty(message)){
                         return;
                     }
                     try {
                         final JSONObject jsonObject=new JSONObject(message);
                         if(jsonObject.getInt("code")==200){
                             tvCount.setText("您附近有"+jsonObject.getInt("data")+"个宝贝");
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


    private ViewPagerCallBack viewPagerCallBack=new ViewPagerCallBack() {
        public void PageSelected(int position) {
            MainActivity.index=position;
        }
    };


    /**
     * 查询附近宝贝的数量
     */
    private void getLocationGoodsCount(){
        HttpMethod.getLocationCount(mHandler);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //获取用户信息
        getUserInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeHandler(mHandler);
    }
}
