package com.youmai.project.activity.map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.youmai.project.adapter.MapGoodsListAdapter;
import com.youmai.project.application.MyApplication;
import com.youmai.project.bean.GoodsBean;
import com.youmai.project.bean.Store;
import com.youmai.project.http.HandlerConstant;
import com.youmai.project.http.HttpMethod;
import com.youmai.project.utils.JsonUtils;
import com.youmai.project.utils.SPUtil;
import com.youmai.project.utils.StatusBarUtils;
import com.youmai.project.utils.Util;
import com.youmai.project.utils.map.GetLocation;
import com.youmai.project.utils.map.MyOrientationListener;
import com.youmai.project.view.MyGridView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * 逛街
 */
public class MapActivity extends BaseActivity implements OnGetGeoCoderResultListener,BaiduMap.OnMarkerClickListener,View.OnClickListener {

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
        byLatLocation();
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
        mBaiduMap.setOnMarkerClickListener(this);
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
        findViewById(R.id.img_am_location).setOnClickListener(this);
    }

    /**
     * 根据经纬度定位
     */
    private void byLatLocation(){
        final String latitude= MyApplication.spUtil.getString(SPUtil.LOCATION_LAT);
        final String longtitude= MyApplication.spUtil.getString(SPUtil.LOCATION_LONG);
        finishLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longtitude));
        //根据经纬度去定位
        mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(finishLng));
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
                     clearTask();
                     //解析数据并设置mark
                     setMark(msg.obj.toString());
                     break;
                //根据店铺id查询商品信息
                case HandlerConstant.GET_GOODS_BY_STOREID_SUCCESS:
                     clearTask();
                     List<GoodsBean> list= JsonUtils.getGoods(msg.obj.toString());
                     if(list.size()>0){
                         View view= LayoutInflater.from(mContext).inflate(R.layout.map_bottom_goods,null);
                         TextView tvNickName=(TextView)view.findViewById(R.id.tv_am_nickName);
                         MyGridView myGridView=(MyGridView)view.findViewById(R.id.mg_am_goods);
                         view.findViewById(R.id.rel_mbg).setOnClickListener(new View.OnClickListener() {
                             public void onClick(View v) {
                                 mPopuwindow.dismiss();
                             }
                         });
                         bottomPopupWindow(0,0,view);
                         MapGoodsListAdapter mapGoodsListAdapter=new MapGoodsListAdapter(mContext,list);
                         myGridView.setAdapter(mapGoodsListAdapter);
                     }
                     break;
                 default:
                     break;
            }
        }
    };


    /**
     * marker点击事件
     * @param marker
     * @return
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        if (null != marker) {
            final int index = marker.getZIndex();
            final Store store=list.get(index);
            if(null==store){
                return false;
            }
            showProgress("商品查询中...",true);
            HttpMethod.getGoodsByStoreId(1,store.getId(),HandlerConstant.GET_GOODS_BY_STOREID_SUCCESS,mHandler);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //重新定位
            case R.id.img_am_location:
                isFirst=false;
                startLocation();
                break;
        }
    }


    /**
     * 查询附近的店铺
     */
    private void getStore(){
        showProgress("店铺查询中...",true);
        HttpMethod.getNearStore(mHandler);
    }


    /**
     * 解析数据并设置mark
     * @param msg
     */
    private void setMark(String msg){
        try {
            list.clear();
            mBaiduMap.clear();
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
     * 定位回调
     * @param geoCodeResult
     */
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }
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
