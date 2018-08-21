package com.youmai.project.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具类
 * Created by lyn on 2017/3/7.
 */

public class DateUtil {
    /**
     * 格式到天
     *
     * @param time
     * @return
     */
    public static String getDay(long time) {
        return new SimpleDateFormat("yyyy-MM-dd").format(time);
    }


    /**
     * 格式到天
     *
     * @param time
     * @return
     */
    public static String getData(long time) {
        if (time <= 0) {
            return "";
        }
        return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(time);
    }


    /**
     * 格式到年
     *
     * @param time
     * @return
     */
    public static String getTime(long time) {
        if (time <= 0) {
            return "";
        }
        return new SimpleDateFormat("dd/MM/yyyy").format(time);
    }

    /**
     * 格式月日
     *
     * @param time
     * @return
     */
    public static String getMd(long time) {
        if (time <= 0) {
            return "";
        }
        return new SimpleDateFormat("MM/dd").format(time);
    }


    /**
     * 把long 转换成 日期 再转换成String类型
     */
    public static String transferLongToDate(long millSec, String str) {
        SimpleDateFormat sdf = new SimpleDateFormat(str);
        Date date = new Date(millSec);
        return sdf.format(date);
    }


    /**
     * 判断周几
     *
     * @return
     */
    public static int weekday() {
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        return c.get(Calendar.DAY_OF_WEEK);
    }
}
