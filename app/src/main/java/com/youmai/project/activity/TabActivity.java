package com.youmai.project.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import com.youmai.project.Application.MyApplication;
import com.youmai.project.R;
import com.youmai.project.activity.center.CenterActivity;
import com.youmai.project.activity.main.MainActivity;
import com.youmai.project.activity.order.OrderActivity;
import com.youmai.project.activity.shop.ShoppingActivity;
import com.youmai.project.activity.user.LoginActivity;
import com.youmai.project.activity.user.UserActivity;
import com.youmai.project.utils.ActivitysLifecycle;
import com.youmai.project.utils.GetLocation;
import com.youmai.project.utils.LogUtils;
import com.youmai.project.utils.SPUtil;
import com.youmai.project.utils.SystemBarTintManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/18 0018.
 */

public class TabActivity extends android.app.TabActivity implements View.OnClickListener{

    private TabHost tabHost;
    private ImageView imgMain,imgShop,imgOrder,imgUser;
    private TextView tvMain,tvShop,tvOrder,tvUser;
    private List<ImageView> imgList=new ArrayList<>();
    private List<TextView> tvList=new ArrayList<>();
    // 按两次退出
    protected long exitTime = 0;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.tag_host);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //系统版本大于19
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.color_FF4081);
        initView();
    }

    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;        // a|=b的意思就是把a和b按位或然后赋值给a   按位或的意思就是先把a和b都换成2进制，然后用或操作，相当于a=a|b
        } else {
            winParams.flags &= ~bits;        //&是位运算里面，与运算  a&=b相当于 a = a&b  ~非运算符
        }
        win.setAttributes(winParams);
    }


    /**
     * 初始化控件
     */
    private void initView(){
        imgMain=(ImageView)findViewById(R.id.img_tab_main);
        imgShop=(ImageView)findViewById(R.id.img_tab_shop);
        imgOrder=(ImageView)findViewById(R.id.img_tab_order);
        imgUser=(ImageView)findViewById(R.id.img_tab_user);
        tvMain=(TextView) findViewById(R.id.tv_tab_main);
        tvShop=(TextView) findViewById(R.id.tv_tab_shop);
        tvOrder=(TextView) findViewById(R.id.tv_tab_order);
        tvUser=(TextView) findViewById(R.id.tv_tab_user);
        tabHost=this.getTabHost();
        TabHost.TabSpec spec;
        if(tabHost!=null){
            spec=tabHost.newTabSpec("首页").setIndicator("首页").setContent(new Intent(this, MainActivity.class));
            tabHost.addTab(spec);
            spec=tabHost.newTabSpec("逛街").setIndicator("逛街").setContent(new Intent(this, ShoppingActivity.class));
            tabHost.addTab(spec);
            spec=tabHost.newTabSpec("发布").setIndicator("发布").setContent(new Intent(this, CenterActivity.class));
            tabHost.addTab(spec);
            spec=tabHost.newTabSpec("订单").setIndicator("订单").setContent(new Intent(this, OrderActivity.class));
            tabHost.addTab(spec);
            spec=tabHost.newTabSpec("我的").setIndicator("我的").setContent(new Intent(this, UserActivity.class));
            tabHost.addTab(spec);
            tabHost.setCurrentTab(0);
        }
        imgList.add(imgMain);
        imgList.add(imgShop);
        imgList.add(imgOrder);
        imgList.add(imgUser);
        tvList.add(tvMain);
        tvList.add(tvShop);
        tvList.add(tvOrder);
        tvList.add(tvUser);
        findViewById(R.id.lin_tab_main).setOnClickListener(this);
        findViewById(R.id.lin_tab_shop).setOnClickListener(this);
        findViewById(R.id.lin_tab_center).setOnClickListener(this);
        findViewById(R.id.lin_tab_order).setOnClickListener(this);
        findViewById(R.id.lin_tab_user).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //首页
            case R.id.lin_tab_main:
                 tabHost.setCurrentTabByTag("首页");
                 imgMain.setImageDrawable(getResources().getDrawable(R.mipmap.tab_main_click_icon));
                 tvMain.setTextColor(getResources().getColor(R.color.color_FF4081));
                 updateColor(0);
                 break;
            //逛街
            case R.id.lin_tab_shop:
                 tabHost.setCurrentTabByTag("逛街");
                 imgShop.setImageDrawable(getResources().getDrawable(R.mipmap.tab_shop_click_icon));
                 tvShop.setTextColor(getResources().getColor(R.color.color_FF4081));
                 updateColor(1);
                 break;
            //中心
            case R.id.lin_tab_center:
                 tabHost.setCurrentTabByTag("发布");
                 updateColor(5);
                 break;
            //订单
            case R.id.lin_tab_order:
                 tabHost.setCurrentTabByTag("订单");
                 imgOrder.setImageDrawable(getResources().getDrawable(R.mipmap.tab_car_click_icon));
                 tvOrder.setTextColor(getResources().getColor(R.color.color_FF4081));
                 updateColor(2);
                 break;
            //我的
            case R.id.lin_tab_user:
                 tabHost.setCurrentTabByTag("我的");
                 imgUser.setImageDrawable(getResources().getDrawable(R.mipmap.tab_user_click_icon));
                 tvUser.setTextColor(getResources().getColor(R.color.color_FF4081));
                 if(TextUtils.isEmpty(MyApplication.spUtil.getString(SPUtil.ACCESS_TOKEN))){
                     Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                     startActivity(intent);
                 }else{
                     updateColor(3);
                 }
                 break;
            default:
                break;
        }
    }


    private void updateColor(int index){
        ImageView imageView;
        TextView textView;
        for (int i=0;i<imgList.size();i++){
             if(i!=index){
                 textView=tvList.get(i);
                 if(null!=textView){
                     textView.setTextColor(getResources().getColor(R.color.color_8a8a8a));
                 }
                 imageView=imgList.get(i);
                 if(null!=imageView){
                     switch (i){
                         case 0:
                              imgMain.setImageDrawable(getResources().getDrawable(R.mipmap.tab_main_icon));
                              break;
                         case 1:
                              imgShop.setImageDrawable(getResources().getDrawable(R.mipmap.tab_shop_icon));
                              break;
                         case 2:
                             imgOrder.setImageDrawable(getResources().getDrawable(R.mipmap.tab_car_icon));
                             break;
                         case 3:
                             imgUser.setImageDrawable(getResources().getDrawable(R.mipmap.tab_user_icon));
                             break;
                         default:break;
                     }
                 }
             }
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN ) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(),"再按一次退出程序!",Toast.LENGTH_LONG).show();
                exitTime = System.currentTimeMillis();
            } else {
                ActivitysLifecycle.getInstance().exit();
                GetLocation.getInstance().stopLocation();
            }
            return false;
        }
        return super.dispatchKeyEvent(event);
    }
}
