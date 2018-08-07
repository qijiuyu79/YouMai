package com.youmai.project.utils.map;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MyLocationData;
import com.youmai.project.application.MyApplication;
import com.youmai.project.utils.SPUtil;
import com.youmai.project.view.DialogView;

/**
 * 定位
 * Created by Administrator on 2017/3/15 0015.
 */

public class GetLocation {

    private static GetLocation getLocation;
    private LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private Handler handler;
    private Context mContext;
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
        this.mContext=mContext;
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
            Message message = new Message();
            message.what = 0x00;
            handler.sendMessage(message);
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }


    /**
     * 开启定位权限
     */
    private DialogView dialogView;
    public void openLocation() {
        if (null != dialogView) {
            return;
        }
        dialogView = new DialogView(mContext, "无法定位，请开启定位权限或者打开GPS！", "去打开", "取消", new View.OnClickListener() {
            public void onClick(View v) {
                dialogView.dismiss();
                dialogView=null;
                stopLocation();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + mContext.getPackageName()));
                mContext.startActivity(intent);
            }
        }, new View.OnClickListener(){
            public void onClick(View v) {
                dialogView.dismiss();
                dialogView=null;
                stopLocation();
            }
        });
        dialogView.show();
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
