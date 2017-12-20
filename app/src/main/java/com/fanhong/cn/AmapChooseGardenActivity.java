package com.fanhong.cn;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.fanhong.cn.util.JsonSyncUtils;
import com.fanhong.cn.util.MySharedPrefUtils;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by Administrator on 2017/12/15.
 */
@ContentView(R.layout.activity_amap_gardens)
public class AmapChooseGardenActivity extends Activity {
    @ViewInject(R.id.tv_title)
    private TextView title;
    @ViewInject(R.id.now_city)
    private TextView nowCity;
    @ViewInject(R.id.go_choose_garden)
    private TextView goChoose;
    @ViewInject(R.id.amap_gardens)
    private AutoLinearLayout gardenLayout;

    private SharedPreferences mSharedPref;
    private AMapLocationClient client;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        init();
    }

    private void init() {
        title.setText("附近小区");
        goChoose.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mSharedPref = MySharedPrefUtils.getSharedPref(AmapChooseGardenActivity.this);
        client = new AMapLocationClient(getApplicationContext());
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermission();
        } else {
            getLocation();
        }
    }

    @Event({R.id.img_back, R.id.go_choose_garden})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.go_choose_garden:
                Intent intent = new Intent(this, GardenSelecterActivity.class);
                startActivityForResult(intent, 100);
                break;
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
            ActivityCompat.requestPermissions(AmapChooseGardenActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE}, 130);
        } else {
            getLocation();
        }
    }

    private void getLocation() {
        final AMapLocation location = client.getLastKnownLocation();
        if (null != location) {
            if (location.getErrorCode() == 0) {
                Log.i("xq", "定位结果==>" + location.getAddress());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        nowCity.setText(location.getCity());
                    }
                });
                getGardens(location.getLongitude(), location.getLatitude());
            }
        } else {
            Log.i("xq", "" + location.getErrorCode());
            Log.i("xq", "无法定位==>location为" + location);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 130) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 获取到权限，作相应处理（调用定位SDK应当确保相关权限均被授权，否则可能引起定位失败）
                getLocation();
            } else {
                // 没有获取到权限，做特殊处理
                Toast.makeText(getApplicationContext(), "获取位置权限失败，请手动开启", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 51) {
            String string = data.getStringExtra("gardenName");
            String id = data.getStringExtra("gardenId");
            String property = data.getStringExtra("gardenProperty");
            Intent intent = new Intent();
            intent.putExtra("gardenName", string);
            intent.putExtra("gardenId", id);
            intent.putExtra("gardenProperty", property);
            setResult(51, intent);
            AmapChooseGardenActivity.this.finish();
        }
    }

    private void getGardens(double x, double y) {
        Log.i("xq", "定位的经纬度==>" + x + "，" + y);
        RequestParams params = new RequestParams(App.CMDURL);
        params.addBodyParameter("cmd", "123");
        params.addBodyParameter("x", x + "");
        params.addBodyParameter("y", y + "");
        org.xutils.x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (JsonSyncUtils.getJsonValue(result, "cmd").equals("124")) {
                    try {
                        JSONArray jsonArray = new JSONObject(result).getJSONArray("data");
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                final String id = object.optString("id");
                                final String name = object.optString("name");
                                TextView textView = new TextView(AmapChooseGardenActivity.this);
                                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(-1, -2);
                                param.setMargins(0, 15, 0, 0);
                                textView.setLayoutParams(param);
                                textView.setPadding(3, 3, 0, 3);
                                textView.setText(name);
                                textView.setTextColor(getResources().getColor(R.color.text_3));
                                textView.setTextSize(17.5f);
                                textView.setBackground(getDrawable(R.drawable.postpicture_biankuang));
                                textView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mSharedPref.edit().putString("gardenName", name).commit();
                                        mSharedPref.edit().putString("gardenId", id).commit();
                                        Intent intent = new Intent();
                                        intent.putExtra("gardenName", name);
                                        intent.putExtra("gardenId", id);
                                        setResult(51, intent);
                                        AmapChooseGardenActivity.this.finish();
                                    }
                                });
                                gardenLayout.addView(textView);
                            }
                        } else {
                            TextView t = new TextView(AmapChooseGardenActivity.this);
                            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(200, -2);
                            p.setMargins(0, 30, 0, 0);
                            t.setLayoutParams(p);
                            t.setPadding(5, 3, 5, 3);
                            t.setTextColor(getResources().getColor(R.color.text_6));
                            t.setTextSize(17.0f);
                            t.setText("没有找到你附近的小区，请前往所有小区列表进行选择");
                            gardenLayout.addView(t);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
}
