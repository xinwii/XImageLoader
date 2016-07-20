package com.xinwei.ximageloader.xImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by iscar on 2016/7/7.
 * XImageLoader主类
 */
public class XImageLoader {
    private static final String TAG = "XImageLoader";
    private String url;
    private static LruCache<String, Bitmap> lruCache;
    private static DiskLruCache diskLruCache;
    private static final int diskCacheSize = 1024 * 1024 * 50;
    private static final int IO_BUFFER_SIZE = 1024 * 8;
    private static XImageLoader xImageLoader;
    private static LinkedBlockingDeque<Runnable> linkedBlockingDeque;
    private static ThreadPoolExecutor executor;
    private static int width = 0;
    private static int height = 0;

    public static XImageLoader with(Context context) {
        width = 0;
        height = 0;
        if (xImageLoader == null)
            synchronized (XImageLoader.class) {
                if (xImageLoader == null)
                    xImageLoader = new XImageLoader(context);
            }
        return xImageLoader;
    }

    public XImageLoader resize(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    /**
     * 仿glide加载方式
     *
     * @param url
     * @return
     */
    public XImageLoader load(String url) {
        this.url = url;
        return this;
    }

    public void into(ImageView imageView) {
        LoadImageView(url, imageView,width,height);
    }

    private XImageLoader(Context context) {
        linkedBlockingDeque = new LinkedBlockingDeque<>();
        executor = new ThreadPoolExecutor(4, 10, 100, TimeUnit.MILLISECONDS, linkedBlockingDeque);
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        lruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() * value.getHeight() / 1024;
            }
        };
        File file = XImageLoaderUtils.getDiskCacheDir(context, "bitmap");
        if (file.exists())
            file.mkdirs();
        try {
            diskLruCache = DiskLruCache.open(file, 1, 1, diskCacheSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载图片
     *
     * @param url
     * @param imageView
     */
    public static void LoadImageView(String url, ImageView imageView,int width,int height) {
        Bitmap bitmap = lruCache.get(XImageLoaderUtils.hashKeyFormUrl(url));
        if (bitmap != null) {
            Log.d(TAG, "get bitmap from memory");
            imageView.setImageBitmap(bitmap);
            return;
        }
        ImageViewData imageData = new ImageViewData(url, imageView, bitmap,width,height);
        GetBitmapRunnable getBitmapRunnable = new GetBitmapRunnable(imageData);
        executor.execute(getBitmapRunnable);
    }

    /**
     * 用于主线程操作
     */
    private static Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ImageViewData imageData = (ImageViewData) msg.obj;
            imageData.imageView.setImageBitmap(imageData.bitmap);
        }
    };

    /**
     * 获取图片线程
     */
    private static class GetBitmapRunnable implements Runnable {
        ImageViewData imageData;

        public GetBitmapRunnable(ImageViewData imageData) {
            this.imageData = imageData;
        }

        @Override
        public void run() {
            Bitmap bitmap = XImageLoaderUtils.getBitmap(imageData.url, lruCache, diskLruCache, IO_BUFFER_SIZE);
            imageData.bitmap = bitmap;
            handler.obtainMessage(1, imageData).sendToTarget();
        }
    }
}
