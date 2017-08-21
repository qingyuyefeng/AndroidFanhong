package com.fanhong.cn.expressage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.fanhong.cn.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/10.
 */
@ContentView(R.layout.activity_express_type)
public class ChooseExpressActivity extends Activity{
    @ViewInject(R.id.tv_title)
    private TextView title;
    @ViewInject(R.id.express_type_recyc)
    private RecyclerView mRecyclerView;

    private List<String> expressList = new ArrayList<>();
    private ExpressTypeAdapter typeAdapter;
    private int status = 0;

    private String[] expressage = {"顺丰快递","韵达快递","申通快递","中通快递","EMS","圆通快递","百世汇通"};
    private String[]  times = {"一小时以内","两小时以内","三小时以内","四小时以内"};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        Intent intent = getIntent();
        status = intent.getIntExtra("status",0);
        if(status == 1){
            title.setText("选择时间");
            initTime();
            typeAdapter = new ExpressTypeAdapter(this,expressList,1);
        }else {
            title.setText("快递类型");
            initData();
            typeAdapter = new ExpressTypeAdapter(this,expressList,2);
        }

        mRecyclerView.setAdapter(typeAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
    }
    private void initData(){
        for(int i = 0;i < expressage.length;i++){
            expressList.add(expressage[i]);
        }
    }
    private void initTime(){
        for(int i = 0;i < times.length;i++){
            expressList.add(times[i]);
        }
    }
    @Event({R.id.img_back})
    private void onClick(View v){
        switch (v.getId()){
            case R.id.img_back:
                finish();
                break;
        }
    }
}
