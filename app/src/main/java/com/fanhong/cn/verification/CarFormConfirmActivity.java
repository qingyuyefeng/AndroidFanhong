package com.fanhong.cn.verification;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fanhong.cn.App;
import com.fanhong.cn.R;
import com.fanhong.cn.fenxiao.PostSuccessActivity;
import com.fanhong.cn.util.JsonSyncUtils;
import com.fanhong.cn.util.StringUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by Administrator on 2017/8/15.
 */
@ContentView(R.layout.activity_verification_car_form_confirm)
public class CarFormConfirmActivity extends Activity {
    @ViewInject(R.id.tv_title)
    private TextView title;
    @ViewInject(R.id.tv_form_name)
    private TextView tv_name;
    @ViewInject(R.id.tv_form_phone)
    private TextView tv_phone;
//    @ViewInject(R.id.tv_form_licence)
//    private TextView tv_licence;
//    @ViewInject(R.id.tv_form_type)
//    private TextView tv_type;
//    @ViewInject(R.id.tv_form_engine)
//    private TextView tv_engine;
//    @ViewInject(R.id.tv_form_idCard)
//    private TextView tv_idCard;
    @ViewInject(R.id.tv_form_address)
    private TextView tv_addr;
//    @ViewInject(R.id.box_read)
//    private CheckBox cbox_read;
    @ViewInject(R.id.btn_commit)
    private Button btn_commit;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        init();
    }

    private void init() {
        title.setText("确认信息");
        Intent intent = getIntent();
        tv_name.setText(intent.getStringExtra("name"));
        tv_phone.setText(StringUtils.replaceString(intent.getStringExtra("phone"), 3, 4, "*"));
        tv_addr.setText(intent.getStringExtra("address"));
    }

   /* @Event(value = R.id.box_read, type = CompoundButton.OnCheckedChangeListener.class)
    private void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        btn_commit.setEnabled(isChecked);
    }*/

    @Event({R.id.img_back, R.id.btn_commit})
    private void onClicks(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_commit://确认并去付款
                break;
        }
    }
}
