package com.fanhong.cn.expressage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.fanhong.cn.App;
import com.fanhong.cn.R;
import com.fanhong.cn.fenxiao.PostSuccessActivity;
import com.fanhong.cn.util.JsonSyncUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
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
    @ViewInject(R.id.get_express_address)
    private EditText gAddress;
    @ViewInject(R.id.get_express_name)
    private EditText gName;
    @ViewInject(R.id.tv_ex_time)
    private TextView exTime;
    @ViewInject(R.id.tv_ex_type)
    private TextView exType;

//    private AMapLocationClient client;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        x.view().inject(this);
        title.setText("寄快递");
//        client = new AMapLocationClient(getApplicationContext());
//        getLocation();
    }

//    private void getLocation(){
//        AMapLocation location = client.getLastKnownLocation();
//        if(location!=null && location.getErrorCode()==0){
//            sProvince.setText(location.getProvince());
//            sCity.setText(location.getCity());
//            sDistrict.setText(location.getDistrict());
//        }
//    }

    @Event({R.id.img_back, R.id.ll_express_time, R.id.ll_express_type,R.id.submit_send_express})
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
            case R.id.submit_send_express:
                if(TextUtils.isEmpty(sAddress.getText().toString())){
                    Toast.makeText(this,"请填写寄件人地址",Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(sName.getText().toString())){
                    Toast.makeText(this,"请填写寄件人姓名",Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(gAddress.getText().toString())){
                    Toast.makeText(this,"请填写收件人地址",Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(gName.getText().toString())){
                    Toast.makeText(this,"请填写收件人姓名",Toast.LENGTH_SHORT).show();
                }else {
                    submitMessage();
                }
                break;
        }
    }

    private void submitMessage(){
        RequestParams params = new RequestParams(App.CMDURL);
        params.addBodyParameter("cmd","73");
        params.addBodyParameter("jdizhi",sAddress.getText().toString());
        params.addBodyParameter("jmz",sName.getText().toString());
        params.addBodyParameter("sdizhi",gAddress.getText().toString());
        params.addBodyParameter("smz",gName.getText().toString());
        params.addBodyParameter("smtime",exTime.getText().toString());
        params.addBodyParameter("kdlx",exType.getText().toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                String cw = JsonSyncUtils.getJsonValue(s,"cw");
                if(cw.equals("0")){
                    Intent intent = new Intent(SendExpressActivity.this, PostSuccessActivity.class);
                    intent.putExtra("fromExpress",true);
                    startActivity(intent);
                }else {
                    Toast.makeText(SendExpressActivity.this,"提交失败，请重试",Toast.LENGTH_SHORT).show();
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
