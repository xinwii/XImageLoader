package com.xinwei.ximageloader.xImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.util.LruCache;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by iscar on 2016/7/7.
 */
public class XImageLoaderUtils {
    private static final String TAG = "XImageLoaderUtils";

    /**
     * Key转为常规字符,url中有不可以为key的字符串
     *
     * @param url
     * @return
     */
    public static String hashKeyFormUrl(String url) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(url.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(url.hashCode());
        }
        return cacheKey;
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * 获取bitmap，首先lrucache，然后disklrucache，然后网络请求
     *
     * @param url
     * @param lruCache
     * @param diskLruCache
     * @param IO_BUFFER_SIZE
     * @return
     */
    protected static Bitmap getBitmap(String url, LruCache<String, Bitmap> lruCache, DiskLruCache diskLruCache, int IO_BUFFER_SIZE) {
        Bitmap bitmap;
        try {
            DiskLruCache.Snapshot snapshot = diskLruCache.get(XImageLoaderUtils.hashKeyFormUrl(url));
            if (snapshot != null) {
                FileInputStream fileInputStream = (FileInputStream) snapshot.getInputStream(0);
                FileDescriptor fileDescriptor = fileInputStream.getFD();
                bitmap = ImageResizer.bitmapFromFileDescriptor(fileDescriptor, 0, 0);
                if (bitmap != null) {
                    lruCache.put(url, bitmap);
                    Log.d(TAG, "get bitmap from disk");
                    return bitmap;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return XImageLoaderHttp.getBitmapFromUrl(url, lruCache, diskLruCache, IO_BUFFER_SIZE);
    }

    /**
     * 获取系统路径，然后根据名字生成File
     *
     * @param context
     * @param uniqueName
     * @return
     */
    protected static File getDiskCacheDir(Context context, String uniqueName) {
        boolean externalStorageAvailable = Environment
                .getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        final String cachePath;
        if (externalStorageAvailable) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }

        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * 关闭资源
     *
     * @param closeable
     */
    protected static void close(Closeable closeable) {
        if (closeable != null)
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
