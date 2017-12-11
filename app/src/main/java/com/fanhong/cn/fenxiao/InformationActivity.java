package com.fanhong.cn.fenxiao;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.maps2d.AMapOptions;
import com.fanhong.cn.App;
import com.fanhong.cn.R;
import com.fanhong.cn.util.JsonSyncUtils;
import com.fanhong.cn.util.StringUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/8/8.
 */

@ContentView(R.layout.activity_fenxiao_information)
public class InformationActivity extends Activity {
    @ViewInject(R.id.fenxiao_name)
    private EditText fName;
    @ViewInject(R.id.fenxiao_phone)
    private EditText fPhone;
    @ViewInject(R.id.hetong_address)
    private EditText fAddress;
    @ViewInject(R.id.bank_card)
    private EditText fBankNumber;
    @ViewInject(R.id.bank_type)
    private EditText fBankType;

    private SharedPreferences mSettingPref;
    private String uid;

    private AMapLocationClient client;
    private boolean isinput = true;//拦截非用户输入的更改，防止死锁

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        mSettingPref = getSharedPreferences("Setting", Context.MODE_PRIVATE);
        uid = mSettingPref.getString("UserId", "");

        if (client == null) {
            client = new AMapLocationClient(getApplicationContext());
        }
        AMapLocation location = client.getLastKnownLocation();
        if (location != null && location.getErrorCode() == 0) {
            fAddress.setText(location.getAddress().split("靠近")[0]);
        }
        fBankNumber.addTextChangedListener(new TextWatcher() {
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
                    String str = StringUtils.addChar(fBankNumber.getText().toString(), ' ');
                    fBankNumber.setText(str);
                    fBankNumber.setSelection(str.length());
                }else
                    isinput = true;
            }
        });
    }

    @Event(value = {R.id.back_bn, R.id.tv_post_information})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_bn:
                finish();
                break;
            case R.id.tv_post_information:
                postInformation(fName.getText().toString(),
                        fPhone.getText().toString(),
                        fAddress.getText().toString(),
                        fBankNumber.getText().toString().trim(),
                        fBankType.getText().toString());
                break;
        }
    }

    private void postInformation(String s1, String s2, String s3, String s4, String s5) {
        if (!(s1.length() > 0 && s2.length() > 0 && s3.length() > 0 && s4.length() > 0 && s5.length() > 0)) {
            Toast.makeText(this, "资料填写不完整", Toast.LENGTH_SHORT).show();
        } else if (!checkPhoneNumber(s2)) {
            Toast.makeText(this, R.string.phoneiswrong, Toast.LENGTH_SHORT).show();
        } else if (!checkBankcard(s4)) {
            Toast.makeText(this, "请输入正确的银行卡号", Toast.LENGTH_SHORT).show();
        } else {
            RequestParams params = new RequestParams(App.CMDURL);
            params.addBodyParameter("cmd", "67");
            params.addBodyParameter("uid", uid);
            params.addBodyParameter("name", s1);
            params.addBodyParameter("tel", s2);
            params.addBodyParameter("dizhi", s3);
            params.addBodyParameter("kahao", s4);
            params.addBodyParameter("ssyh", s5);
            x.http().post(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String s) {
                    String cw = JsonSyncUtils.getJsonValue(s, "cw");
                    if (cw.equals("0")) {
                        startActivity(new Intent(InformationActivity.this, PostSuccessActivity.class));
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
        }
    }

    //验证是否为电话号码
    private boolean checkPhoneNumber(String phoneNum) {
        Pattern p1 = Pattern.compile("^(((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[0-9])|(18[0-9]))+\\d{8})?$");
        Pattern p2 = Pattern.compile("^(0[0-9]{2,3}\\-)?([1-9][0-9]{6,7})$");
        if (((phoneNum.length() == 11 && p1.matcher(phoneNum).matches()) || (phoneNum.length() < 16 && p2.matcher(phoneNum).matches()))) {
            return true;
        } else {
            return false;
        }
    }

    //验证是否为银行卡号
    private boolean checkBankcard(String cardNum) {
        if (cardNum.length() < 15 || cardNum.length() > 19) {
            return false;
        } else {
            return true;
        }
    }
}
