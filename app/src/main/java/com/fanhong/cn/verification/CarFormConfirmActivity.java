package com.fanhong.cn.verification;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fanhong.cn.App;
import com.fanhong.cn.R;
import com.fanhong.cn.fenxiao.PostSuccessActivity;
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
    @ViewInject(R.id.tv_form_licence)
    private TextView tv_licence;
    @ViewInject(R.id.tv_form_type)
    private TextView tv_type;
    @ViewInject(R.id.tv_form_engine)
    private TextView tv_engine;
    @ViewInject(R.id.tv_form_idCard)
    private TextView tv_idCard;
    @ViewInject(R.id.tv_form_address)
    private TextView tv_addr;
    @ViewInject(R.id.box_read)
    private CheckBox cbox_read;
    @ViewInject(R.id.btn_commit)
    private Button btn_commit;

    private CarOrderForm form;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        init();
    }

    private void init() {
        title.setText("确认信息");
        form = (CarOrderForm) getIntent().getExtras().getSerializable("model");
        tv_name.setText(form.getName());
        tv_phone.setText(StringUtils.replaceString(form.getPhone(), 3, 4, "*"));
        tv_licence.setText(form.getLicence());
        tv_type.setText(form.getType());
        tv_engine.setText(form.getEngine());
        tv_idCard.setText(StringUtils.replaceString(form.getIdCard(), 8, 2, "*"));
        tv_addr.setText(form.getAddress());
    }

    @Event(value = R.id.box_read, type = CompoundButton.OnCheckedChangeListener.class)
    private void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        btn_commit.setEnabled(isChecked);
    }

    @Event({R.id.img_back, R.id.tv_read, R.id.btn_commit})
    private void onClicks(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.tv_read:
                startActivityForResult(new Intent(this, CarVerificationNotice.class),11);
                break;
            case R.id.btn_commit:
                if (cbox_read.isChecked())
                    commitForm();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 11)
            if (!cbox_read.isChecked())
                cbox_read.setChecked(true);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void commitForm() {
//        Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        RequestParams param = new RequestParams(App.CMDURL);
        param.addParameter("cmd", "");
        Intent intent = new Intent(CarFormConfirmActivity.this, PostSuccessActivity.class);
        intent.putExtra("fromVerification", true);
        startActivity(intent);
        /*  x.http().post(param, new Callback.CommonCallback<String>() {
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
        });*/
    }
}
