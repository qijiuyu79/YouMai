package com.youmai.project.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

/**
 * Created by lyn on 2017/5/20.
 */

public class FileUtils {
    public static void mkdirsPath(String path) {
        File file = new File(path);
        if (file == null) {
            return;
        }
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static boolean fileExists(String path) {
        File file = new File(path);
        if (file == null) {
            return false;
        }
        return file.exists();
    }

    /**
     * 截取url的文件名作为本地存储的文件名
     */
    public static String getFileName(String path) {
        String fileName = "";
        if (TextUtils.isEmpty(path) || !path.contains("/")) {
            return fileName;
        }
        try {
            fileName = path.substring(path.lastIndexOf("/") + 1);
        } catch (Exception e) {
            fileName = "";
        }
        return fileName;
    }


    public static long getFileSizes(File f) throws Exception {
        long s = 0;
        if (f.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(f);
            s = fis.available();
        } else {
            f.createNewFile();
        }
        return s;
    }


    /**
     * 将图片的长和宽缩小味原来的1/2
     * @param imgPath
     * @return
     */
    public static Bitmap getBitMapBy2(String imgPath,int potions){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inSampleSize = potions;
        return BitmapFactory.decodeFile(imgPath,options);
    }


    /**
     * 质量压缩方法
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 60;
        while (baos.toByteArray().length / 1024 > 200) { // 循环判断如果压缩后图片是否大于1000kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            options -= 10;// 每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }


    /**
     * 如果图片是content开头，就获取该图片地址
     * @param mContext
     * @param uri
     * @return
     */
    public static Uri getFileUri(Context mContext,Uri uri){
        try {
            if (uri.getScheme().equals("file")) {
                String path = uri.getEncodedPath();
                if (path != null) {
                    path = Uri.decode(path);
                    ContentResolver cr = mContext.getContentResolver();
                    StringBuffer buff = new StringBuffer();
                    buff.append("(")
                            .append(MediaStore.Images.ImageColumns.DATA)
                            .append("=")
                            .append("'" + path + "'")
                            .append(")");
                    Cursor cur = cr.query(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            new String[] { MediaStore.Images.ImageColumns._ID },
                            buff.toString(), null, null);
                    int index = 0;
                    for (cur.moveToFirst(); !cur.isAfterLast(); cur
                            .moveToNext()) {
                        index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                        index = cur.getInt(index);
                    }
                    if (index == 0) {
                    } else {
                        Uri uri_temp = Uri
                                .parse("content://media/external/images/media/"
                                        + index);
                        if (uri_temp != null) {
                            uri = uri_temp;
                        }
                    }
                }
            }
        }catch (Exception e){

        }
        return uri;
    }


    /**
     * 获取文件几M
     * @param fileS
     * @return
     */
    public static double getFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = df.format((double) fileS / 1048576);
        return Double.parseDouble(fileSizeString);
    }


    /**
     * 获取文件大小
     * @param fileS
     * @return
     */
    public static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

}
