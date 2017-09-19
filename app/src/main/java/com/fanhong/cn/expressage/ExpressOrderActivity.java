package com.fanhong.cn.expressage;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.fanhong.cn.R;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/9.
 */
@ContentView(R.layout.activity_check_express)
public class ExpressOrderActivity extends Activity{
    @ViewInject(R.id.tv_title)
    private TextView title;
    @ViewInject(R.id.check_layout)
    private AutoRelativeLayout checkLayout;
    @ViewInject(R.id.wuliu_layout)
    private AutoLinearLayout wuliuLayout;
    @ViewInject(R.id.my_send_ex_recyc)
    private ListView sListView;


    private List<MysendModel> mysendModelList = new ArrayList<>();
    private MySendexpressAdapter mySendexpressAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        initDatas();
        initViews();
        mySendexpressAdapter = new MySendexpressAdapter(this,mysendModelList);
        sListView.setAdapter(mySendexpressAdapter);
    }
    private void initViews(){
        title.setText("我的运单");
        checkLayout.setVisibility(View.GONE);
        wuliuLayout.setVisibility(View.GONE);
        sListView.setVisibility(View.VISIBLE);
    }
    private void initDatas(){
        for(int i=0;i<3;i++){
            MysendModel mysendModel = new MysendModel();
            mysendModelList.add(mysendModel);
        }
    }
    @Event(R.id.img_back)
    private void onClick(View v){
        finish();
    }
}
