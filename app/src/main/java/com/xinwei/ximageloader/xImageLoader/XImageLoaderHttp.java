package com.xinwei.ximageloader.xImageLoader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.LruCache;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by iscar on 2016/7/7.
 */
public class XImageLoaderHttp {
    private static final String TAG = "XImageLoaderHttp";

    protected static Bitmap getBitmapFromUrl(String url, LruCache<String, Bitmap> lruCache, DiskLruCache diskLruCache, int IO_BUFFER_SIZE) {
        Log.d(TAG, "get bitmap from http");
        HttpURLConnection urlConnection;
        BufferedInputStream in = null;
        try {
            URL u = new URL(url);
            urlConnection = (HttpURLConnection) u.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), IO_BUFFER_SIZE);
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            lruCache.put(url, bitmap);
            DiskLruCache.Editor editor = diskLruCache.edit(url);
            OutputStream outputStream = editor.newOutputStream(0);
            if (downloadUrlToStream(url, outputStream, IO_BUFFER_SIZE)) {
                editor.commit();
            } else {
                editor.abort();
            }
            diskLruCache.flush();
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            XImageLoaderUtils.close(in);
        }
        return null;
    }

    private static boolean downloadUrlToStream(String urlString,
                                               OutputStream outputStream, int IO_BUFFER_SIZE) {
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;

        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(),
                    IO_BUFFER_SIZE);
            out = new BufferedOutputStream(outputStream, IO_BUFFER_SIZE);

            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            return true;
        } catch (IOException e) {
            Log.e(TAG, "downloadBitmap failed." + e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            XImageLoaderUtils.close(in);
            XImageLoaderUtils.close(out);
        }
        return false;
    }
}
