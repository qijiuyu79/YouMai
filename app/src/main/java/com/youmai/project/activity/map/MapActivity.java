package com.youmai.project.activity.map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
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
import com.youmai.project.activity.main.GoodDetailsActivity;
import com.youmai.project.adapter.MapGoodsListAdapter;
import com.youmai.project.application.MyApplication;
import com.youmai.project.bean.GoodsBean;
import com.youmai.project.bean.HttpBaseBean;
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
    private BitmapDescriptor bitmapDescriptor;
    //店铺对象
    private Store store;
    private List<ImageView> imgList=new ArrayList<>();
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
        findViewById(R.id.lin_sign).setOnClickListener(this);
    }


    /**
     * 开始定位
     */
    private void startLocation(){
        showProgress("定位中...");
        GetLocation.getInstance().stopLocation();
        GetLocation.getInstance().setLocation(mBaiduMap,mContext,mHandler);
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
                case HandlerConstant.GET_STORE_INFO_SUCCESS:
                     clearTask();
                     final List<GoodsBean> list= JsonUtils.getMapGoods(msg.obj.toString());
                     if(list.size()>0){
                         View view= LayoutInflater.from(mContext).inflate(R.layout.map_bottom_goods,null);
                         bottomPopupWindow(0,0,view);
                         TextView tvNickName=(TextView)view.findViewById(R.id.tv_am_nickName);
                         ImageView imgX1=(ImageView)view.findViewById(R.id.img_au_x1);
                         ImageView imgX2=(ImageView)view.findViewById(R.id.img_au_x2);
                         ImageView imgX3=(ImageView)view.findViewById(R.id.img_au_x3);
                         ImageView imgX4=(ImageView)view.findViewById(R.id.img_au_x4);
                         ImageView imgX5=(ImageView)view.findViewById(R.id.img_au_x5);
                         imgList.add(imgX1);
                         imgList.add(imgX2);
                         imgList.add(imgX3);
                         imgList.add(imgX4);
                         imgList.add(imgX5);
                         //设置昵称
                         tvNickName.setText(store.getNickname());
                         //设置星级
                         setXing(store.getCreditLevel());
                         MyGridView myGridView=(MyGridView)view.findViewById(R.id.mg_am_goods);
                         MapGoodsListAdapter mapGoodsListAdapter=new MapGoodsListAdapter(mContext,list);
                         myGridView.setAdapter(mapGoodsListAdapter);
                         //进入商品详情页
                         myGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                 GoodsBean goodsBean=list.get(position);
                                 Intent intent=new Intent(mContext, GoodDetailsActivity.class);
                                 intent.putExtra("goodsBean",goodsBean);
                                 startActivity(intent);
                             }
                         });

                         //查看更多商品
                         view.findViewById(R.id.lin_search).setOnClickListener(new View.OnClickListener() {
                             public void onClick(View v) {
                                 Intent intent=new Intent();
                                 Bundle bundle=new Bundle();
                                 intent.setClass(mContext, SellerGoodsActivity.class);
                                 bundle.putSerializable("goodsBean",list.get(0));
                                 intent.putExtras(bundle);
                                 startActivity(intent);

                             }
                         });

                         //关闭弹框
                         view.findViewById(R.id.rel_mbg).setOnClickListener(new View.OnClickListener() {
                             public void onClick(View v) {
                                 mPopuwindow.dismiss();
                             }
                         });
                     }
                     break;
                //店铺签到
                case HandlerConstant.STORE_EVALUATE_SUCCESS:
                     clearTask();
                     HttpBaseBean httpBaseBean= (HttpBaseBean) msg.obj;
                     if(null==httpBaseBean){
                        return;
                     }
                     if(httpBaseBean.isSussess()){
                         showMsg("签到成功！");
                         //查询附近的店铺
                         getStore();
                     }else{
                        showMsg(httpBaseBean.getMsg());
                     }
                     break;
                 default:
                     break;
            }
        }
    };


    /**
     * 设置星级
     */
    private void setXing(int index){
        for (int i=0;i<imgList.size();i++){
            if(i<index){
                imgList.get(i).setImageDrawable(getResources().getDrawable(R.mipmap.yes_select_x));
            }else{
                imgList.get(i).setImageDrawable(getResources().getDrawable(R.mipmap.no_select_x));
            }
        }
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
            store=list.get(index);
            if(null==store){
                return false;
            }
            showProgress("商品查询中...");
            HttpMethod.getStoreInfo(store.getId(),1,3,HandlerConstant.GET_STORE_INFO_SUCCESS,mHandler);
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
            //店铺签到
            case R.id.lin_sign:
                 showProgress("签到中...");
                 HttpMethod.storeEvaluate(mHandler);
                 break;
            default:
                break;
        }
    }


    /**
     * 查询附近的店铺
     */
    private void getStore(){
        showProgress("店铺查询中...");
        HttpMethod.getNearStore(mHandler);
    }


    /**
     * 解析数据并设置mark
     * @param msg
     */
    private View markerView;
    private ViewHolder holder = null;
    private void setMark(String msg){
        //清空地图
        mBaiduMap.clear();
        //解析json数据
        JsonUtils.getStoreList(msg,list);
        for (int i=0,len=list.size();i<len;i++){
            if(null==markerView){
                holder = new ViewHolder();
                markerView=LayoutInflater.from(mContext).inflate(R.layout.map_marker,null);
                holder.tvNickName=(TextView)markerView.findViewById(R.id.tv_nickName);
                markerView.setTag(holder);
            }else{
                holder=(ViewHolder)markerView.getTag();
            }
            final Store store=list.get(i);
            if(null==store){
                return;
            }
            holder.tvNickName.setText(store.getNickname());
            bitmapDescriptor=BitmapDescriptorFactory.fromView(markerView);
            MarkerOptions op = new MarkerOptions().position(new LatLng(store.getLatitude(), store.getLongitude())).icon(bitmapDescriptor).zIndex(store.getPosition()).animateType(MarkerOptions.MarkerAnimateType.grow);
            mBaiduMap.addOverlay(op);
        }
    }


    private class ViewHolder{
        private TextView tvNickName;
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
            if(distance>2000){
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



    @Override
    public void onStart() {
        super.onStart();
        myOrientationListener.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        myOrientationListener.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    public void onPause() {
        super.onPause();
        mapView.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeHandler(mHandler);
    }
}
