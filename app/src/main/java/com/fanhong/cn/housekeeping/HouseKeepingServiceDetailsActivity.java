package com.fanhong.cn.housekeeping;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fanhong.cn.R;

import org.xutils.common.Callback;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by Administrator on 2017/7/21.
 */
@ContentView(R.layout.activity_hk_service_details)
public class HouseKeepingServiceDetailsActivity extends Activity {
    @ViewInject(R.id.tv_title)
    private TextView title;
    @ViewInject(R.id.tv_hk_details_title)
    private TextView tv_title;
    @ViewInject(R.id.tv_hk_details_price)
    private TextView tv_price;
    @ViewInject(R.id.img_hk_details_top)
    private ImageView img_top;
    @ViewInject(R.id.img_hk_details_details)
    private ImageView img_details;

    private ImageOptions options;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        options = new ImageOptions.Builder().setIgnoreGif(false).setUseMemCache(true).setLoadingDrawableId(R.drawable.img_default).setFailureDrawableId(R.drawable.img_default).build();
        tv_title.setText(getIntent().getStringExtra("title"));
        tv_price.setText(getIntent().getStringExtra("price"));
        bindImages();
    }

    private void bindImages() {
        x.image().bind(img_top, "assets://timg.png", options);
        x.image().bind(img_details, "assets://images/housekeeping_details.jpg", options, new Callback.CommonCallback<Drawable>() {
            @Override
            public void onSuccess(Drawable drawable) {
                Log.i("bindximage", "drawable:width:" + drawable.getMinimumWidth() + "\theight:" + drawable.getMinimumHeight() + "\n" +
                        "ImageView:Width:" + img_details.getWidth() + "Height:" + img_details.getHeight());
                DisplayMetrics display = getResources().getDisplayMetrics();
                int width = display.widthPixels;
                width = width > img_details.getWidth() ? width : img_details.getWidth();
                int height = (int) ((float) width * drawable.getMinimumHeight() / drawable.getMinimumWidth());
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(width, height);
                param.bottomMargin = 20 * width / 720;
                img_details.setLayoutParams(param);
                img_details.setImageDrawable(drawable);
                Log.i("bindximage", "Parent:width:" + width + "\theight:" + height + "\n" +
                        "ImageView:Width:" + img_details.getWidth() + "Height:" + img_details.getHeight());
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
    }

    @Event(value = {R.id.tv_back, R.id.btn_hk_details_phone, R.id.btn_hk_details_oder_now})
    private void onclicks(View v) {
        switch (v.getId()) {
            case R.id.btn_hk_details_phone:
                new AlertDialog.Builder(this).setTitle("即将拨打").setMessage("拨打客服电话(15123073170)？").setPositiveButton("立即拨打", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + "15123073170"));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }).setNegativeButton("取消", null).show();
                break;
            case R.id.btn_hk_details_oder_now:
                Intent intent = new Intent(this, HouseKeepingOrderActivity.class);
                intent.putExtra("title", getIntent().getStringExtra("title"));
                startActivity(intent);
                break;
            case R.id.tv_back:
                finish();
                break;
        }
    }
}
