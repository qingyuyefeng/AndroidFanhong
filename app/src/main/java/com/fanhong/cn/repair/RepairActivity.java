package com.fanhong.cn.repair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
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
 * Created by Administrator on 2017/8/21.
 */
@ContentView(R.layout.activity_repair)
public class RepairActivity extends Activity {
    @ViewInject(R.id.tv_title)
    private TextView tv_title;
    @ViewInject(R.id.edt_input_name)
    private EditText edtName;
    @ViewInject(R.id.edt_input_phone)
    private EditText edtPhone;
    //    @ViewInject(R.id.ctv_province)
    //    CheckedTextView ctvProvince;
    //    @ViewInject(R.id.ctv_city)
    //    CheckedTextView ctvCity;
    //    @ViewInject(R.id.ctv_area)
    //    CheckedTextView ctvArea;
    @ViewInject(R.id.edt_input_address)
    private EditText edtAddr;
    @ViewInject(R.id.edt_input_details)
    private EditText edtDetails;

    private boolean isinput = true;
    private Bundle bundle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        tv_title.setText("上门维修");
        edtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isinput) {
                    isinput = false;
                    String text = s.toString().trim().replace("-", "");
                    if (text.length() > 0) {
                        if (text.charAt(0) == '1') {//以‘1’开头的说明是电话号码，否则认为是座机号码
                            text = StringUtils.addChar(3, text, '-');
                            edtPhone.setText(text);
                        } else {
                            text = StringUtils.addChar(text, '-');
                            edtPhone.setText(text);
                        }
                    }
                    edtPhone.setSelection(text.length());
                } else isinput = true;
            }
        });
    }

    @Event({R.id.img_back, R.id.ctv_province, R.id.ctv_city, R.id.ctv_area, R.id.btn_confirm})
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
                if (getForms()) {
                    RequestParams params = new RequestParams(App.CMDURL);
                    params.addParameter("cmd","71");
                    params.addParameter("name", bundle.getString("name"));
                    params.addParameter("phone", bundle.getString("phone"));
                    params.addParameter("dizhi", bundle.getString("addr"));
                    params.addParameter("concent", bundle.getString("details"));
                    x.http().post(params, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String s) {
                            if (JsonSyncUtils.getJsonValue(s, "cw").equals("0")) {
                                Intent intent = new Intent(RepairActivity.this, RepairSuccessActivity.class);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
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
                } else
                    Toast.makeText(this, "您的输入有误，请检查！", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private boolean getForms() {
        String name = edtName.getText().toString();
        if (StringUtils.isEmpty(name)) {
            Toast.makeText(this, "请输入联系人姓名！", Toast.LENGTH_SHORT).show();
            return false;
        }
        String phone = edtPhone.getText().toString().replace("-", "");
        if (!StringUtils.validPhoneNum("2", phone)) {
            Toast.makeText(this, "请输入正确的电话号码！", Toast.LENGTH_SHORT).show();
            return false;
        }
        String addr = edtAddr.getText().toString();
        if (StringUtils.isEmpty(addr)) {
            Toast.makeText(this, "请输入详细地址！", Toast.LENGTH_SHORT).show();
            return false;
        }
        String details = edtDetails.getText().toString();
        if (StringUtils.isEmpty(details)) {
            Toast.makeText(this, "请简要描述您所报修的损坏情况！", Toast.LENGTH_SHORT).show();
            return false;
        }
        bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("phone", phone);
        bundle.putString("addr", addr);
        bundle.putString("details", details);
        return true;
    }
}
