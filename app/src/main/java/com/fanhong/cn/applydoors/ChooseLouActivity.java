package com.fanhong.cn.applydoors;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fanhong.cn.App;
import com.fanhong.cn.R;
import com.fanhong.cn.util.JsonSyncUtils;
import com.zhy.autolayout.AutoLinearLayout;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by Administrator on 2017/6/19.
 */

public class ChooseLouActivity extends Activity{
    private ImageView backBtn;
    private TextView title;
    private AutoLinearLayout loudongLayout;
    private String[] strings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooselou);
        init();
        title.setText("选择楼栋号");
    }
    private void init(){
        backBtn = (ImageView) findViewById(R.id.img_back);
        title = (TextView) findViewById(R.id.tv_title);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        loudongLayout = (AutoLinearLayout) findViewById(R.id.loudong_layout);
        getLou();
    }

    private void getLou(){
        Intent intent = getIntent();
        String cellId = intent.getStringExtra("cellId");
        RequestParams params = new RequestParams(App.CMDURL);
        params.addBodyParameter("cmd","45");
        params.addBodyParameter("xid",cellId);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                if(JsonSyncUtils.getJsonValue(s,"cw").equals("0")){
                    String data = JsonSyncUtils.getJsonValue(s,"data");
                    strings = data.split(",");
                    for(int i=0;i<strings.length;i++){
                        TextView tv = new TextView(ChooseLouActivity.this);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1,90);
                        layoutParams.setMargins(50,5,50,5);
                        tv.setLayoutParams(layoutParams);
                        tv.setBackgroundResource(R.drawable.postpicture_biankuang);
                        tv.setGravity(Gravity.CENTER);
                        tv.setTextSize(16.0f);
                        final String[] strings1 = strings[i].split("<");
                        tv.setText(strings1[0]);
                        tv.setPadding(25,5,25,5);
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
                    Toast.makeText(ChooseLouActivity.this,"选择小区错误",Toast.LENGTH_SHORT).show();
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
}
