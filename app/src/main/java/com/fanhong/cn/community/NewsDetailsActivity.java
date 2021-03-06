package com.fanhong.cn.community;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanhong.cn.App;
import com.fanhong.cn.R;
import com.fanhong.cn.util.JsonSyncUtils;
import com.zhy.autolayout.AutoRelativeLayout;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import static com.alipay.sdk.app.statistic.c.v;
import static com.fanhong.cn.R.id.news_detail_back;

/**
 * Created by Administrator on 2017/7/9.
 */
@ContentView(R.layout.newsdetail)
public class NewsDetailsActivity extends Activity {
    @ViewInject(R.id.news_detail_title)
    TextView tv_title;
    @ViewInject(R.id.news_detail_time)
    TextView tv_time;
    @ViewInject(R.id.news_detail_place)
    TextView tv_from;
    @ViewInject(R.id.new_detail_content)
    TextView tv_content;
    @ViewInject(R.id.img_news_details_bar)
    ImageView img_bar;
    @ViewInject(R.id.news_details_bar)
    AutoRelativeLayout news_progressbar;
    AnimationDrawable anim;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        String id = getIntent().getStringExtra("newsId");
//        x.image().bind(img_bar,"assets://images/progressbar.gif",new ImageOptions.Builder().setIgnoreGif(false).build());
        img_bar.setImageResource(R.drawable.anim_progressbar);
        anim = (AnimationDrawable) img_bar.getDrawable();
        anim.start();
        initdata(id);
    }

    private void initdata(String id) {
        RequestParams params = new RequestParams(App.CMDURL);
        params.addParameter("cmd", "51");
        params.addParameter("id", id);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Log.i("====mLog====", s);
                if (JsonSyncUtils.getJsonValue(s, "cw").equals("0")) {
                    String data = JsonSyncUtils.getJsonValue(s, "data");
                    tv_title.setText(JsonSyncUtils.getJsonValue(data, "bt"));
                    tv_time.setText(JsonSyncUtils.getJsonValue(data, "time"));
                    tv_from.setText(JsonSyncUtils.getJsonValue(data, "zz"));
                    tv_content.setText(JsonSyncUtils.getJsonValue(data, "nr"));
                    news_progressbar.setVisibility(View.GONE);
                    anim.stop();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                Log.i("====mLog====", "onError");
            }

            @Override
            public void onCancelled(CancelledException e) {
                Log.i("====mLog====", "onCancelled");
            }

            @Override
            public void onFinished() {
                Log.i("====mLog====", "onFinished");
            }
        });
    }

    @Event(news_detail_back)
    private void onbackclick(View v) {
        finish();
    }
}
