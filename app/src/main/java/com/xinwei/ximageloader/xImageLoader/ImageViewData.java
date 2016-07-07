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

    public ImageViewData(String url, ImageView imageView, Bitmap bitmap) {
        this.url = url;
        this.imageView = imageView;
        this.bitmap = bitmap;
    }
}