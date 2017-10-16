package com.fanhong.cn.verification;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.MyLocationStyle;
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

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/9/8.
 */
@ContentView(R.layout.activity_input_caryuyue)
public class InputYuyueActivity extends Activity implements LocationSource,
        AMapLocationListener {
    @ViewInject(R.id.tv_title)
    private TextView title;
    @ViewInject(R.id.default_location)
    private EditText editText1;
    @ViewInject(R.id.car_hostname)
    private EditText editText2;
    @ViewInject(R.id.car_hostphone)
    private EditText editText3;
    @ViewInject(R.id.input_code)
    private EditText editText4;
    @ViewInject(R.id.get_code)
    private TextView getCode;
    @ViewInject(R.id.agree_sheet_protocol)
    private CheckBox isAgree;
    @ViewInject(R.id.sheet_protocol)
    private TextView sheetText;
    @ViewInject(R.id.submit_caryuyue)
    private Button submit;
    @ViewInject(R.id.gaode_map)
    private MapView mapView;

    private Button agreeSheet;


    private AMap aMap;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;

    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private boolean ifAgree = true;
    private Bundle bundle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        x.view().inject(this);
        title.setText("年审预约");
        sheetText.setText("《代办年审须知》");
        isAgree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ifAgree = true;
                } else {
                    ifAgree = false;
                }
            }
        });
        bundle = savedInstanceState;
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermission();
        } else {
            init(bundle);
        }

    }

    private void init(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
    }

    private void requestPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "没有权限,请手动开启定位权限", Toast.LENGTH_SHORT).show();
            // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
            ActivityCompat.requestPermissions(InputYuyueActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE}, 100);
        } else {
            init(bundle);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 获取到权限，作相应处理（调用定位SDK应当确保相关权限均被授权，否则可能引起定位失败）
                init(bundle);
            } else {
                // 没有获取到权限，做特殊处理
                Toast.makeText(getApplicationContext(), "获取位置权限失败，请手动开启", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Event({R.id.img_back, R.id.get_code, R.id.sheet_protocol, R.id.submit_caryuyue})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.get_code:
                if (StringUtils.validPhoneNum("2", editText3.getText().toString())) {
                    getCode(editText3.getText().toString());
                } else {
                    Toast.makeText(this, R.string.phoneiswrong, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.sheet_protocol:
                createDialog();
                break;
            case R.id.submit_caryuyue:
                if (TextUtils.isEmpty(editText1.getText())) {
                    Toast.makeText(this, "请输入地址！", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(editText2.getText())) {
                    Toast.makeText(this, "请输入姓名！", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(editText3.getText()) || !StringUtils.validPhoneNum("2", editText3.getText().toString())) {
                    Toast.makeText(this, "请输入正确的手机号！", Toast.LENGTH_SHORT).show();
//                } else if (!getCode.getText().toString().equals("")) {
//                    Toast.makeText(this, "验证码错误！", Toast.LENGTH_SHORT).show();
                } else if (!ifAgree) {
                    Toast.makeText(this, "请阅读并同意代办年审须知！", Toast.LENGTH_SHORT).show();
                } else {
                    submitData();
                }
                break;
        }
    }

    private void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.layout_dialog_sheet, null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        agreeSheet = (Button) view.findViewById(R.id.read_and_agree);
        TextView textView = (TextView) view.findViewById(R.id.tv_content);
        textView.setText(R.string.carmustknows);
        agreeSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                isAgree.setChecked(true);
            }
        });
    }

    //获取手机验证码
    private void getCode(String str) {
        RequestParams params = new RequestParams(App.CMDURL);
        params.addBodyParameter("cmd", "1");
        params.addBodyParameter("name", str);
        startCount();
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                if (JsonSyncUtils.getJsonValue(s, "cw").equals("0")) {
                    Toast.makeText(InputYuyueActivity.this, R.string.getcode_success, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(InputYuyueActivity.this, R.string.getcode_reget, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void submitData() {
        RequestParams params = new RequestParams(App.CMDURL);
        params.addBodyParameter("cmd", "85");
        params.addBodyParameter("yzm", editText4.getText().toString());
//        params.addBodyParameter("mapdz",editText1.getText().toString());
//        params.addBodyParameter("name",editText2.getText().toString());
//        params.addBodyParameter("phone",editText3.getText().toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String cw = JsonSyncUtils.getJsonValue(result, "cw");
                if (cw.equals("1")) {
                    Toast.makeText(InputYuyueActivity.this, "短信验证码错误", Toast.LENGTH_SHORT).show();
                } else if (cw.equals("0")) {
                    goNext();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void goNext() {
        Intent intent = new Intent(this, CarFormConfirmActivity.class);
        intent.putExtra("address", editText1.getText().toString());
        intent.putExtra("name", editText2.getText().toString());
        intent.putExtra("phone", editText3.getText().toString());
        startActivity(intent);
    }

    private TimerTask timerTask;
    private Timer timer;

    public void startCount() {
        timer = new Timer();
        timerTask = new TimerTask() {
            int count = 30;

            @Override
            public void run() {
                if (count > 0) {
                    Message message = new Message();
                    message.what = 80;
                    message.arg1 = count;
                    handler.sendMessage(message);
                } else {
                    Message message1 = new Message();
                    message1.what = 81;
                    handler.sendMessage(message1);
                }
                count--;
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int tt = msg.what;
            switch (tt) {
                case 65:
                    mListener = (OnLocationChangedListener) msg.obj;
                    break;
                case 80:
                    getCode.setText(msg.arg1 + "秒后可重试");
                    getCode.setEnabled(false);
                    break;
                case 81:
                    getCode.setText("重新获取验证码");
                    getCode.setEnabled(true);
                    timer.cancel();
                    break;
            }
        }
    };

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        setupLocationStyle();
    }

    private void setupLocationStyle() {
        // 自定义系统定位蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        // 自定义定位蓝点图标
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.
                fromResource(R.drawable.gps_point));
        // 自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(STROKE_COLOR);
        //自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth(4);
        // 设置圆形的填充颜色
        myLocationStyle.radiusFillColor(FILL_COLOR);
        // 将自定义的 myLocationStyle 对象添加到地图上
        aMap.setMyLocationStyle(myLocationStyle);
    }

    /**
     * 定位成功回调
     *
     * @param aMapLocation
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        handler.sendEmptyMessage(65);
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null
                    && aMapLocation.getErrorCode() == 0) {
//                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
//                amapLocation.getLatitude();//获取纬度
//                amapLocation.getLongitude();//获取经度
//                amapLocation.getAccuracy();//获取精度信息
//                amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
//                amapLocation.getCountry();//国家信息
//                amapLocation.getProvince();//省信息
//                amapLocation.getCity();//城市信息
//                amapLocation.getDistrict();//城区信息
//                amapLocation.getStreet();//街道信息
//                amapLocation.getStreetNum();//街道门牌号信息
//                amapLocation.getCityCode();//城市编码
//                amapLocation.getAdCode();//地区编码
//                amapLocation.getAoiName();//获取当前定位点的AOI信息
//                amapLocation.getBuildingId();//获取当前室内定位的建筑物Id
//                amapLocation.getFloor();//获取当前室内定位的楼层
//                amapLocation.getGpsStatus();//获取GPS的当前状态
                //TODO 未能获取到建筑物id和室内楼层
//                Log.i("xq","BuildingId==>"+aMapLocation.getBuildingId());
//                Log.i("xq","Floor==>"+aMapLocation.getFloor());

                String[] strings = aMapLocation.getAddress().split("靠近");
                editText1.setText(strings[0]);
                editText1.setFocusable(true);
                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
                aMap.moveCamera(CameraUpdateFactory.zoomTo(18));
                //调用停止定位
                deactivate();
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                Log.i("AmapErr", errText);
//                mLocationErrText.setVisibility(View.VISIBLE);
//                mLocationErrText.setText(errText);
            }
        }
    }

    /**
     * 激活定位
     *
     * @param onLocationChangedListener
     */
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        Message msg = handler.obtainMessage(65);
        msg.obj = onLocationChangedListener;
        handler.sendMessage(msg);
        if (mlocationClient == null)
            mlocationClient = new AMapLocationClient(getApplicationContext());
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener(this);
        //设置为高精度定位模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //只定位一次
        mLocationOption.setOnceLocation(true);
        //如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
        mLocationOption.setNeedAddress(true);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求

        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        mlocationClient.startLocation();
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

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
        deactivate();
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
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }
}
