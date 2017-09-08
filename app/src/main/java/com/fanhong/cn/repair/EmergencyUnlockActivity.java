package com.fanhong.cn.repair;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.fanhong.cn.App;
import com.fanhong.cn.R;
import com.fanhong.cn.applydoors.DoorRecycAdapter;
import com.fanhong.cn.models.DoorcheckModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/6.
 */
@ContentView(R.layout.activity_unlocked)
public class EmergencyUnlockActivity extends Activity{
    @ViewInject(R.id.tv_title)
    private TextView title;
    @ViewInject(R.id.show_location)
    private TextView location;
    @ViewInject(R.id.swip_refresh)
    private SwipeRefreshLayout swipRefresh;
    @ViewInject(R.id.unlock_recyclerview)
    private RecyclerView unlockRecyclerView;

    private List<DoorcheckModel> modelList = new ArrayList<>();
    private DoorRecycAdapter adapter;
    private int firstItemPosition ,lastItemPosition ;
//    private LinearLayoutManager mLayoutManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        title.setText(R.string.service_lock);

        getData();
        adapter = new DoorRecycAdapter(this,modelList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        unlockRecyclerView.setLayoutManager(mLayoutManager);
        unlockRecyclerView.setAdapter(adapter);
        unlockRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if(layoutManager instanceof LinearLayoutManager){
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                    firstItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    lastItemPosition  = linearLayoutManager.findLastVisibleItemPosition();
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && lastItemPosition  + 1 == adapter.getItemCount()) {
                        handler.sendEmptyMessageDelayed(1, 3000);
                    }
                    if(modelList.get(firstItemPosition) instanceof DoorcheckModel){

                    }else {
                        //重新加载
                        handler.sendEmptyMessageDelayed(1, 3000);
                    }
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });
        swipRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.sendEmptyMessageDelayed(0, 3000);
            }
        });
    }
    @Event(value = {R.id.img_back,R.id.choose_cell},type = View.OnClickListener.class)
    private void onClick(View v){
        switch (v.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.choose_cell:
                break;
        }
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    //重新访问接口刷新布局
                    getData();
                    break;
                case 1:
                    break;
            }
        }
    };
    private void getData(){
        RequestParams params = new RequestParams(App.CMDURL);
        params.addBodyParameter("cmd","41");
        params.addBodyParameter("uid",getSharedPreferences("Setting", Context.MODE_PRIVATE).getString("UserId",""));
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String data = jsonObject.optString("data", "");
//            Log.i("xq",data);
                    String data1 = jsonObject.optString("data1", "");
//            Log.i("xq",data1);
                    if (!data.equals("0")) {
                        String[] array1 = data.split(",");
                        for (int i = 0; i < array1.length; i++) {
                            String[] arr = array1[i].split("<");
                            DoorcheckModel doorcheckModel = new DoorcheckModel();
                            doorcheckModel.setCellName(arr[0]);
                            doorcheckModel.setLouNumber(arr[1]);
                            doorcheckModel.setMiyue(arr[2]);
                            doorcheckModel.setStatus(0);
                            modelList.add(doorcheckModel);
                        }
                    }
                    if (!data1.equals("0")) {
                        String[] array2 = data1.split(",");
                        for (int i = 0; i < array2.length; i++) {
                            DoorcheckModel doorcheckModel = new DoorcheckModel();
                            doorcheckModel.setCellName(array2[i]);
                            doorcheckModel.setStatus(1);
                            modelList.add(doorcheckModel);
                        }
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IndexOutOfBoundsException e) {
                    Log.i("xq", "数组越界==>" + modelList.size());
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
}
