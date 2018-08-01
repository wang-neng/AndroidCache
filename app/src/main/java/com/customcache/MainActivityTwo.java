package com.customcache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.customcache.cachedemothree.ExternalStorageUtils;
import com.customcache.cachedemothree.HttpUtils;

import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;

/**
 *
 * Created by wn on 2018/8/2.
 */

public class MainActivityTwo extends AppCompatActivity implements View.OnClickListener {

    private Button btn_download;
    private ImageView iv_img;
    private MyLruCache myLruCache;
    private LinkedHashMap<String, SoftReference<Bitmap>> cashMap = new LinkedHashMap<>();
    private static final String TAG = "MainActivity";
    private String imgPath = "http://www.3dmgame.com/UploadFiles/201212/Medium_20121217143424221.jpg";
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bitmap bitmap = (Bitmap) msg.obj;
            iv_img.setImageBitmap(bitmap);
            Toast.makeText(MainActivityTwo.this, "从网络上下载图片", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_two);
        initView();
        int totalMemory = (int) Runtime.getRuntime().maxMemory();
        int size = totalMemory / 8;
        myLruCache = new MyLruCache(size);
        btn_download.setOnClickListener(this);
    }

    private void initView() {
        btn_download = (Button) findViewById(R.id.btn_download);
        iv_img = (ImageView) findViewById(R.id.iv_img);
    }

    @Override
    public void onClick(View v) {
        Bitmap b = getImgCache();
        if (b != null) {
            iv_img.setImageBitmap(b);
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (HttpUtils.isNetConn(MainActivityTwo.this)) {
                        byte b[] = HttpUtils.getDateFromNet(imgPath);
                        if (b != null && b.length != 0) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                            Message msg = Message.obtain();
                            msg.obj = bitmap;
                            handler.sendMessage(msg);
                            myLruCache.put(imgPath, bitmap);
                            Log.d(TAG, "run: " + "缓存到强引用中成功");
                            boolean bl = ExternalStorageUtils.storeToSDRoot("haha.jpg", b);
                            if (bl) {
                                Log.d(TAG, "run: " + "缓存到本地内存成功");
                            } else {
                                Log.d(TAG, "run: " + "缓存到本地内存失败");
                            }
                        } else {
                            Toast.makeText(MainActivityTwo.this, "下载失败！", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(MainActivityTwo.this, "请检查你的网络！", Toast.LENGTH_SHORT).show();
                    }
                }
            }).start();
        }
    }

    /**
     * 从缓存中获取图片
     *
     * @return 返回获取到的Bitmap
     */
    public Bitmap getImgCache() {
        Bitmap bitmap = myLruCache.get(imgPath);
        if (bitmap != null) {
            Log.d(TAG, "getImgCache: " + "从LruCache获取图片");
        } else {
            SoftReference<Bitmap> sr = cashMap.get(imgPath);
            if (sr != null) {
                bitmap = sr.get();
                myLruCache.put(imgPath, bitmap);
                cashMap.remove(imgPath);
                Log.d(TAG, "getImgCache: " + "从软引用获取图片");
            } else {
                bitmap = ExternalStorageUtils.getImgFromSDRoot("haha.jpg");
                Log.d(TAG, "getImgCache: " + "从外部存储获取图片");
            }
        }
        return bitmap;
    }

    /**
     * 自定义一个方法继承系统的LruCache方法
     */
    public class MyLruCache extends LruCache<String, Bitmap> {

        /**
         * 必须重写的构造函数，定义强引用缓存区的大小
         * @param maxSize for caches that do not override {@link #sizeOf}, this is
         *                the maximum number of entries in the cache. For all other caches,
         *                this is the maximum sum of the sizes of the entries in this cache.
         */
        public MyLruCache(int maxSize) {
            super(maxSize);
        }

        //返回每个图片的大小
        @Override
        protected int sizeOf(String key, Bitmap value) {
            //获取当前变量每行的字节数和行高度（基本是固定写法，记不住给我背！）
            return value.getRowBytes() * value.getHeight();
        }

        /**
         * 当LruCache中的数据被驱逐或是移除时回调的函数
         *
         * @param evicted  当LruCache中的数据被驱逐用来给新的value倒出空间的时候变化
         * @param key  用来标示对象的键，一般put的时候传入图片的url地址
         * @param oldValue 之前存储的旧的对象
         * @param newValue 存储的新的对象
         */
        @Override
        protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
            if (evicted) {
                /**
                 * 将旧的值存到软引用中，因为强引用中可能有多个值被驱逐，
                 * 所以创建一个LinkedHashMap<String, SoftReference<Bitmap>>来存储软引用
                 * 基本也是固定写法
                 */
                SoftReference<Bitmap> softReference = new SoftReference<Bitmap>(oldValue);
                cashMap.put(key, softReference);
            }
        }
    }
}
