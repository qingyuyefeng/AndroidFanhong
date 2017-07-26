package com.fanhong.cn.housekeeping;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.fanhong.cn.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.x;

/**
 * Created by Administrator on 2017/7/24.
 */
@ContentView(R.layout.activity_hk_order)
public class HouseKeepingOrderActivity extends Activity {

    private String service_title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        service_title = getIntent().getStringExtra("title");
    }

    @Event({R.id.tv_back, R.id.btn_hk_order_addr_default, R.id.btn_hk_order_pay_now})
    private void onClicks(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.btn_hk_order_addr_default:
                break;
            case R.id.btn_hk_order_pay_now:
                Intent intent = new Intent(this, HouseKeepingOrderDetailsActivity.class);
                intent.putExtra("title", service_title);
                startActivity(intent);
                break;
        }
    }
}
