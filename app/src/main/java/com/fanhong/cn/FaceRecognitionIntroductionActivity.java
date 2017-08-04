package com.fanhong.cn;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhy.autolayout.AutoLinearLayout;

import org.xutils.common.Callback;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2017/8/2.
 */
@ContentView(R.layout.activity_face_recognition_introduce)
public class FaceRecognitionIntroductionActivity extends Activity {
    @ViewInject(R.id.tv_top_title)
    TextView tv_title;
    @ViewInject(R.id.tv_introduce)
    TextView tv_introduce;
    @ViewInject(R.id.img_introduce)
    ImageView img_view;

    private ImageOptions options;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        options = new ImageOptions.Builder().setUseMemCache(true).build();
        initViews(getIntent().getIntExtra("position", -1));

    }

    private void initViews(int position) {
        switch (position) {
            case 0:
                tv_introduce.setVisibility(View.GONE);
                tv_title.setText(R.string.app_name);
                x.image().loadDrawable("assets://images/tese_fanshequ.png", options, new Callback.CommonCallback<Drawable>() {
                    @Override
                    public void onSuccess(Drawable drawable) {
                        DisplayMetrics display = getResources().getDisplayMetrics();
                        int width = display.widthPixels, height;
                        Log.i("asd", "width:" + width + "drawable:" + drawable.getMinimumWidth() + "::" + drawable.getMinimumHeight());
                        height = drawable.getMinimumHeight() * width / drawable.getMinimumWidth();
                        AutoLinearLayout.LayoutParams params = new AutoLinearLayout.LayoutParams(width, height);
                        params.bottomMargin = 10 * width / 720;
                        img_view.setLayoutParams(params);
                        img_view.setImageDrawable(drawable);
                    }

                    @Override
                    public void onError(Throwable throwable, boolean b) {

                    }

                    @Override
                    public void onCancelled(CancelledException e) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });

                break;
            case 1://app_name
                tv_title.setText("人脸识别");
                x.image().loadDrawable("assets://images/face_recognition_introduce.png",
                        options, new Callback.CommonCallback<Drawable>() {
                    @Override
                    public void onSuccess(Drawable drawable) {
                        DisplayMetrics display = getResources().getDisplayMetrics();
                        int width = display.widthPixels, height;
                        Log.i("asd", "width:" + width + "drawable:" + drawable.getMinimumWidth() + "::" + drawable.getMinimumHeight());
                        height = drawable.getMinimumHeight() * width / drawable.getMinimumWidth();
                        AutoLinearLayout.LayoutParams params = new AutoLinearLayout.LayoutParams(width, height);
                        params.bottomMargin = 10 * width / 720;
                        Log.i("asd", "width:" + width + "imageview:" + width + "::" + height);
                        img_view.setLayoutParams(params);
                        img_view.setImageDrawable(drawable);
                    }

                    @Override
                    public void onError(Throwable throwable, boolean b) {

                    }

                    @Override
                    public void onCancelled(CancelledException e) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });
//                AssetManager manager = getAssets();
//                try {
//                    InputStream is = manager.open("assets://images/face_recognition_introduce.png");
//                    Drawable drawable = Drawable.createFromStream(is, null);
//                    DisplayMetrics display = getResources().getDisplayMetrics();
//                    int width = display.widthPixels, height;
//                    Log.i("asd", "width:" + width + "drawable:" + drawable.getMinimumWidth() + "::" + drawable.getMinimumHeight());
//                    height = drawable.getMinimumHeight() * width / drawable.getMinimumWidth();
//                    AutoLinearLayout.LayoutParams params = new AutoLinearLayout.LayoutParams(width, height);
//                    params.bottomMargin = 10 * width / 720;
//                    img_view.setLayoutParams(params);
//                    img_view.setImageDrawable(drawable);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                break;
        }
    }

    @Event(R.id.tv_back)
    private void onclick(View v) {
        finish();
    }
}
