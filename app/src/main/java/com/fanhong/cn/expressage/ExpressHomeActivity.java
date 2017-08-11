package com.fanhong.cn.expressage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanhong.cn.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by Administrator on 2017/8/9.
 */

@ContentView(R.layout.activity_express)
public class ExpressHomeActivity extends Activity{
    @ViewInject(R.id.tv_title)
    private TextView titleTop;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        titleTop.setText(R.string.daifa_express);
    }
    @Event({R.id.img_back,R.id.bn_sendexpress,R.id.bn_checkexpress})
    private void onClick(View v){
        switch (v.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.bn_sendexpress:
                startActivity(new Intent(this,SendExpressActivity.class));
                break;
            case R.id.bn_checkexpress:
                startActivity(new Intent(this,CheckExpressActivity.class));
                break;
        }
    }
}
