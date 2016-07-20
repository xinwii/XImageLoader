package com.xinwei.ximageloader.xImageLoader;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * 图片对象
 */
public class ImageViewData {
    public ImageView imageView;
    public String url;
    public Bitmap bitmap;
    public int width;
    public int height;

    public ImageViewData(String url, ImageView imageView, Bitmap bitmap,int width,int height) {
        this.url = url;
        this.imageView = imageView;
        this.bitmap = bitmap;
        this.width=width;
        this.height=height;
    }
}