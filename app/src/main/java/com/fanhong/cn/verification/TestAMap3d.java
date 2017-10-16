package com.fanhong.cn.verification;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.LocationSource.OnLocationChangedListener;
import com.amap.api.maps.MapView;
import com.amap.api.maps.CameraUpdateFactory;
import com.fanhong.cn.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2017/10/10.
 */

@ContentView(R.layout.activity_test_amap)
public class TestAMap3d extends Activity {
    @ViewInject(R.id.show_location)
    TextView mAddress;
    @ViewInject(R.id.check_style)
    CheckBox checkBox;
    @ViewInject(R.id.map)
    MapView mapView;

    AMap aMap;
    AMapLocationClient client;
    AMapLocationClientOption mLocationOption;
    OnLocationChangedListener listener;

    String address;


    LocationSource mSource = new LocationSource() {
        /**
         * 开始定位
         * @param onLocationChangedListener
         */
        @Override
        public void activate(OnLocationChangedListener onLocationChangedListener) {
            Message msg = handler.obtainMessage(111);
            msg.obj = onLocationChangedListener;
            handler.sendMessage(msg);
            if (client == null)
                client = new AMapLocationClient(getApplicationContext());
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            client.setLocationListener(mListener);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //只定位一次
            mLocationOption.setOnceLocation(true);
            //如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
            mLocationOption.setNeedAddress(true);
            //设置定位参数
            client.setLocationOption(mLocationOption);
            //开始定位
            client.startLocation();
        }

        /**
         * 停止定位
         */
        @Override
        public void deactivate() {
            listener = null;
            if (client != null) {
                client.stopLocation();
                client.onDestroy();
            }
            client = null;
        }
    };

    AMapLocationListener mListener = new AMapLocationListener() {
        /**
         * 定位成功的回调
         * @param aMapLocation
         */
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if(listener!=null&&aMapLocation!=null){
                if(aMapLocation.getErrorCode()==0){
                    Message msg = handler.obtainMessage(222);
                    msg.obj = aMapLocation.getAddress();
                    handler.sendMessage(msg);
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(18));
                    Log.i("xq","BuildingId==>"+aMapLocation.getBuildingId());
                    Log.i("xq","Floor==>"+aMapLocation.getFloor());
                    //调用停止定位
                    mSource.deactivate();
                }else {
                    Toast.makeText(TestAMap3d.this,"定位失败",Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        mapView.onCreate(savedInstanceState);
        init();
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                aMap.setMapCustomEnable(isChecked);
            }
        });
    }
    private void init(){
        if(aMap==null){
            aMap = mapView.getMap();
        }
        aMap.setLocationSource(mSource);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        setMapCustomStyleFile(this);
    }
    private void setMapCustomStyleFile(Context context) {
        String styleName = "style_json.json";
        FileOutputStream outputStream = null;
        InputStream inputStream = null;
        String filePath = null;
        try {
            inputStream = context.getAssets().open(styleName);
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);

            filePath = context.getFilesDir().getAbsolutePath();
            File file = new File(filePath + "/" + styleName);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            outputStream = new FileOutputStream(file);
            outputStream.write(b);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        aMap.setCustomMapStylePath(filePath + "/" + styleName);
        aMap.showMapText(false);
    }
    @Event({R.id.get_location,R.id.basicmap,R.id.rsmap,R.id.nightmap,R.id.navimap})
    private void onClick(View v){
        switch (v.getId()){
            case R.id.get_location:
                mAddress.setText(address);
                break;
            case R.id.basicmap:
                aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 矢量地图模式
                break;
            case R.id.rsmap:
                aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 卫星地图模式
                break;
            case R.id.nightmap:
                aMap.setMapType(AMap.MAP_TYPE_NIGHT);//夜景地图模式
                break;
            case R.id.navimap:
                aMap.setMapType(AMap.MAP_TYPE_NAVI);//导航地图模式
                break;
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 111:
                    listener = (OnLocationChangedListener) msg.obj;
                    break;
                case 222:
                    address = (String) msg.obj;
                    break;
            }
        }
    };

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
