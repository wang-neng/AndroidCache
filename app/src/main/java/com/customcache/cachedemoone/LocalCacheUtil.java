package com.customcache.cachedemoone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 将网络上获取下来的数据，保存在本地文件中
 * 本地缓存 File
 * SD卡  文件系统
 * Created by wn on 2018/8/1.
 */

public class LocalCacheUtil {

    private String cachePath;

    public LocalCacheUtil(Context context, String uniqueName) {
        cachePath = getCacheDirString(context, uniqueName);
    }

    /*获取SD缓存目录的路径：String类型*/
    private String getCacheDirString(Context context, String uniqueName) {
        File file = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            file = new File(context.getExternalCacheDir(), uniqueName);
            //file = new File(Environment.getExternalStorageDirectory(), uniqueName);
        } else {
            file = new File(context.getCacheDir(), uniqueName);
        }

        if (!file.exists()) {
            file.mkdirs();
        }

        return file.getAbsolutePath();
    }

    /**
     * 设置Bitmap数据到本地
     *
     * @param url
     * @param bitmap
     */
    public void setBitmapToLocal(String url, Bitmap bitmap) {
        FileOutputStream fos = null;
        try {
            String fileName = MD5Encoder.encode(url);
            File file = new File(cachePath, fileName);
            File parentFile = file.getParentFile();//获取上级所有目录
            if (!parentFile.exists()) {
                // 如果文件不存在，则创建文件夹
                parentFile.mkdirs();
            }
            fos = new FileOutputStream(file);
            // 将图片压缩到本地
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (fos != null) {
                try {
                    fos.close();//关闭流
                    fos = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 通过url获取Bitmap
     *
     * @param url
     */
    public Bitmap getBitmapFromLocal(String url) {
        try {
            File file = new File(cachePath, MD5Encoder.encode(url));
            if (file.exists()) {
                // 如果文件存在
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                //Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                return bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
