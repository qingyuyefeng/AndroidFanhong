package com.fanhong.cn.synctaskpicture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.fanhong.cn.App;
import com.fanhong.cn.pay.MD5Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.LinkedHashMap;

/**
 * Created by Administrator on 2017/5/19.
 */

public class LoadImage extends AsyncTask<String,Void,Bitmap> {
    private ImageView imageView;
    private String url;
    private static Context context;



    private LoadImage(){}

    public static void Load(ImageView imageView, String url, Context context) {
        LoadImage loadImage=new LoadImage();
        loadImage.imageView = imageView;
        loadImage.url = url;
        loadImage.context = context;
        loadImage.imageView.setTag(url);
        loadImage.execute(url);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if(bitmap!=null)
        {
            imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap bitmap=null;
        InputStream is=null;
        try {
            is=new URL(url).openConnection().getInputStream();
            InputStream iss=SaveInFile.get(context,url);
            if(App.memery.get(url)!=null){
                bitmap= App.memery.get(url);
                Log.i("hhh","bitmap1="+bitmap+"");
            }else if(iss!=null){
                bitmap= BitmapFactory.decodeStream(iss);
                Log.i("hhh","bitmap2="+bitmap+"");
            }else {
                SaveInFile.save(context,is,url);
                iss=SaveInFile.get(context,url);
                bitmap= BitmapFactory.decodeStream(iss);
                Log.i("hhh","bitmap3="+bitmap+"");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return bitmap;
    }
    private static final int MAX_CAPACITY = 20;// ??????????
                                    // ????????????????????
    private static final LinkedHashMap<String, SoftReference<Bitmap>> firstCacheMap = new LinkedHashMap<String, SoftReference<Bitmap>>(
            MAX_CAPACITY) {
        protected boolean removeEldestEntry(Entry<String, SoftReference<Bitmap>> eldest) {
            // ????true???????????????????????????
            if (this.size() > MAX_CAPACITY) {
                return true;
            } else {// ???????????????
                diskCache(eldest.getKey(), eldest.getValue());
                return false;
            }
        }
    };
    private static void diskCache(String key, SoftReference<Bitmap> value) {
        // ??��??SD??????????????MD5????????????????
        String fileName = MD5Util.getMD5Str(key);
        String filePath = context.getCacheDir().getAbsolutePath() + File.separator + fileName;
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(new File(filePath));
            if (value.get() != null) {
                value.get().compress(Bitmap.CompressFormat.JPEG, 60, os);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * ???????????
     * @param key
     * @param result
     */
    public void addFirstCache(String key, Bitmap result) {
        if (result != null) {
            synchronized (firstCacheMap) {
                firstCacheMap.put(key, new SoftReference<Bitmap>(result));
            }
        }
    }
}
