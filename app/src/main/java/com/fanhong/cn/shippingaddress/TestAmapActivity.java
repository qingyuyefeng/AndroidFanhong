package com.fanhong.cn.shippingaddress;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.MapView;
import com.fanhong.cn.R;

/**
 * Created by Administrator on 2017/8/22.
 */

public class TestAmapActivity extends Activity{
    private Button getLocation;
    private TextView showLocation;
    private MapView mapView;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_amap);
        initView();
        locationClient = new AMapLocationClient(getApplicationContext());
        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AMapLocation location = locationClient.getLastKnownLocation();
                if(location!=null&&location.getErrorCode()==0){
//                    String result = Utils.getLocationStr(location);
                    String str1 = location.getProvince()+location.getCity()+location.getDistrict()+
                            location.getStreet();
                    showLocation.setText(str1);
                }
            }
        });
    }
    private void initView(){
        getLocation = (Button) findViewById(R.id.get_location);
        showLocation = (TextView) findViewById(R.id.show_location);
        mapView = (MapView) findViewById(R.id.gaode_map);
    }
}
