package com.fanhong.cn;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhy.autolayout.AutoLinearLayout;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        initViews(getIntent().getIntExtra("position", -1));

    }

    private void initViews(int position) {
        Drawable image;
        AutoLinearLayout.LayoutParams params;
        DisplayMetrics display = getResources().getDisplayMetrics();
        int width = display.widthPixels, height;
        switch (position) {
            case 0:
                tv_introduce.setVisibility(View.GONE);

                tv_title.setText(R.string.app_name);
                image = getDrawable(R.drawable.tese_fanshequ);
                Log.i("asd", "width:" + width);
                height = image.getMinimumHeight() * width / image.getMinimumWidth();
                params = new AutoLinearLayout.LayoutParams(width, height);
                params.bottomMargin = 10 * width / 720;
                img_view.setLayoutParams(params);
                img_view.setImageDrawable(image);
                break;
            case 1://app_name
                tv_title.setText("人脸识别");
                image = getDrawable(R.drawable.face_recognition_introduce);
                Log.i("asd", "width:" + width);
                height = image.getMinimumHeight() * width / image.getMinimumWidth();
                params = new AutoLinearLayout.LayoutParams(width, height);
                params.bottomMargin = 10 * width / 720;
                img_view.setLayoutParams(params);
                img_view.setImageDrawable(image);
                break;
        }
    }

    @Event(R.id.tv_back)
    private void onclick(View v) {
        finish();
    }
}
