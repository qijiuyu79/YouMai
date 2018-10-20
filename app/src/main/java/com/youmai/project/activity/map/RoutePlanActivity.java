package com.youmai.project.activity.map;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.application.MyApplication;
import com.youmai.project.utils.SPUtil;
import com.youmai.project.utils.StatusBarUtils;
import com.youmai.project.utils.SystemBarTintManager;
import com.youmai.project.utils.map.GetRoutePlan;

/**
 * 图片路径规划
 * Created by Administrator on 2018/10/20.
 */

public class RoutePlanActivity  extends BaseActivity {

    private MapView mapView;
    public BaiduMap mBaiduMap;
    //路径规划对象
    public GetRoutePlan getRoutePlan;
    //地图
    private RoutePlanSearch rpSearch = null;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        StatusBarUtils.transparencyBar(this);
        setContentView(R.layout.activity_routeplan);
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
    private void initView() {
        mapView = (MapView) findViewById(R.id.mapView);
        mBaiduMap = mapView.getMap();
        //路径规划
        rpSearch = RoutePlanSearch.newInstance();
        getRoutePlan = new GetRoutePlan(mBaiduMap);
        rpSearch.setOnGetRoutePlanResultListener(getRoutePlan);
        double latitude=getIntent().getDoubleExtra("latitude",0);
        double longtitude=getIntent().getDoubleExtra("longtitude",0);
        final double lat = Double.parseDouble(MyApplication.spUtil.getString(SPUtil.LOCATION_LAT));
        final double lon = Double.parseDouble(MyApplication.spUtil.getString(SPUtil.LOCATION_LONG));

        PlanNode stNode = PlanNode.withLocation(new LatLng(lat, lon));
        PlanNode enNode = PlanNode.withLocation(new LatLng(latitude, longtitude));
        rpSearch.walkingSearch((new WalkingRoutePlanOption()).from(stNode).to(enNode));

        findViewById(R.id.lin_back).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RoutePlanActivity.this.finish();
            }
        });
    }
}
