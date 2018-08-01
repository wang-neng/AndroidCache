package com.customcache.cachedemothree;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 操作外部存储的工具类
 * Created by wn on 2018/8/2.
 */
public class ExternalStorageUtils {
    /**
     * 将传递过来的图片byte数组存储到sd卡中
     * @param imgName  图片的名字
     * @param buff  byte数组
     * @return  返回是否存储成功
     */
    public static boolean storeToSDRoot(String imgName, byte buff[]) {
        boolean b = false;
        String basePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(basePath, imgName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(buff);
            fos.close();
            b = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
    }

    /**
     * 从本地内存中根据图片名字获取图片
     * @param imgName  图片名字
     * @return  返回图片的Bitmap格式
     */
    public static Bitmap getImgFromSDRoot(String imgName) {
        Bitmap bitmap = null;
        String basePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(basePath, imgName);
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte b[] = new byte[1024];
            int len;
            while ((len = fis.read(b)) != -1) {
                baos.write(b, 0, len);
            }
            byte buff[] = baos.toByteArray();
            if (buff != null && buff.length != 0) {
                bitmap = BitmapFactory.decodeByteArray(buff, 0, buff.length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }


}
