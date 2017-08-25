package com.fanhong.cn.shippingaddress;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.fanhong.cn.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2017/8/22.
 */

public class TestAmapActivity extends Activity {
    private Button getLocation;
    private TextView showLocation;
    private MapView mapView;
    private AMap aMap;
    private AMapLocationClient locationClient = null;

    //    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
//    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_amap);
        initView();
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        setMapCustomStyleFile(this);
        locationClient = new AMapLocationClient(getApplicationContext());
        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AMapLocation location = locationClient.getLastKnownLocation();
                if (location != null && location.getErrorCode() == 0) {
//                    String result = Utils.getLocationStr(location);
                    String str1 = location.getProvince() + location.getCity() + location.getDistrict() +
                            location.getStreet();
                    showLocation.setText(str1);
                }
            }
        });
    }

    private void initView() {
        getLocation = (Button) findViewById(R.id.get_location);
        showLocation = (TextView) findViewById(R.id.show_location);
        mapView = (MapView) findViewById(R.id.gaode_map);
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

    @Override
    protected void onPause() {
        super.onStop();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
