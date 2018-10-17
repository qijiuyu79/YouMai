package com.youmai.project.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ScrollView;
import android.widget.Toast;

import com.youmai.project.utils.photo.PicturesUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
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
        try {
            file = new File(newPath);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();

            File file1=new File(newPath);
            LogUtils.e(FileUtils.getFileSize(file1.length())+"______");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(null!=bitmap){
            bitmap.recycle();
            bitmap=null;
        }
        return newPath;
    }



    /**
     * 截屏webview
     */
    public static Bitmap screenshot(ScrollView scrollView) {
        int h = 0;
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
            scrollView.getChildAt(i).setBackgroundColor(Color.parseColor("#ffffff"));
        }
        Bitmap bitmap= Bitmap.createBitmap(scrollView.getWidth(), h, Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        return bitmap;
    }


    /**
     * 保存bitmap图片到本地
     * @param context
     * @param bmp
     */
    public static void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "youmai");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
//          context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
    }
}
