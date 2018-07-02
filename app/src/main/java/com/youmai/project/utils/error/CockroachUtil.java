package com.youmai.project.utils.error;

import android.os.Handler;
import android.os.Looper;

/**
 * 异常捕获类
 * Created by Administrator on 2017/12/2 0002.
 */

public class CockroachUtil {

    /**
     * 安装
     */
    public static void install(){
        Cockroach.install(new Cockroach.ExceptionHandler() {
            public void handlerException(final Thread thread, final Throwable throwable) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        try {
                            throwable.printStackTrace();
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }


    /**
     * 卸载
     */
    public static void clear(){
        Cockroach.uninstall();
        install();
    }
}
