package com.youmai.project.utils;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.WindowManager;
import com.youmai.project.application.MyApplication;

import java.util.Map;

/**
 * 公共参数
 * Created by Administrator on 2017/2/4 0004.
 */

public class ParameterUtils {
    private static ParameterUtils pu;
    public static ParameterUtils getInstance() {
        if (pu == null) {
            pu = new ParameterUtils();
        }
        return pu;
    }

    public Map<String, String> getParameter(Map<String, String> map) {

        Context mContext = MyApplication.application;
        TelephonyManager telephonyMgr = (TelephonyManager) mContext.getSystemService(mContext.TELEPHONY_SERVICE);
        //设备唯一id
        String device_id = telephonyMgr.getDeviceId();
        //手机型号
        String device_type = android.os.Build.MODEL.replace("+","").replace(" ","").replace("'","");
        //操作系统版本
        String os_version = android.os.Build.VERSION.RELEASE.replace("+","").replace(" ","");
        //手机品牌
        String device_name = android.os.Build.BRAND.replace("+","").replace(" ","");
        //app版本号
        int app_version = Util.getVersionCode(mContext);
        //屏幕宽高
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        //网络类型
        String network_type = Util.getNetTypeName(mContext);
        map.put("device_id", device_id);
        map.put("device_type", device_type);
        map.put("os_name", "Android");
        map.put("os_version", os_version);
        map.put("device_name", device_name);
        map.put("app_version", String.valueOf(app_version));
        map.put("device_mac", "no");
        map.put("network_type", network_type);
        map.put("device_width", String.valueOf(width));
        map.put("device_height", String.valueOf(height));
        map.put("latitude", MyApplication.spUtil.getString(SPUtil.LOCATION_LAT));
        map.put("longitude", MyApplication.spUtil.getString(SPUtil.LOCATION_LONG));
        map.put("timestamp", (System.currentTimeMillis()/1000)+"");
        map.put("channel", Util.getChannel(mContext));
        map.put("access_token", MyApplication.spUtil.getString(SPUtil.ACCESS_TOKEN));
        return map;
    }

}
