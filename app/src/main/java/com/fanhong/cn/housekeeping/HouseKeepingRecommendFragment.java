package com.fanhong.cn.housekeeping;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanhong.cn.R;
import com.fanhong.cn.SampleConnection;
import com.fanhong.cn.adapters.HKRecyclerViewAdapter;
import com.fanhong.cn.bean.HousekeepingRecommendBean;
import com.fanhong.cn.models.BannerModel;
import com.sivin.Banner;
import com.sivin.BannerAdapter;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/19.
 */
@ContentView(R.layout.fragment_hk_recommend)
public class HouseKeepingRecommendFragment extends Fragment {
    @ViewInject(R.id.rv_hk_recommend)
    RecyclerView recyclerView;
    @ViewInject(R.id.banner_hk_recommend)
    Banner banner;

    private List<BannerModel> bannerModels = new ArrayList<>();
    BannerAdapter bannerAdapter;
    HKRecyclerViewAdapter adapter;

    List<HousekeepingRecommendBean> recommendDatas = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = x.view().inject(this, inflater, container);
        bannerAdapter = new BannerAdapter<BannerModel>(bannerModels) {

            @Override
            protected void bindTips(TextView tv, BannerModel bannerModel) {

            }

            @Override
            public void bindImage(ImageView imageView, BannerModel bannerModel) {
                x.image().bind(imageView, bannerModel.getImageUrl());
            }
        };
        banner.setBannerAdapter(bannerAdapter);
        banner.setOnBannerItemClickListener(new Banner.OnBannerItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String linkUrl=bannerModels.get(position).getLink();
            }
        });
        loadBannerImg();
        return view;
    }

    private void loadBannerImg() {
        RequestParams params = new RequestParams(SampleConnection.url);
        params.addParameter("cmd", "55");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                getBannerData(s);
                banner.setVisibility(View.VISIBLE);
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

    private void getBannerData(String s) {
        bannerModels.clear();
        bannerModels.add(new BannerModel().setLink("4小时家庭保洁").setImageUrl("assets://timg.png"));
        bannerModels.add(new BannerModel().setLink("厨房保洁").setImageUrl("http://img4.imgtn.bdimg.com/it/u=1383668407,3661848721&fm=200&gp=0.jpg"));
        bannerModels.add(new BannerModel().setLink("4小时家庭保洁").setImageUrl("http://imgsrc.baidu.com/image/c0%3Dshijue1%2C0%2C0%2C294%2C40/sign=adbb6307c2fcc3cea0cdc170fa2cbcfd/c2fdfc039245d6883e945d81aec27d1ed21b2405.jpg"));
        banner.notifyDataHasChanged();
    }

    private void initListdatas() {
        recommendDatas.add(new HousekeepingRecommendBean("assets://timg.png", "4小时家庭保洁", "199", "已接2654单", "好评90%"));
        recommendDatas.add(new HousekeepingRecommendBean("http://imgsrc.baidu.com/image/c0%3Dshijue1%2C0%2C0%2C294%2C40/sign=adbb6307c2fcc3cea0cdc170fa2cbcfd/c2fdfc039245d6883e945d81aec27d1ed21b2405.jpg", "厨房保洁", "690/套", "已接2694单", "好评96%"));
        recommendDatas.add(new HousekeepingRecommendBean("http://img4.imgtn.bdimg.com/it/u=1383668407,3661848721&fm=200&gp=0.jpg", "厨房保洁", "690/套", "已接2954单", "好评96%"));
        recommendDatas.add(new HousekeepingRecommendBean("http://img4.imgtn.bdimg.com/it/u=1383668407,3661848721&fm=200&gp=0.jpg", "4小时家庭保洁", "199", "已接26254单", "好评93%"));
        recommendDatas.add(new HousekeepingRecommendBean("assets://timg.png", "4小时家庭保洁", "199", "已接1654单", "好评90%"));

        adapter = new HKRecyclerViewAdapter(getActivity(), recommendDatas);
        adapter.setItemClickListener(new HKRecyclerViewAdapter.OnItemCLickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent=new Intent(getActivity(),HouseKeepingServiceDetailsActivity.class);
                intent.putExtra("title",recommendDatas.get(position).getTitle());
                intent.putExtra("price",recommendDatas.get(position).getPrice());
//                intent.putExtra("url",recommendDatas.get(position).getImgUrl());
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

    @Override
    public void onResume() {
        super.onResume();
        recommendDatas.clear();
        initListdatas();
    }
}
