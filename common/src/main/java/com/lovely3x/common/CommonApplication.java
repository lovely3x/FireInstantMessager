package com.lovely3x.common;

import android.app.Application;
import android.support.v4.view.ViewPager;
import android.support.v7.internal.widget.ViewUtils;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

/**
 *
 * Created by lovely3x on 15-9-1.
 */
public class CommonApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initUrgentEvent();
    }


    /**
     * 在这里初始化,需要立即初始化的不能在拖延的
     */
    public void initUrgentEvent() {
        com.lovely3x.common.utils.ViewUtils.init(this);
        com.lovely3x.common.utils.NetUtils.init(this);
    }

    /**
     * 在这里初始化时不那么紧急的事件
     * 推荐在程序的启动页调用
     */
    public void initSlowEvent() {
        initImageLoader();
    }


    /**
     * 初始化图片加载器
     */
    private void initImageLoader() {
        File cacheDir = StorageUtils.getOwnCacheDirectory(getApplicationContext(), "imageloader/Cache");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                // max width, max height，即保存的每个缓存文件的最大长宽
                .threadPoolSize(5).denyCacheImageMultipleSizesInMemory()
                        // 线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory().memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
                        // You can pass your own memory cache
                        // implementation/你可以通过自己的内存缓存实现
                .memoryCacheSize(10 * 1024 * 1024).diskCacheSize(50 * 1024 * 1024)
                        // 将保存的时候的URI名称用MD5 加密
                .tasksProcessingOrder(QueueProcessingType.FIFO).diskCacheFileCount(300)
                        // 缓存的文件数量
                .diskCache(new UnlimitedDiscCache(cacheDir))
                        // 自定义缓存路径
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()).imageDownloader(new BaseImageDownloader(getApplicationContext(), 5 * 1000, 30 * 1000))
                        // connectTimeout
                .writeDebugLogs().defaultDisplayImageOptions(new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build()) // Remove
                .build();// 构建
        ImageLoader.getInstance().init(config);
    }
}
