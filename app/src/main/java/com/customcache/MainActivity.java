package com.customcache;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.Arrays;
import java.util.List;

/**
 * 首先根据图片的网络地址在网络上下载图片，将图片先缓存到内存缓存中，缓存到强引用中，也就是LruCache中。
 * 如果强引用中空间不足，就会将较早存储的图片对象驱逐到软引用(softReference)中存储，
 * 然后将图片缓存到文件(内部存储外部存储)中；
 * 读取图片的时候，先读取内存缓存，判断强引用中是否存在图片，如果强引用中存在，则直接读取，如果强引用
 * 中不存在，则判断软引用中是否存在，如果软引用中存在，则将软引用中的图片添加到强引用中并且删除软引用
 * 中的数据，如果软引用中不存在，则读取文件存储，如果文件存储不存在，则网络加载。
 * 下载： 网络--内存--文件
 * 读取： 内存--强引用--软引用--文件--网络
 * Created by wn on 2018/8/2.　
 */
public class MainActivity extends AppCompatActivity {
    private ListView lv;

    private List<String> urlList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = (ListView) findViewById(R.id.id_main_lv_lv);
        initData();
    }

    // 初始化数据
    private void initData() {
        // 初始化图片URL列表
       // urlList = Arrays.asList(SharedData.IMAGE_URLS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    // 初始化视图
    private void initView() {
        // 为ListView适配数据
        ImageAdapter adapter = new ImageAdapter(MainActivity.this, urlList);
        lv.setAdapter(adapter);
    }
}
