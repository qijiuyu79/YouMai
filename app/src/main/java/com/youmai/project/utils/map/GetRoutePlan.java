package com.youmai.project.utils.map;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.youmai.project.R;
import com.youmai.project.application.MyApplication;
import com.youmai.project.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 路径规划
 * Created by Administrator on 2017/3/27 0027.
 */

public class GetRoutePlan implements OnGetRoutePlanResultListener {

    private BaiduMap mBaiduMap;
    private BitmapDescriptor bitmap,bitmap2;

    public GetRoutePlan(BaiduMap baiduMap){
        this.mBaiduMap=baiduMap;
    }
    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {

        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);

            List<LatLng> points1 = new ArrayList<LatLng>();
            final WalkingRouteLine w=result.getRouteLines().get(0);
            List<WalkingRouteLine.WalkingStep> list=w.getAllStep();
            for(WalkingRouteLine.WalkingStep l:list){
                points1.addAll(l.getWayPoints());
            }
//            overlay.setData(w);
//            overlay.addToMap();
//            overlay.zoomToSpan();

            PolylineOptions ooPolyline1 = new PolylineOptions().width(10)
                    .color(MyApplication.application.getResources().getColor(R.color.color_FF4081)).points(points1);
            mBaiduMap.addOverlay(ooPolyline1);

            //构建起始位置图标
            bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.start_icon);
            MarkerOptions option = new MarkerOptions().position(points1.get(0)).icon(bitmap);
            mBaiduMap.addOverlay(option);

            //构建终点位置图标
            bitmap2 = BitmapDescriptorFactory.fromResource(R.mipmap.end_icon);
            MarkerOptions option2 = new MarkerOptions().position(points1.get(points1.size()-1)).icon(bitmap2);
            //在地图上添加Marker，并显示
            mBaiduMap.addOverlay(option2);

            MapStatus mMapStatus = new MapStatus.Builder().target(points1.get(0)).zoom(15.8f).build();
            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
            mBaiduMap.setMapStatus(mMapStatusUpdate);
            LogUtils.e(w.getDistance()+"++++++++++++++++++");
            if(w.getDistance()>300 && w.getDistance()<500){
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(19.0f),1000);
            }else if(w.getDistance()>500 && w.getDistance()<1000){
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(18.5f),1000);
            }else if(w.getDistance()>1000 && w.getDistance()<2000){
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(18.0f),1000);
            }else if(w.getDistance()>2000){
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(17.0f),1000);
            }
        }
    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

    }

    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {

    }

    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }


    private class MyWalkingRouteOverlay extends WalkingRouteOverlay {
        public MyWalkingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        public BitmapDescriptor getStartMarker() {
            return BitmapDescriptorFactory.fromResource(R.mipmap.start_icon);
        }

        public BitmapDescriptor getTerminalMarker() {
            return BitmapDescriptorFactory.fromResource(R.mipmap.end_icon);
        }
    }


    public void close(){
        if(null!=bitmap && null!=bitmap2){
            bitmap.recycle();
            bitmap2.recycle();
            bitmap=null;
            bitmap2=null;
        }
    }
}
