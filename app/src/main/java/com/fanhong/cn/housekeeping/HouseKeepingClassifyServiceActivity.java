package com.fanhong.cn.housekeeping;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.fanhong.cn.R;
import com.fanhong.cn.adapters.HKRecyclerViewAdapter;
import com.fanhong.cn.bean.HousekeepingRecommendBean;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import static com.alipay.sdk.app.statistic.c.v;

/**
 * Created by Administrator on 2017/7/20.
 */
@ContentView(R.layout.activity_hk_classify_service)
public class HouseKeepingClassifyServiceActivity extends Activity {
    @ViewInject(R.id.rv_hk_classify_service)
    private RecyclerView recyclerView;
    @ViewInject(R.id.tv_chat_title)
    private TextView tv_title;

    private HKRecyclerViewAdapter adapter;
    private List<HousekeepingRecommendBean> datas = new ArrayList<>();
    private String classify = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        classify = getIntent().getStringExtra("classify");
        tv_title.setText(classify);
        initListdatas();
    }
    @Event(R.id.tv_back)
    private void onBackClick(View v){
        finish();
    }
    private void initListdatas() {
        datas.add(new HousekeepingRecommendBean("assets://timg.png", "4小时家庭保洁", "199", "已接2654单", "好评90%"));
        datas.add(new HousekeepingRecommendBean("http://imgsrc.baidu.com/image/c0%3Dshijue1%2C0%2C0%2C294%2C40/sign=adbb6307c2fcc3cea0cdc170fa2cbcfd/c2fdfc039245d6883e945d81aec27d1ed21b2405.jpg", "厨房保洁", "690/套", "已接2694单", "好评96%"));
        datas.add(new HousekeepingRecommendBean("http://img4.imgtn.bdimg.com/it/u=1383668407,3661848721&fm=200&gp=0.jpg", "厨房保洁", "690/套", "已接2954单", "好评96%"));
        datas.add(new HousekeepingRecommendBean("http://img4.imgtn.bdimg.com/it/u=1383668407,3661848721&fm=200&gp=0.jpg", "4小时家庭保洁", "199", "已接26254单", "好评93%"));
        datas.add(new HousekeepingRecommendBean("assets://timg.png", "4小时家庭保洁", "199", "已接1654单", "好评90%"));

        adapter = new HKRecyclerViewAdapter(this, datas);
        adapter.setItemClickListener(new HKRecyclerViewAdapter.OnItemCLickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent=new Intent(HouseKeepingClassifyServiceActivity.this,HouseKeepingServiceDetailsActivity.class);
                intent.putExtra("title",datas.get(position).getTitle());
                intent.putExtra("price",datas.get(position).getPrice());
//                intent.putExtra("url",datas.get(position).getImgUrl());
                startActivity(intent);
            }
//        }).setItemLongClickListener(new HKRecyclerViewAdapter.OnItemLongCLickListener() {
//            @Override
//            public void onItemLongClick(View v, int position) {
//
//            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
    }

}
