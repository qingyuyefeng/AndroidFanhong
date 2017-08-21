package com.fanhong.cn.expressage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
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

    private LocationClient mLocationClient;
    private MyLocationListener listener;
    private String str1, str2, str3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        x.view().inject(this);
        initLocation();
        title.setText("寄快递");

    }

    private void initLocation() {
        mLocationClient = new LocationClient(this);
        listener = new MyLocationListener();
        mLocationClient.registerLocationListener(listener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
//        int span = 1000;
//        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
        mLocationClient.start();
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

    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
//            MyLocationData data = new MyLocationData.Builder()
//                    .accuracy(bdLocation.getRadius())
//                    .latitude(bdLocation.getLatitude())
//                    .longitude(bdLocation.getLongitude())
//                    .build();
//            Log.i("xq", bdLocation.getStreet() + "==>" + bdLocation.getStreetNumber());
            str1 = bdLocation.getProvince();
            str2 = bdLocation.getCity();
            str3 = bdLocation.getDistrict();
            Log.i("xq",str1);
            Log.i("xq",str2);
            Log.i("xq",str3);
            Message msg = handler.obtainMessage();
            msg.what = 1;
            msg.obj = str1+","+str2+","+str3;
            handler.sendMessage(msg);
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    String s = (String) msg.obj;
                    String[] ss = s.split(",");
                    sProvince.setText(ss[0]);
                    sCity.setText(ss[1]);
                    sDistrict.setText(ss[2]);
                    break;
            }
        }
    };
}
