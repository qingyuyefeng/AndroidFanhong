package com.fanhong.cn.repair;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.fanhong.cn.App;
import com.fanhong.cn.GardenSelecterActivity;
import com.fanhong.cn.R;
import com.fanhong.cn.models.DoorcheckModel;
import com.fanhong.cn.util.JsonSyncUtils;

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
public class EmergencyUnlockActivity extends Activity {
    @ViewInject(R.id.tv_title)
    private TextView title;
    @ViewInject(R.id.show_location)
    private TextView location;
    @ViewInject(R.id.swip_refresh)
    private SwipeRefreshLayout swipRefresh;
    @ViewInject(R.id.unlock_recyclerview)
    private RecyclerView unlockRecyclerView;

    private List<OpenDoorBean> modelList = new ArrayList<>();
    private DoorRecycAdapter adapter;
    private int firstItemPosition, lastItemPosition;
    private String gardenName, gardenId;
    String tel;

    //    private LinearLayoutManager mLayoutManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        title.setText(R.string.service_lock);
        gardenName = getIntent().getStringExtra("gardenName");
        gardenId = getIntent().getStringExtra("gardenId");
        location.setText(gardenName);

        getData();
//        modelList.add(new OpenDoorBean("刘师傅", "136xxxx5273", "500", "20", "assets://images/kaisuoshifu.png"));
        adapter = new DoorRecycAdapter(this, modelList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        adapter.setClickListener(new DoorRecycAdapter.OnItemCLickListener() {
            @Override
            public void onItemClick(View v, int position) {
                tel = modelList.get(position).getPhone();
                Intent i = new Intent(Intent.ACTION_CALL);
                //判断Android版本是否大于23
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int checkCallPhonePermission = ContextCompat.checkSelfPermission(EmergencyUnlockActivity.this, Manifest.permission.CALL_PHONE);

                    if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(EmergencyUnlockActivity.this, new String[]{Manifest.permission.CALL_PHONE},
                                11);
                        return;
                    }
                }
                Uri uri = Uri.parse("tel:" + tel);
                i.setData(uri);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });
        unlockRecyclerView.setLayoutManager(mLayoutManager);
        unlockRecyclerView.setAdapter(adapter);
        unlockRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                    firstItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    lastItemPosition = linearLayoutManager.findLastVisibleItemPosition();
                    //上拉
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && lastItemPosition + 1 == adapter.getItemCount()) {
                        refreshList();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });
        //下拉
        swipRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 11)
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent i = new Intent(Intent.ACTION_CALL);
                Uri uri = Uri.parse("tel:" + tel);
                i.setData(uri);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(i);
            }
    }

    @Event(value = {R.id.img_back, R.id.choose_cell}, type = View.OnClickListener.class)
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.choose_cell:
                startActivityForResult(new Intent(this, GardenSelecterActivity.class), 11);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 51 && null != data) {
            gardenName = data.getStringExtra("gardenName");
            gardenId = data.getStringExtra("gardenId");
            location.setText(gardenName);
        }
    }

    private void refreshList() {
        //do refresh here
        modelList.clear();
        getData();
    }

    private void getData() {
        RequestParams params = new RequestParams(App.CMDURL);
        params.addParameter("cmd", "");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (JsonSyncUtils.getJsonValue(result, "cw").equals("0")) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
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
                swipRefresh.setRefreshing(false);
            }
        });
    }
}
