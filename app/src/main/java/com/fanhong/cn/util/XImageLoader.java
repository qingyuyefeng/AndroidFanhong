package com.fanhong.cn.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import cn.finalteam.galleryfinal.ImageLoader;
import cn.finalteam.galleryfinal.widget.GFImageView;

/**
 * Created by Administrator on 2018/3/9.
 */

public class XImageLoader implements ImageLoader {
    private Bitmap.Config config = null;

    public XImageLoader() {
        this(Bitmap.Config.RGB_565);
    }

    public XImageLoader(Bitmap.Config config) {
        this.config = config;
    }

    @Override
    public void displayImage(Activity activity, String path, GFImageView imageView, Drawable defaultDrawable, int width, int height) {
        ImageOptions options = new ImageOptions.Builder()
                .setLoadingDrawable(defaultDrawable)
                .setFailureDrawable(defaultDrawable)
                .setConfig(config)
                .setSize(width, height)
                .setCrop(true)
                .setUseMemCache(false)
                .build();
        x.image().bind(imageView, "file://" + path, options);
    }

    @Override
    public void clearMemoryCache() {

    }
}
