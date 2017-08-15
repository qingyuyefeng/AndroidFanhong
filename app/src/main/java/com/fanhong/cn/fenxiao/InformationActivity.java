package com.fanhong.cn.fenxiao;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fanhong.cn.App;
import com.fanhong.cn.R;
import com.fanhong.cn.util.JsonSyncUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        mSettingPref = getSharedPreferences("Setting", Context.MODE_PRIVATE);
        uid = mSettingPref.getString("UserId", "");
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
                        fBankNumber.getText().toString(),
                        fBankType.getText().toString());
                break;
        }
    }

    private void postInformation(String s1, String s2, String s3, String s4, String s5) {
        if (s1.length() > 0 && s2.length() > 0 && s3.length() > 0 && s4.length() > 0 && s5.length() > 0) {
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
        } else {
            Toast.makeText(this, "资料填写不完整", Toast.LENGTH_SHORT).show();
        }
    }
}
