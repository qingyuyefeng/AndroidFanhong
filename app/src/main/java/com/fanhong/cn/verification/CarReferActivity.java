package com.fanhong.cn.verification;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanhong.cn.App;
import com.fanhong.cn.R;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import static com.fanhong.cn.R.id.tv_title;

/**
 * Created by Administrator on 2017/8/8.
 */
@ContentView(R.layout.activity_verification_car_refer)
public class CarReferActivity extends Activity {
    @ViewInject(R.id.tv_title)
    TextView tv_title;
    @ViewInject(R.id.img_order_step_1)
    ImageView img_step_1;
    @ViewInject(R.id.tv_order_step_1)
    ImageView tv_step_1;
    @ViewInject(R.id.img_order_step_1)
    ImageView img_step_2;
    @ViewInject(R.id.tv_order_step_1)
    ImageView tv_step_2;
    @ViewInject(R.id.img_order_step_1)
    ImageView img_step_3;
    @ViewInject(R.id.tv_order_step_1)
    ImageView tv_step_3;
    @ViewInject(R.id.img_order_step_1)
    ImageView img_step_4;
    @ViewInject(R.id.tv_order_step_1)
    ImageView tv_step_4;
    @ViewInject(R.id.img_order_step_1)
    ImageView img_step_5;
    @ViewInject(R.id.tv_order_step_1)
    ImageView tv_step_5;
    @ViewInject(R.id.img_order_step_1)
    ImageView img_step_6;
    @ViewInject(R.id.tv_order_step_1)
    ImageView tv_step_6;
    @ViewInject(R.id.img_order_step_1)
    ImageView img_step_7;
    @ViewInject(R.id.tv_order_step_1)
    ImageView tv_step_7;

    private int[] STATE_PAST = {R.drawable.order_state_past, R.color.text_9},
            STATE_NOW = {R.drawable.order_state_now, R.color.skyblue},
            STATE_WAIT = {R.drawable.order_state_wait, R.color.text_9};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        setTitle("机动车年审");
        initDatas();
    }

    private void initDatas() {
        RequestParams param=new RequestParams(App.CMDURL);
        param.addParameter("cmd","");
        x.http().post(param, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {

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

    private void setTitle(String title) {
        tv_title.setText(title);
    }

    @Event({R.id.img_back,R.id.btn_back})
    private void onClicks(View v) {
//        switch (v.getId()) {
//            case R.id.img_back:
//            case R.id.btn_back:
                finish();
//                break;
//        }
    }
}
