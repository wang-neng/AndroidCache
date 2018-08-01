package com.customcache.cachedemothree;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 网络访问工具类
 * Created by wn on 2018/8/2.
 */
public class HttpUtils {
    /**
     * 判断网络连接是否通畅
     * @param mContext
     * @return
     */
    public static boolean isNetConn(Context mContext) {
        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null) {
            return info.isConnected();
        } else {
            return false;
        }
    }

    /**
     * 根据path下载网络上的数据
     * @param path  路径
     * @return  返回下载内容的byte数据形式
     */
    public static byte[] getDateFromNet(String path) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setDoInput(true);
            conn.connect();
            if (conn.getResponseCode()==200) {
                InputStream is = conn.getInputStream();
                byte b[] = new byte[1024];
                int len;
                while ((len=is.read(b))!=-1) {
                    baos.write(b, 0, len);
                }
                return baos.toByteArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

}
