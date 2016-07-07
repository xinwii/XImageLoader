package com.xinwei.ximageloader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.xinwei.ximageloader.xImageLoader.XImageLoader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView imageView1 = (ImageView) findViewById(R.id.imageView);
        ImageView imageView2 = (ImageView) findViewById(R.id.imageView2);
        ImageView imageView3 = (ImageView) findViewById(R.id.imageView3);
        XImageLoader.with(this).load("http://img4.imgtn.bdimg.com/it/u=788367407,1907680151&fm=21&gp=0.jpg").into(imageView1);
        XImageLoader.with(this).load("http://img4.imgtn.bdimg.com/it/u=788367407,1907680151&fm=21&gp=0.jpg").into(imageView2);
        XImageLoader.with(this).load("http://img4.imgtn.bdimg.com/it/u=788367407,1907680151&fm=21&gp=0.jpg").into(imageView3);
    }
}
