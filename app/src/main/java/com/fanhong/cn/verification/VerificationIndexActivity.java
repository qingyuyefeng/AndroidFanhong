package com.fanhong.cn.verification;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.fanhong.cn.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by Administrator on 2017/8/8.
 */
@ContentView(R.layout.activity_verification_index)
public class VerificationIndexActivity extends Activity {
    @ViewInject(R.id.tv_title)
    TextView tv_title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        setTitle("代办年审");
    }

    private void setTitle(String title) {
        tv_title.setText(title);
    }

    @Event({R.id.img_back, R.id.verification_car})
    private void onClicks(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.verification_car:
                startActivity(new Intent(this, VerificationCarActivity.class));
                break;
        }
    }
}
