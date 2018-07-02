package com.youmai.project.utils;

import android.graphics.Bitmap;

import com.youmai.project.utils.photo.PicturesUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2018/5/31 0031.
 */

public class BitMapUtils {


    /**
     * 对图片进行压缩
     * @param file
     * @return
     */
    public static String  compressBitMap(File file){
        //将图片缩小为原来的一半
        Bitmap bitmap=FileUtils.getBitMapBy2(file.getPath(), 2);
        //对图片进行压缩
        bitmap = FileUtils.compressImage(bitmap);
        String newPath=Util.getSdcardPath()+System.currentTimeMillis()+"_"+(Math.random()*9+1)*1000+".jpg";
        LogUtils.e(newPath);
        try {
            file = new File(newPath);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(null!=bitmap){
            bitmap.recycle();
            bitmap=null;
        }
        return newPath;
    }
}
