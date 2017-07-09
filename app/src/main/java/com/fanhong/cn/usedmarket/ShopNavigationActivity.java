package com.fanhong.cn.usedmarket;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.fanhong.cn.R;
import com.fanhong.cn.SampleActivity;

import org.json.JSONObject;

/**
 * Created by Administrator on 2017/6/26.
 */

public class ShopNavigationActivity extends SampleActivity{
    private RadioGroup radioGroup;
    private RadioButton radioButton1,radioButton2;
    private Fragment showListFragment,addItemFragment;
    @Override
    public synchronized void connectSuccess(JSONObject json, int type) {
        super.connectSuccess(json, type);
    }

    @Override
    public synchronized void connectFail(int type) {
        super.connectFail(type);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usedmarkettop);
        initViews();
    }
    private void initDatas(){

    }
    private void initViews(){
        findViewById(R.id.usedmarket_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        radioGroup = (RadioGroup) findViewById(R.id.radio_usedmarket);
        radioButton1 = (RadioButton) findViewById(R.id.radio_usedmarket_first);
        radioButton2 = (RadioButton) findViewById(R.id.radio_usedmarket_second);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.radio_usedmarket_first:
                        radioButton1.setTextColor(getResources().getColor(R.color.skyblue));
                        radioButton2.setTextColor(getResources().getColor(R.color.white));
                        break;
                    case R.id.radio_usedmarket_second:
                        radioButton1.setTextColor(getResources().getColor(R.color.white));
                        radioButton2.setTextColor(getResources().getColor(R.color.skyblue));
                        break;
                }
            }
        });
    }
}
