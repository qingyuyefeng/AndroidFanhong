package com.fanhong.cn.expressage;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fanhong.cn.R;
import com.zhy.autolayout.AutoLinearLayout;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/9.
 */

@ContentView(R.layout.activity_check_express)
public class CheckExpressActivity extends Activity{
    @ViewInject(R.id.tv_title)
    private TextView title;
    @ViewInject(R.id.input_express_number)
    private EditText edtExpressNumber;
    @ViewInject(R.id.my_send_express)
    private TextView tvsend;
    @ViewInject(R.id.line_send_express)
    private View linesend;
    @ViewInject(R.id.my_send_ex_recyc)
    private ListView msRecyclerView;
    @ViewInject(R.id.my_get_express)
    private TextView tvget;
    @ViewInject(R.id.line_get_express)
    private View lineget;
    @ViewInject(R.id.my_get_ex_recyc)
    private ListView mgRecyclerView;
    @ViewInject(R.id.if_no_express)
    private AutoLinearLayout layout;

    private List<MysendModel> mysendModelList = new ArrayList<>();
    private MySendexpressAdapter mySendexpressAdapter;
    private List<MysendModel> mygetModelList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        title.setText("查快递");
        initData();
        mySendexpressAdapter = new MySendexpressAdapter(this,mysendModelList);
        mySendexpressAdapter.setFollowClick(new MySendexpressAdapter.FollowClick() {
            @Override
            public void btnClick() {
                Intent intent = new Intent();
                intent.setClass(CheckExpressActivity.this,FollowOrderActivity.class);
                startActivity(intent);
            }
        });
        msRecyclerView.setAdapter(mySendexpressAdapter);
        tvsend.setText("我寄的（"+mysendModelList.size()+"）");
        tvget.setText("我收的（"+mygetModelList.size()+"）");
    }
    @Event({R.id.img_back,R.id.img_search_express,R.id.my_send_express,R.id.my_get_express})
    private void onClick(View v){
        switch (v.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.img_search_express:
                break;
            case R.id.my_send_express:
                linesend.setVisibility(View.VISIBLE);
                lineget.setVisibility(View.INVISIBLE);
                hideData();
                if(mysendModelList.size()>0){
                    msRecyclerView.setVisibility(View.VISIBLE);
                }else {
                    layout.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.my_get_express:
                linesend.setVisibility(View.INVISIBLE);
                lineget.setVisibility(View.VISIBLE);
                hideData();
                if(mygetModelList.size()>0){
                    mgRecyclerView.setVisibility(View.VISIBLE);
                }else {
                    layout.setVisibility(View.VISIBLE);
                }
                break;
        }
    }
    private void initData(){
        for(int i=0;i<3;i++){
            MysendModel mysendModel = new MysendModel();
            mysendModelList.add(mysendModel);
        }
    }
    private void hideData(){
        msRecyclerView.setVisibility(View.GONE);
        mgRecyclerView.setVisibility(View.GONE);
        layout.setVisibility(View.GONE);
    }
}
