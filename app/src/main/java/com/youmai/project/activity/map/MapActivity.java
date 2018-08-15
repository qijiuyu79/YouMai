package com.youmai.project.activity.map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ZoomControls;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.youmai.project.R;
import com.youmai.project.activity.BaseActivity;
import com.youmai.project.application.MyApplication;
import com.youmai.project.bean.Store;
import com.youmai.project.http.HandlerConstant;
import com.youmai.project.http.HttpMethod;
import com.youmai.project.utils.SPUtil;
import com.youmai.project.utils.StatusBarUtils;
import com.youmai.project.utils.Util;
import com.youmai.project.utils.map.GetLocation;
import com.youmai.project.utils.map.MyOrientationListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 逛街
 */
public class MapActivity extends BaseActivity implements OnGetGeoCoderResultListener,BaiduMap.OnMarkerClickListener {

    private MapView mapView;
    public BaiduMap mBaiduMap;
    private GeoCoder mSearch = null;
    //是否定位成功
    private boolean isFirst=false;
    //中心点坐标
    private LatLng finishLng;
    //附近店铺数据
    private List<Store> list=new ArrayList<>();
    //店铺图标
    private BitmapDescriptor bitmap;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        StatusBarUtils.transparencyBar(this);
        setContentView(R.layout.activity_map);
        initView();
        initOritationListener();
        startLocation();
    }


    /**
     * 初始化控件
     */
    private void initView(){
        bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.dianpu_icon);
        mapView=(MapView)findViewById(R.id.mapView);
        mBaiduMap = mapView.getMap();
        // 根据经纬度搜索
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);
        // 注册触摸事件
        mBaiduMap.setOnMapStatusChangeListener(new Maptouch());
        //去除百度logo
        int count = mapView.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mapView.getChildAt(i);
            if (child instanceof ZoomControls || child instanceof ImageView) {
                child.setVisibility(View.INVISIBLE);
            }
        }
        //隐藏缩放按钮
        mapView.showZoomControls(false);
    }


    /**
     * 开始定位
     */
    private void startLocation(){
        showProgress("定位中...",true);
        GetLocation.getInstance().stopLocation();
        GetLocation.getInstance().setLocation(mContext,mHandler);
    }


    private Handler mHandler=new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                //定位
                case 0x00:
                    final String latitude= MyApplication.spUtil.getString(SPUtil.LOCATION_LAT);
                    final String longtitude= MyApplication.spUtil.getString(SPUtil.LOCATION_LONG);
                    if(TextUtils.isEmpty(latitude) && TextUtils.isEmpty(longtitude)){
                        clearTask();
                        //去打开定位权限
                        GetLocation.getInstance().openLocation();
                        return;
                    }
                    if (!isFirst) {
                        isFirst = true;
                        finishLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longtitude));
                        //根据经纬度去定位
                        mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(finishLng));
                    }
                    break;
                //查询附近的店铺
                case HandlerConstant.GET_NEAR_STORE_SUCCESS:
                     //解析数据并设置mark
                     setMark(msg.obj.toString());
                     break;
                 default:
                     break;
            }
        }
    };


    /**
     * 定位回调
     * @param geoCodeResult
     */
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        clearTask();
        if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
            return;
        }
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(reverseGeoCodeResult.getLocation()));
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(18f), 500);
        //查询附近的店铺
        getStore();
    }


    /**
     * marker点击事件
     * @param marker
     * @return
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        if (null != marker) {
            final int index = marker.getZIndex();
        }
        return true;
    }


    /**
     * 地图触摸状态改变监听(地图移动获取坐标)
     */
    public class Maptouch implements BaiduMap.OnMapStatusChangeListener {
        public void onMapStatusChange(MapStatus mapStatus) {
        }

        public void onMapStatusChangeFinish(MapStatus mapStatus) {
            LatLng finishLng2 = mapStatus.target;// 获取地图中心点坐标
            // 计算距离
            Double distance = Util.GetShortDistance(finishLng.longitude, finishLng.latitude, finishLng2.longitude, finishLng2.latitude);
            if(distance>500){
                //查询附近的店铺
                getStore();
            }
            return;
        }

        public void onMapStatusChangeStart(MapStatus arg0) {
        }

        @Override
        public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

        }
    }


    /**
     * 查询附近的店铺
     */
    private void getStore(){
        HttpMethod.getNearStore(mHandler);
    }


    /**
     * 解析数据并设置mark
     * @param msg
     */
    private void setMark(String msg){
        try {
            list.clear();
            final JSONObject jsonObject=new JSONObject(msg);
            final JSONArray jsonArray=new JSONArray(jsonObject.getString("data"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1=jsonArray.getJSONObject(i);
                Store store=new Store();
                store.setId(jsonObject1.getString("id"));
                //解析经纬度
                JSONObject jsonObject2=new JSONObject(jsonObject1.getString("location"));
                if(null==jsonObject2){
                    return;
                }
                JSONArray jsonArray2=new JSONArray(jsonObject2.getString("coordinates"));
                for (int k = 0; k < jsonArray2.length(); k++) {
                    if(k==0){
                        store.setLongitude(jsonArray2.getDouble(k));
                    }else{
                        store.setLatitude(jsonArray2.getDouble(k));
                    }
                }
                MarkerOptions op = new MarkerOptions().position(new LatLng(store.getLatitude(), store.getLongitude())).icon(bitmap).zIndex(i).animateType(MarkerOptions.MarkerAnimateType.grow);
                mBaiduMap.addOverlay(op);
                list.add(store);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 传感器监听
     **/
    MyOrientationListener myOrientationListener;

    private void initOritationListener() {
        myOrientationListener = new MyOrientationListener(MapActivity.this);
        myOrientationListener
                .setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
                    public void onOrientationChanged(float x) {
                        if (mBaiduMap == null || mBaiduMap.getLocationData() == null) {
                            return;
                        }
                        // 构造定位数据
                        MyLocationData locData = new MyLocationData.Builder()
                                .accuracy(mBaiduMap.getLocationData().accuracy)
                                // 此处设置开发者获取到的方向信息，顺时针0-360
                                .direction((int) x)
                                .latitude(mBaiduMap.getLocationData().latitude)
                                .longitude(mBaiduMap.getLocationData().longitude).build();
                        // 设置定位数据
                        mBaiduMap.setMyLocationData(locData);
                    }
                });
    }
}
