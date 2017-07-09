package com.fanhong.cn.applydoors;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanhong.cn.R;
import com.fanhong.cn.SampleConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2017/7/5.
 */

public class OpenDoorActivity extends Activity{
    private ImageView backBtn,openDoorBtn;
    private TextView cellName,louNumber,status;
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
        if(isNetworkAvailable(this)){
            status.setText(R.string.connected);
            status.setTextColor(getResources().getColor(R.color.skyblue));
        }else {
            status.setText(R.string.connectfail);
            status.setTextColor(getResources().getColor(R.color.warnred));
        }
    }

    View.OnClickListener ocl = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.back_btn:
                    finish();
                    break;
                case R.id.open_door:
                    new Thread(){
                        @Override
                        public void run() {
                            openDoor(miyue);
                        }
                    }.start();
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
    public synchronized void openDoor(String str){
        String string = SampleConnection.OPEN_LOCKED_URL;
        Log.i("xq","开门***url====>"+string);
        OutputStream os = null;
        try {
            URL url = new URL(string);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setReadTimeout(5000);
            http.setConnectTimeout(5000);
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setDoInput(true);
            http.setUseCaches(false);
            String content = str;
            os = http.getOutputStream();
            os.write(content.getBytes());
            int res = http.getResponseCode();
            Log.i("xq","返回码***res======>"+res);
            if(res == 200){
                BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream(),"utf-8"));
                StringBuffer sb = new StringBuffer();
                String s;
                while((s = br.readLine())!=null){
                    sb.append(s);
                }
                JSONObject jsonObject = new JSONObject(sb.toString());
                String status = jsonObject.optString("data");
                if(status.equals("ok")){
                    //开门提示
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
