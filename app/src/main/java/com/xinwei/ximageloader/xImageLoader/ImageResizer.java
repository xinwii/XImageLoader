package com.xinwei.ximageloader.xImageLoader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.FileDescriptor;

/**
 * Created by iscar on 2016/7/7.
 * 计算图片大小
 */
public class ImageResizer {
    private static final String TAG = "ImageResizer";

    /**
     * 压缩bitmap，返回bitmap
     *
     * @param fileDescriptor
     * @param width
     * @param height
     * @return
     */
    public static Bitmap bitmapFromFileDescriptor(FileDescriptor fileDescriptor, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);

        options.inSampleSize = calculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
    }

    /**
     * 计算inSampleSize
     *
     * @param options
     * @param width
     * @param height
     * @return
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int width, int height) {
        int inSampleSize = 1;
        if (width == 0 || height == 0)
            return inSampleSize;
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        while (outWidth / inSampleSize > width || outHeight / inSampleSize > height) {
            inSampleSize *= 2;
        }
        Log.d(TAG, "inSampleSize = " + inSampleSize);
        return inSampleSize;
    }

}
