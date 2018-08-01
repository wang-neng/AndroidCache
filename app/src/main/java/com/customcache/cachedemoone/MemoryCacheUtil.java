package com.customcache.cachedemoone;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * 主要是用到了LruCache这个类，这个类比较适合用来缓存图片，它会将强引用对象放在LinkedHashMap中，
 * 当缓存数据大小达到预定值的时候会将在该集合中比较少使用的对象从内存中移除。
 * 内存缓存
 * Created by wn on 2018/8/1.
 */

public class MemoryCacheUtil {
    private LruCache<String,Bitmap> mLruCache ;

    public MemoryCacheUtil() {
        // maxMemory 是允许的最大值 ，超过这个最大值，则会回收
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory/8;
        mLruCache = new LruCache<String, Bitmap>(cacheSize){

            /**
             * 计算每张图片的大小
             * @param key     图片的url作为key
             * @param bitmap  图片的值为value
             * @return
             */
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };

    }

    /**
     * 通过url从内存中获取图片
     * @param url
     * @return
     */
    public Bitmap getBitmapFromMemory(String url) {
        return mLruCache.get(url);
    }

    /**
     * 设置Bitmap到内存
     *
     * @param url
     * @param bitmap
     */
    public void setBitmapToMemory(String url, Bitmap bitmap) {
        if (getBitmapFromMemory(url) == null) {
            mLruCache.put(url, bitmap); // 设置图片
        }
    }

    /**
     * 从内存中删除指定的Bitmap
     *
     * @param key
     */
    public void removeBitmapFromMemory(String key) {
        mLruCache.remove(key);
    }


}
