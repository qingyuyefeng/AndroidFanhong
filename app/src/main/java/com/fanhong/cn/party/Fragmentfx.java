package com.fanhong.cn.party;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fanhong.cn.App;
import com.fanhong.cn.R;
import com.fanhong.cn.party.activities.AddFxActivity;
import com.fanhong.cn.party.activities.DetailsActivity;
import com.fanhong.cn.party.adapters.FxAdapter;
import com.fanhong.cn.party.models.FxItemModel;
import com.fanhong.cn.util.JsonSyncUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/3.
 */
@ContentView(R.layout.fragment_fx)
public class Fragmentfx extends Fragment {

    @ViewInject(R.id.refresh_fx)
    SwipeRefreshLayout refreshLayout;
    @ViewInject(R.id.fx_recyclerview)
    RecyclerView recyclerView;

    private List<FxItemModel> list = new ArrayList<>();
    private FxAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = x.view().inject(this, inflater, container);
        return view;
    }

    private void getDatas() {
        RequestParams params = new RequestParams(App.CMDURL);
        params.addBodyParameter("cmd", "97");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONArray jsonArray = new JSONObject(result).getJSONArray("data");
                    for (int i=0;i<jsonArray.length();i++){
                        JSONObject object = jsonArray.getJSONObject(i);
                        FxItemModel model = new FxItemModel();
                        model.setPicUrl(object.optString("image",""));
                        model.setPhotoUrl(object.optString("logo",""));
                        model.setContent(object.optString("content",""));
                        model.setAuthor(object.optString("name",""));
                        model.setId(object.optInt("id",0));
                        model.setTime(new SimpleDateFormat("yyyy-MM-dd").format(Long.parseLong(object.optString("times"))));
                        list.add(model);
                    }
//                    Log.i("xq","list.size()==>"+list.size());
                    handler.sendEmptyMessage(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }


    @Event(R.id.add_fx)
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_fx:
                startActivity(new Intent(getActivity(), AddFxActivity.class));
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        list.clear();
        getDatas();
        Log.i("xq","list.size()==>"+list.size());
        adapter = new FxAdapter(getActivity(),list);
        adapter.setClick(new FxAdapter.ItemClick() {
            @Override
            public void itemclick(int id, String content) {
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("content",content);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list.clear();
                getDatas();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(0);
                        refreshLayout.setRefreshing(false);
                    }
                },2000);
            }
        });
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adapter.notifyDataSetChanged();
        }
    };

}
