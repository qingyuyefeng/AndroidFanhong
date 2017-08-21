package com.fanhong.cn.repair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.fanhong.cn.R;
import com.fanhong.cn.fenxiao.PostSuccessActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by Administrator on 2017/8/21.
 */
@ContentView(R.layout.activity_repair)
public class RepairActivity extends Activity {
    @ViewInject(R.id.tv_title)
    TextView tv_title;
    @ViewInject(R.id.edt_input_name)
    EditText edtName;
    @ViewInject(R.id.edt_input_phone)
    EditText edtPhone;
    @ViewInject(R.id.ctv_province)
    CheckedTextView ctvProvince;
    @ViewInject(R.id.ctv_city)
    CheckedTextView ctvCity;
    @ViewInject(R.id.ctv_area)
    CheckedTextView ctvArea;
    @ViewInject(R.id.edt_input_address)
    EditText edtAddr;
    @ViewInject(R.id.edt_input_details)
    EditText edtDetails;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        tv_title.setText("管线维修");
    }

    @Event({R.id.img_back,R.id.ctv_province,R.id.ctv_city,R.id.ctv_area,R.id.btn_confirm})
    private void onCLicks(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.ctv_province:
                break;
            case R.id.ctv_city:
                break;
            case R.id.ctv_area:
                break;
            case R.id.btn_confirm:
                startActivity(new Intent(this, RepairSuccessActivity.class));
                break;
        }
    }
}
