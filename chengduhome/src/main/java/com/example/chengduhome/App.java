package com.example.chengduhome;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.tencent.bugly.Bugly;


import java.io.File;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2017/12/20.
 */

public class App extends Application {

    private static Application appContext;
    private ImageLoader loader;

    public ImageLoader getLoader() {
        return loader;
    }
    //当程序启动后，第一个运行的方法，程序入口
    //当程序启动后，第一个运行的方法，程序入口
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
    public static Context getAppContext() {
        return appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        JPushInterface.init(getApplicationContext());
        loader =ImageLoader.getInstance();
        //loader对象的对象的手动配置
        // 初始化ImageLoaderConfiguration类对象
        ImageLoaderConfiguration config = initConfiguration();
        loader.init(config);
        appContext = this;
        Bugly.init(getApplicationContext(), "bf6b3d2956", false);

    }
    private ImageLoaderConfiguration initConfiguration() {
        File file = getCacheDir(); //获取缓存文件夹，位于：data/data/程序包名/caches
        if (!file.exists()) {
            file.mkdirs();
        }
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds=true;
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        opts.inPurgeable=true;
        opts.inInputShareable=true;
        DisplayImageOptions options = initDisplayOptions();

        int maxSize = (int) (Runtime.getRuntime().maxMemory()/16);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPoolSize(3)  //最多可以同时下载多少张图片
                .memoryCacheSizePercentage(40)  //设置最大的内容大小，占用可用内存的百分比m
                .memoryCache(new WeakMemoryCache()) //设置的缓存策略
                .diskCacheSize(10*1024*1024) //设置磁盘缓存的最大带下
                .diskCache(new UnlimitedDiskCache(file))  //设置磁盘缓存策略,参数中的file、：设置图片的存储地址
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) //设置文件的命名方式
                .defaultDisplayImageOptions(options)  //设置图片显示时的配置信息
                .build();
        return config;
    }
    private DisplayImageOptions initDisplayOptions() {

        return new DisplayImageOptions.Builder()
                .cacheInMemory(true)  //是指图片下载后是否缓存到内存中
                .cacheOnDisk(true)//是指图片下载后是否到本地
              /*  .showImageOnLoading(R.drawable.yugang)  // 设置图片下载过程中，控件上显示的图片
                .showImageOnFail(R.drawable.yugang) //设置图片下载失败，控件上显示的图片*/
                .showImageOnLoading(R.drawable.pictureme)  // 设置图片下载过程中，控件上显示的图片
                .showImageOnFail(R.drawable.pictureme) //设置图片下载失败，控件上显示的图片
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)

                //.bitmapConfig(Bitmap.Config.RGB_565)
                //.bitmapConfig(Bitmap.Config.RGB_565)
                .build();


    }

}
