package com.fanhong.cn.applydoors;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.fanhong.cn.R;
import com.fanhong.cn.SampleActivity;
import com.fanhong.cn.SampleConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/19.
 */

public class ChooseLouActivity extends SampleActivity{
    private ImageView backBtn;
    private LinearLayout loudongLayout;
    private String[] strings;
    private SampleConnection mSampleC;
    private final int CHOOSE_LOUDONG = 52;

    @Override
    public synchronized void connectFail(int type) {
        switch (type){
            case CHOOSE_LOUDONG:
                Toast.makeText(this,"数据异常错误1",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public synchronized void connectSuccess(JSONObject json, int type) {
        switch (type){
            case CHOOSE_LOUDONG:
                try {
                    Log.i("xq","选择楼栋json====>"+json.toString());
                    String str = json.getString("cw");
                    int cw = Integer.parseInt(str);
                    String str1 = json.getString("data");
                    if(cw == 0){
                        strings = str1.split(",");
                        for(int i=0;i<strings.length;i++){
                            TextView tv = new TextView(this);
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1,50);
                            layoutParams.setMargins(50,5,5,5);
                            tv.setLayoutParams(layoutParams);
                            tv.setGravity(Gravity.CENTER_VERTICAL);
                            tv.setTextSize(15.0f);
                            final String[] strings1 = strings[i].split("<");
                            tv.setText(strings1[0]);
                            loudongLayout.addView(tv);
                            tv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    intent.putExtra("loudongName",strings1[0]);
                                    intent.putExtra("loudongId",strings1[1]);
                                    setResult(52,intent);
                                    ChooseLouActivity.this.finish();
                                }
                            });
                        }
                    }else {
                        Toast.makeText(this,"数据异常错误",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (IndexOutOfBoundsException e){}
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooselou);
        init();
    }
    private void init(){
        backBtn = (ImageView) findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        loudongLayout = (LinearLayout) findViewById(R.id.loudong_layout);
        getLou();

    }

    private void getLou(){
        Intent intent = getIntent();
        String cellId = intent.getStringExtra("cellId");
        Map<String,Object> map = new HashMap<>();
        map.put("cmd","45");
        map.put("xid",cellId);
        Log.i("xq","map====>"+map.toString());
        if (mSampleC == null){
            mSampleC = new SampleConnection(this,CHOOSE_LOUDONG);
        }
        mSampleC.connectService1(map);
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            return true;
        }
    });
}
