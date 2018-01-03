package com.fanhong.cn.applydoors;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fanhong.cn.App;
import com.fanhong.cn.R;
import com.fanhong.cn.util.JsonSyncUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by Administrator on 2017/7/5.
 */

public class OpenDoorActivity extends Activity {
    private ImageView backBtn, openDoorBtn;
    private TextView cellName, louNumber, status;
    private String miyue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menjin);
        initViews();
    }

    private void initViews() {
        backBtn = (ImageView) findViewById(R.id.back_btn);
        backBtn.setOnClickListener(ocl);
        openDoorBtn = (ImageView) findViewById(R.id.open_door);
        openDoorBtn.setOnClickListener(ocl);
        cellName = (TextView) findViewById(R.id.cellName);
        louNumber = (TextView) findViewById(R.id.lounumber);
        status = (TextView) findViewById(R.id.connect_status);

        Intent intent = getIntent();
        miyue = intent.getStringExtra("miyue");
        cellName.setText(intent.getStringExtra("cellName"));
        louNumber.setText(intent.getStringExtra("louNumber"));
        if (isNetworkAvailable(this)) {
            status.setText(R.string.connected);
            status.setTextColor(getResources().getColor(R.color.skyblue));
        } else {
            status.setText(R.string.connectfail);
            status.setTextColor(getResources().getColor(R.color.warnred));
        }
    }

    View.OnClickListener ocl = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back_btn:
                    finish();
                    break;
                case R.id.open_door:
                    openDoor(miyue);
                    Toast.makeText(OpenDoorActivity.this, "正在开门...", Toast.LENGTH_LONG).show();
                    openDoorBtn.setEnabled(false);
//                    openDoor("b6dcdeb66926fd8850096e99d3bdb2c5");
//                    Toast.makeText(OpenDoorActivity.this,R.string.opendoor,Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    //判断是否联网
    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        } else {
            //如果仅仅是用来判断网络连接
            //则可以使用 cm.getActiveNetworkInfo().isAvailable();
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //访问接口开门
    public synchronized void openDoor(String str) {
        RequestParams params = new RequestParams(App.OPEN_URL);
        params.addParameter("key", str);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (JsonSyncUtils.getJsonValue(result, "error").equals("succ")) {
                    String s = JsonSyncUtils.getJsonValue(result, "data");
                    final String uuid = JsonSyncUtils.getJsonValue(s, "cmd_uuid");
                    Log.i("xq",s+".."+uuid);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            checkOpen(uuid);
                        }
                    },3000);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                handler.sendEmptyMessage(12);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void checkOpen(String uuid) {
        RequestParams params = new RequestParams(App.CHECK_URL);
        params.addBodyParameter("cmd_uuid", uuid);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("xq","reslut==>"+result);
                if(JsonSyncUtils.getJsonValue(result,"result").equals("0")){
                    handler.sendEmptyMessage(11);
                }else {
//                    网络延时超过3秒的则不提示了
//                    handler.sendEmptyMessage(12);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                handler.sendEmptyMessage(12);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                handler.sendEmptyMessage(13);
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 11:
                    Toast.makeText(OpenDoorActivity.this, "开门成功！", Toast.LENGTH_SHORT).show();
                    break;
                case 12:
                    Toast.makeText(OpenDoorActivity.this, "开门失败，请重试！", Toast.LENGTH_SHORT).show();
                    break;
                case 13:
                    openDoorBtn.setEnabled(true);
                    break;
            }
        }
    };
}
