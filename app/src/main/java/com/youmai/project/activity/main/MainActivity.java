package com.youmai.project.activity.main;

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

import com.youmai.project.application.MyApplication;
import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.bean.Login;
import com.youmai.project.bean.ViewPagerCallBack;
import com.youmai.project.fragment.RecommendedFragment;
import com.youmai.project.http.HandlerConstant;
import com.youmai.project.http.HttpMethod;
import com.youmai.project.utils.map.GetLocation;
import com.youmai.project.utils.SPUtil;
import com.youmai.project.utils.UpdateVersionUtils;
import com.youmai.project.view.PagerSlidingTabStrip;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
public class MainActivity extends BaseActivity{


    private PagerSlidingTabStrip tabs;
    private DisplayMetrics dm;
    private ViewPager pager;
    //切换fragment的位置
    public static int index;
    //是否定位成功
    private boolean isFirst=false;
    public static List<String> keyList=new ArrayList<>();
    public static List<String> valList=new ArrayList<>();
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
        dm = getResources().getDisplayMetrics();
        pager = (ViewPager) findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        findViewById(R.id.rel_am_search).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setClass(SearchKeyActivity.class);
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
        showProgress("定位中",true);
        GetLocation.getInstance().setLocation(MainActivity.this,mHandler);
    }


    /**
     * 获取商品分类
     */
    private void getGoodsType(){
        showProgress("加载中...",false);
        HttpMethod.getGoodsType(mHandler);
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

        private final String[] titles = { "二手","闲置","宠物"};

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

    private Handler mHandler=new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            clearTask();
            switch (msg.what){
                //定位成功
                case 0x00:
                    if(isFirst){
                        return;
                    }
                    isFirst=true;
                     //获取商品分类
//                     getGoodsType();
                    pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
                    pager.setOffscreenPageLimit(3);
                    tabs.setViewPager(pager);
                    tabs.setViewPagerCallBack(viewPagerCallBack);
                    setTabsValue();
                     break;
                //获取首页分类
                case HandlerConstant.GET_GOODS_TYPE_SUCCESS:
                    final String message= (String) msg.obj;
                    if(TextUtils.isEmpty(message)){
                        return;
                    }
                    try {
                        final JSONObject object=new JSONObject(message);
                        if(object.getInt("code")!=200){
                            return;
                        }
                        final JSONObject jsonObject=new JSONObject(object.getString("data"));
                        final Iterator<String> sIterator = jsonObject.keys();
                        while(sIterator.hasNext()){
                            final String key = sIterator.next();
                            keyList.add(key);
                            valList.add(jsonObject.getString(key));
                        }
                        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
                        pager.setOffscreenPageLimit(valList.size());
                        tabs.setViewPager(pager);
                        tabs.setViewPagerCallBack(viewPagerCallBack);
                        setTabsValue();

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                //获取最新的access_token
                case HandlerConstant.GET_ACCESS_TOKEN_SUCCESS:
                     Login login= (Login) msg.obj;
                     if(login==null){
                        return;
                     }
                     if(login.isSussess()){
                        MyApplication.spUtil.addString(SPUtil.AUTH_TOKEN,login.getData().getAuth_token());
                        MyApplication.spUtil.addString(SPUtil.ACCESS_TOKEN,login.getData().getAccess_token());
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


    @Override
    protected void onResume() {
        super.onResume();
        final String auth_token= MyApplication.spUtil.getString(SPUtil.AUTH_TOKEN);
        if(!TextUtils.isEmpty(auth_token)){
            HttpMethod.getAccessToken(auth_token,mHandler);
        }
    }
}
