package com.youmai.project.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MyLocationData;
import com.youmai.project.application.MyApplication;

/**
 * 定位
 * Created by Administrator on 2017/3/15 0015.
 */

public class GetLocation {

    private static GetLocation getLocation;
    private LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private Handler handler;
    private static boolean isLocation=false;
    public static GetLocation getInstance() {
        if (null == getLocation) {
            getLocation = new GetLocation();
        }
        return getLocation;
    }

    /**
     * 设置定位
     */
    public void setLocation(Context mContext, Handler handler) {
        this.handler = handler;
        mLocClient = new LocationClient(mContext.getApplicationContext());
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setScanSpan(10000);
        option.setIsNeedAddress(true);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }


    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {
        public void onReceiveLocation(BDLocation location) {
            //GPS定位成功、网络定位成功、离线定位成功

            if (location.getLocType() == BDLocation.TypeGpsLocation ||
                    location.getLocType() == BDLocation.TypeNetWorkLocation ||
                    location.getLocType() == BDLocation.TypeOffLineLocation) {
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(location.getRadius())
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .latitude(location.getLatitude())
                        .longitude(location.getLongitude()).build();

                final Double longtitude = location.getLongitude();
                final Double latitude = location.getLatitude();
                MyApplication.spUtil.addString(SPUtil.LOCATION_LAT, latitude + "");
                MyApplication.spUtil.addString(SPUtil.LOCATION_LONG, longtitude + "");
                MyApplication.spUtil.addString(SPUtil.LOCATION_ADDRESS,location.getAddrStr());
            }
            if(!isLocation){
                isLocation=true;
                Message message = new Message();
                message.what = 0x00;
                handler.sendMessage(message);
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    /**
     * 停止定位
     */
    public void stopLocation() {
        if (null != mLocClient) {
            mLocClient.unRegisterLocationListener(myListener);
            mLocClient.stop();
            mLocClient=null;
        }
    }
}
