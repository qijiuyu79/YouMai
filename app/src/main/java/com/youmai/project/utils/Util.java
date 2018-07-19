package com.youmai.project.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import com.youmai.project.application.MyApplication;

import java.io.File;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @author Administrator
 */
public class Util extends ClassLoader {
    public final static int NETWORK_PHONE = 1;
    public final static int NO_NETWORK = 0;
    public final static int NETWORK_WIFI = 2;



    /**
     * 密码强度校验的正则表达式 安全度低：1、全数字6~14位——\d{6,14} 2、全小写字符6~14位——[a-z]{6,14}
     * 3、全大写字符6~14位——[A-Z]{6,14} 安全度中：1、全数字15~18位——\d{15,18}
     * 2、全小写字符15~18位——[a-z]{15,18} 3、全大写字符15~18位——[A-Z]{15,18}
     * 4、小写字符+数字6~14位——[a-z\d]{6,14} 5、大写字符+数字6~14位——[A-Z\d]{6,14}
     * 6、大小写字符6~14位——[a-zA-Z]{6,14} 安全度高： 1、小写字符+数字15~18位——[a-z\d]{15,18}
     * 2、大写字符+数字15~18位——[A-Z\d]{15,18} 3、大小写字符15~18位——[a-zA-Z]{15,18}
     * 4、大小写字符+数字（至少6位）——[a-zA-Z\d]{6,}
     * <p>
     * <p>
     * 校验密码强度
     *
     * @param pwd 需要验证的密码
     * @return 验证结果（1-安全度低；2-安全度中；3-安全度高）
     */
    public static int checkPwdSafety(String pwd) {
        String[] expressions1 = {"\\d{6,14}", "[a-z]{6,14}", "[A-Z]{6,14}"};// 安全度低
        String[] expressions2 = {"\\d{15,18}", "[a-z]{15,18}", "[A-Z]{15,18}", "[a-z\\d]{6,14}", "[A-Z\\d]{6,14}", "[a-zA-Z]{6,14}"};// 安全度中
        String[] expressions3 = {"[a-z\\d]{15,18}", "[A-Z\\d]{15,18}", "[a-zA-Z]{15,18}", "[a-zA-Z\\d]{6,}"};// 安全度高

        int result = 0;
        CharSequence inputStr = pwd;
        // 低
        for (int i = 0; i < expressions1.length; i++) {
            Pattern pattern = Pattern.compile(expressions1[i]);
            Matcher matcher = pattern.matcher(inputStr);
            if (matcher.matches()) {
                result = 1;
                break;
            }
        }
        // 如果满足安全度低，返回校验结果
        if (result != 0) {
            return result;
        }
        // 中
        for (int i = 0; i < expressions2.length; i++) {
            Pattern pattern = Pattern.compile(expressions2[i]);
            Matcher matcher = pattern.matcher(inputStr);
            if (matcher.matches()) {
                result = 2;
                break;
            }
        }
        // 如果满足安全度中，返回校验结果
        if (result != 0) {
            return result;
        }
        // 高
        for (int i = 0; i < expressions3.length; i++) {
            Pattern pattern = Pattern.compile(expressions3[i]);
            Matcher matcher = pattern.matcher(inputStr);
            if (matcher.matches()) {
                result = 3;
                break;
            }
        }

        return result;
    }


    /**
     * 判断当前网络是否能用，并判断网络类型 当前网络类型�?无网络，1手机网，2wifi�?
     *
     * @param context
     */
    public static int isNetworkActive(Context context) {
        State wifiState = null;
        State mobileState = null;
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .getState();
        // wifi和手机网同时�?��时手机优先使用wifi网络
        if (wifiState != null && mobileState != null
                && State.CONNECTED != wifiState
                && State.CONNECTED == mobileState) {
            // 手机网络连接成功
            return Util.NETWORK_PHONE;
        } else if (wifiState != null && mobileState != null
                && State.CONNECTED != wifiState
                && State.CONNECTED != mobileState) {
            // 手机没有任何的网�?
            return Util.NO_NETWORK;
        } else {
            // 无线网络连接成功 (wifiState != null && State.CONNECTED == wifiState)
            return Util.NETWORK_WIFI;
        }
    }

    //当前网络是否可用
    public static boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) MyApplication.application.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if(null!=info && info.getState()== State.CONNECTED){
            return true;
        }
        return false;
    }

    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            // 有存储的SDCard
            return true;
        } else {
            return false;
        }
    }


    /**
     * 获取sd卡路径
     */
    public static String getSdcardPath() {
        String path = Environment.getExternalStorageDirectory() + File.separator + "youmai" + File.separator;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }


    /**
     * 判断是否输入表情符号
     * @param string
     * @return
     */
    public static boolean isEmoji(String string) {
        Pattern p = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(string);
        return m.find();
    }


    /**
     * 保留两位小数的double数据
     * @param d
     * @return
     */
    public static String setDouble(double d){
        final DecimalFormat df = new DecimalFormat("0.00");
        return df.format(d);
    }

    /**
     * 保留1位小数的double数据
     * @param d
     * @return
     */
    public static String setDoubleDotOne(double d){
        final DecimalFormat df = new DecimalFormat("0.0");
        return df.format(d);
    }


    /**
     * 抖动的动画特效
     * @return
     */
    public static Animation genDefaultAnimation() {
        TranslateAnimation animtion = new TranslateAnimation(0, 10, 0, 0);
        animtion.setInterpolator(new CycleInterpolator(3.0f));
        animtion.setDuration(1000);
        return animtion;
    }

    /**
     * 获取当前系统的版本号
     *
     * @return
     */
    public static int getVersionName(Context mContext) {
        try {
            // 获取packagemanager的实例
            PackageManager packageManager = mContext.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
            int version = packInfo.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 获取渠道名称
     * @return
     */
    public static String getChannel(Context mContext){
        final ApplicationInfo appInfo;
        try {
            appInfo = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
            final String utmSource = appInfo.metaData.get("SENSORS_ANALYTICS_UTM_SOURCE")+"";
            return utmSource;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 网络类型
     *
     * @return
     */
    public static String getNetTypeName(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager
                    .getActiveNetworkInfo();
            if (activeNetworkInfo == null) {
                return "无网络";
            }
            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return activeNetworkInfo.getTypeName();
            } else {
                String typeName = activeNetworkInfo.getSubtypeName();
                if (typeName == null || typeName.length() == 0) {
                    return "未知网络";
                } else if (typeName.length() > 3) {
                    return activeNetworkInfo.getSubtypeName().substring(0, 4);
                } else {
                    return activeNetworkInfo.getSubtypeName().substring(0, typeName.length());
                }
            }
        } else {
            return "无网络";
        }
    }

    /**
     *获取状态栏高度
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    /*
    * 设置控件所在的位置YY，并且不改变宽高，
    * XY为绝对位置
    */
    public static void setLayout(View view,int x,int y) {
        ViewGroup.MarginLayoutParams margin=new ViewGroup.MarginLayoutParams(view.getLayoutParams());
        margin.setMargins(x,y, x+margin.width, y+margin.height);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
        view.setLayoutParams(layoutParams);
    }


    private static final int MIN_CLICK_DELAY_TIME = 500;
    private static long lastClickTime;

    /**
     *  两次点击按钮之间的点击间隔不能少于700毫秒
     * @return
     */
    public static boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }

    /**
     * 只允许字母、数字和汉字
     * @param str
     * @return
     * @throws PatternSyntaxException
     */
    public static boolean stringFilter(String str)throws PatternSyntaxException {
        String   regEx  =  "^[\\u4e00-\\u9fa5*a-zA-Z0-9]+$";
        Pattern   p   =   Pattern.compile(regEx);
        Matcher   m   =   p.matcher(str);
        return   m.matches();
    }


    /**
     * 判断是否登陆
     * @param context
     * @return
     */
    public static boolean isLogin(Context context){
        final String auth_token= MyApplication.spUtil.getString(SPUtil.AUTH_TOKEN);
        if(!TextUtils.isEmpty(auth_token)){
            return true;
        }
        return false;
    }
}
