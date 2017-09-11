package com.fanhong.cn.expressage;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.fanhong.cn.App;
import com.fanhong.cn.R;
import com.fanhong.cn.util.JsonSyncUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/11.
 */
@ContentView(R.layout.activity_net_phone)
public class NetphoneActivity extends Activity {
    @ViewInject(R.id.net_phone_rec)
    private RecyclerView recyclerView;

    private List<NetphoneModel> list = new ArrayList<>();
    private NetphoneAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        getData();
        adapter = new NetphoneAdapter(this, list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
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
