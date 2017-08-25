package com.fanhong.cn.expressage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.fanhong.cn.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by Administrator on 2017/8/9.
 */

@ContentView(R.layout.activity_sendexpress)
public class SendExpressActivity extends Activity {
    @ViewInject(R.id.tv_title)
    private TextView title;
    @ViewInject(R.id.send_express_province)
    private TextView sProvince;
    @ViewInject(R.id.send_express_city)
    private TextView sCity;
    @ViewInject(R.id.send_express_district)
    private TextView sDistrict;
    @ViewInject(R.id.tv_ex_time)
    private TextView exTime;
    @ViewInject(R.id.tv_ex_type)
    private TextView exType;

//    private String str1,str2,str3;
    private AMapLocationClient client;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        x.view().inject(this);
        title.setText("寄快递");
        client = new AMapLocationClient(getApplicationContext());
        getLocation();
    }

    private void getLocation(){
        AMapLocation location = client.getLastKnownLocation();
        if(location!=null && location.getErrorCode()==0){
            sProvince.setText(location.getProvince());
            sCity.setText(location.getCity());
            sDistrict.setText(location.getDistrict());
        }
    }

    @Event({R.id.img_back, R.id.ll_express_time, R.id.ll_express_type})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.ll_express_time:
                Intent intent = new Intent(this, ChooseExpressActivity.class);
                intent.putExtra("status", 1);
                startActivityForResult(intent, 0);
                break;
            case R.id.ll_express_type:
                Intent intent1 = new Intent(this, ChooseExpressActivity.class);
                intent1.putExtra("status", 2);
                startActivityForResult(intent1, 0);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 46) {
            String type = data.getStringExtra("string");
            exType.setText(type);
        } else if (resultCode == 45) {
            String type = data.getStringExtra("string");
            exTime.setText(type);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
}
