package com.fanhong.cn.housekeeping;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanhong.cn.App;
import com.fanhong.cn.FragmentMainActivity;
import com.fanhong.cn.R;
import com.fanhong.cn.util.StringUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by Administrator on 2017/7/25.
 */
@ContentView(R.layout.activity_hk_order_details)
public class HouseKeepingOrderDetailsActivity extends Activity {
    @ViewInject(R.id.img_hk_order_details)
    ImageView img_details;
    @ViewInject(R.id.tv_hk_order_details_title)
    TextView tv_details_title;
    @ViewInject(R.id.tv_hk_order_details_time)
    TextView tv_details_time;
    @ViewInject(R.id.tv_hk_order_details_step_time)
    TextView tv_step_time;
    @ViewInject(R.id.tv_hk_order_details_step_addr)
    TextView tv_step_addr;
    @ViewInject(R.id.tv_hk_order_details_step_phone)
    TextView tv_step_phone;
    @ViewInject(R.id.img_hk_order_step_1)
    ImageView img_step_1;
    @ViewInject(R.id.tv_hk_order_step_1)
    TextView tv_step_1;
    @ViewInject(R.id.img_hk_order_step_2)
    ImageView img_step_2;
    @ViewInject(R.id.tv_hk_order_step_2)
    TextView tv_step_2;
    @ViewInject(R.id.img_hk_order_step_3)
    ImageView img_step_3;
    @ViewInject(R.id.tv_hk_order_step_3)
    TextView tv_step_3;
    @ViewInject(R.id.img_hk_order_step_4)
    ImageView img_step_4;
    @ViewInject(R.id.tv_hk_order_step_4)
    TextView tv_step_4;
    @ViewInject(R.id.img_hk_order_step_5)
    ImageView img_step_5;
    @ViewInject(R.id.tv_hk_order_step_5)
    TextView tv_step_5;
    @ViewInject(R.id.img_hk_order_step_6)
    ImageView img_step_6;
    @ViewInject(R.id.tv_hk_order_step_6)
    TextView tv_step_6;


    private String service_title, service_price;
    private int[] STATE_PAST = {R.drawable.order_state_past, R.color.leftbackground},
            STATE_NOW = {R.drawable.order_state_now, R.color.skyblue},
            STATE_WAIT = {R.drawable.order_state_wait, R.color.text_9};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        service_title = getIntent().getStringExtra("title");
        service_price = getIntent().getStringExtra("price");
initDatas();
        rulephonetext(tv_step_phone.getText().toString().trim());
    }

    private void initDatas() {
        RequestParams params=new RequestParams(App.CMDURL);
        params.addParameter("cmd","");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                initSteps();
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

    private void initSteps() {
    }

    private void rulephonetext(String phone) {
        phone = StringUtils.replaceString(phone, 3, 4, "*");
        phone = StringUtils.addChar(3, phone, '-');
        tv_step_phone.setText(phone);
    }

    @Event({R.id.tv_back, R.id.layout_hk_order_details, R.id.btn_hk_order_return})
    private void onClicks(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.layout_hk_order_details:
                intent.setClass(this, HouseKeepingServiceDetailsActivity.class);
                intent.putExtra("single", true);
                intent.putExtra("title", service_title);
                intent.putExtra("price", service_price);
                startActivity(intent);
                break;
            case R.id.btn_hk_order_return:
                intent.setClass(this, FragmentMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
    }
}
