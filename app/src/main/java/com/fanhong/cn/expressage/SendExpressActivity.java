package com.fanhong.cn.expressage;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.fanhong.cn.App;
import com.fanhong.cn.R;
import com.fanhong.cn.fenxiao.PostSuccessActivity;
import com.fanhong.cn.listviews.SpinerPopWindow;
import com.fanhong.cn.util.JsonSyncUtils;
import com.fanhong.cn.util.MySharedPrefUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/9.
 */

@ContentView(R.layout.activity_sendexpress)
public class SendExpressActivity extends Activity {
    @ViewInject(R.id.tv_title)
    private TextView title;
//    @ViewInject(R.id.send_express_province)
//    private TextView sProvince;
//    @ViewInject(R.id.send_express_city)
//    private TextView sCity;
//    @ViewInject(R.id.send_express_district)
//    private TextView sDistrict;
    @ViewInject(R.id.send_express_address)
    private EditText sAddress;
    @ViewInject(R.id.send_express_name)
    private EditText sName;
    @ViewInject(R.id.choose_get_city)
    private TextView chooseReCity;
    @ViewInject(R.id.get_express_address)
    private EditText gAddress;
    @ViewInject(R.id.get_express_name)
    private EditText gName;
    @ViewInject(R.id.tv_ex_time)
    private TextView exTime;
    @ViewInject(R.id.tv_ex_type)
    private TextView exType;

    private AMapLocationClient client;
    private SpinerPopWindow<String> spw;
    private List<String> provinces = new ArrayList<>();
    private String seProvince;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        x.view().inject(this);
        title.setText("寄快递");
        client = new AMapLocationClient(getApplicationContext());

        if (Build.VERSION.SDK_INT >= 23) {
            requestPermission();
        } else {
            getLocation();
        }
    }

    private void getLocation() {
        AMapLocation location = client.getLastKnownLocation();
        if (location != null && location.getErrorCode() == 0) {
            String[] strings = location.getAddress().split("靠近");
            sAddress.setText(strings[0]);
            seProvince = location.getProvince();
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
            ActivityCompat.requestPermissions(SendExpressActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE}, 110);
        } else {
            getLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 110) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 获取到权限，作相应处理（调用定位SDK应当确保相关权限均被授权，否则可能引起定位失败）
                getLocation();
            } else {
                // 没有获取到权限，做特殊处理
                Toast.makeText(getApplicationContext(), "获取位置权限失败，请手动开启", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Event({R.id.img_back, R.id.choose_get_city, R.id.ll_express_time, R.id.ll_express_type, R.id.submit_send_express})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.choose_get_city:
                getProvinces();
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
            case R.id.submit_send_express:
                if (TextUtils.isEmpty(sAddress.getText().toString())) {
                    Toast.makeText(this, "请填写寄件人地址", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(sName.getText().toString())) {
                    Toast.makeText(this, "请填写寄件人姓名", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(gAddress.getText().toString())) {
                    Toast.makeText(this, "请填写收件人地址", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(gName.getText().toString())) {
                    Toast.makeText(this, "请填写收件人姓名", Toast.LENGTH_SHORT).show();
                } else {
                    submitMessage();
                }
                break;
        }
    }

    private void getProvinces() {
        RequestParams params = new RequestParams(App.CMDURL);
        params.addBodyParameter("cmd", "89");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (JsonSyncUtils.getJsonValue(result, "cw").equals("0")) {
                    provinces = JsonSyncUtils.getStringList(JsonSyncUtils.getJsonValue(result, "data"), "province");
                    spw = new SpinerPopWindow<String>(SendExpressActivity.this, provinces, new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            chooseReCity.setText(provinces.get(position));
                            spw.dismiss();
                        }
                    },"");
                    spw.setWidth(chooseReCity.getWidth());
                    spw.showAsDropDown(chooseReCity,0,0);
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

    private void submitMessage() {
        RequestParams params = new RequestParams(App.CMDURL);
        params.addBodyParameter("cmd", "73");
        params.addBodyParameter("uid", MySharedPrefUtils.getUserId(this));
        params.addBodyParameter("jsf",seProvince);
        params.addBodyParameter("jdizhi", sAddress.getText().toString());
        params.addBodyParameter("jmz", sName.getText().toString());
        params.addBodyParameter("ssf",chooseReCity.getText().toString());
        params.addBodyParameter("sdizhi", gAddress.getText().toString());
        params.addBodyParameter("smz", gName.getText().toString());
        params.addBodyParameter("smtime", exTime.getText().toString());
        params.addBodyParameter("kdlx", exType.getText().toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                String cw = JsonSyncUtils.getJsonValue(s, "cw");
                if (cw.equals("0")) {
                    Intent intent = new Intent(SendExpressActivity.this, PostSuccessActivity.class);
                    intent.putExtra("fromExpress", true);
                    startActivity(intent);
                } else {
                    Toast.makeText(SendExpressActivity.this, "提交失败，请重试", Toast.LENGTH_SHORT).show();
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
