package com.fanhong.cn.housekeeping;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.fanhong.cn.FragmentMainActivity;
import com.fanhong.cn.R;
import com.fanhong.cn.util.StringUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by Administrator on 2017/7/25.
 */
@ContentView(R.layout.activity_hk_order_details)
public class HouseKeepingOrderDetailsActivity extends Activity {
    @ViewInject(R.id.tv_hk_order_details_step_phone)
    TextView tv_step_phone;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        rulephonetext(tv_step_phone.getText().toString().trim());
    }

    private void rulephonetext(String phone) {
        phone = StringUtils.replaceString(phone, 3, 4, "*");
        phone = StringUtils.addChar(3, phone, '-');
        tv_step_phone.setText(phone);
    }
    @Event({R.id.tv_back, R.id.btn_hk_order_return})
    private void onClicks(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.btn_hk_order_return:
                Intent intent =new Intent(this, FragmentMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
    }
}
