package com.fanhong.cn.expressage;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.fanhong.cn.R;

import org.xutils.view.annotation.ContentView;
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

    private String[] expressage = {"顺丰快递,韵达快递,申通快递,中通快递,EMS,圆通快递,百世汇通"};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        title.setText("快递类型");
        initData();
        typeAdapter = new ExpressTypeAdapter(this,expressList);
        mRecyclerView.setAdapter(typeAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
    }
    private void initData(){
        for(int i = 0;i < expressage.length;i++){
            expressList.add(expressage[i]);
        }
    }
}
