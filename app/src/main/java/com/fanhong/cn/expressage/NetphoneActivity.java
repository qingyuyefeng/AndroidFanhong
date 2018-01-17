package com.fanhong.cn.expressage;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.fanhong.cn.App;
import com.fanhong.cn.R;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/11.
 */
@ContentView(R.layout.activity_net_phone)
public class NetphoneActivity extends Activity {
    @ViewInject(R.id.tv_title)
    private TextView title;
    @ViewInject(R.id.net_phone_rec)
    private RecyclerView recyclerView;

    private List<NetphoneModel> list = new ArrayList<>();
    private NetphoneAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        title.setText("网点电话");
        getData();
        adapter = new NetphoneAdapter(this, list);
        adapter.setCallPhone(new NetphoneAdapter.CallPhone() {
            @Override
            public void callout(final String phone) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NetphoneActivity.this);
                builder.setTitle("将要拨打" + phone);
                builder.setMessage("是否立即拨打？");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callNumber(phone);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    @Event({R.id.img_back})
    private void onClick(View v){
        finish();
    }

    private void callNumber(String phone){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:"+phone));
//        if(ActivityCompat.checkSelfPermission(NetphoneActivity.this, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
//            return;
//        }
        startActivity(intent);

    }

    private void getData() {
        RequestParams params = new RequestParams(App.CMDURL);
        params.addBodyParameter("cmd", "83");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (JsonSyncUtils.getJsonValue(result, "cw").equals("0")) {
                    try {
                        JSONArray jsonArray = new JSONObject(result).optJSONArray("kdtel");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.optJSONObject(i);
                            NetphoneModel model = new NetphoneModel();
                            model.setName(object.optString("name") + "：");
                            model.setPhone(object.optString("tel"));
                            list.add(model);
                        }
//                        Log.i("xq", "list.size()==>" + list.size());
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
