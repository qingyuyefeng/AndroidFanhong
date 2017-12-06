package com.fanhong.cn.party.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.fanhong.cn.App;
import com.fanhong.cn.R;
import com.fanhong.cn.party.adapters.MembersAdapter;
import com.fanhong.cn.party.adapters.ScoreAdapter;
import com.fanhong.cn.party.models.ScoreModel;
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
 * Created by Administrator on 2017/11/30.
 */
@ContentView(R.layout.activity_party_member)
public class PartyMembersActivity extends Activity {
    @ViewInject(R.id.tv_title)
    private TextView title;
    @ViewInject(R.id.member_recyclerview)
    private RecyclerView recyclerView;

    private List<ScoreModel> list = new ArrayList<>();
    private MembersAdapter adapter;

    private int type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        initViews();
    }

    private void initViews() {
        Intent intent = getIntent();
        title.setText(intent.getStringExtra("title"));
        type = intent.getIntExtra("type", -1);
//        Log.i("xq", "member type==>" + type);
    }

    private void getData(int tt) {
        RequestParams params = new RequestParams(App.CMDURL);
        params.addBodyParameter("cmd", "107");
        params.addBodyParameter("number", tt + "");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
//                Log.i("xq", "result ==>" + result);
                if (JsonSyncUtils.getJsonValue(result, "cmd").equals("108")) {
                    try {
                        JSONArray jsonArray = new JSONObject(result).getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            ScoreModel model = new ScoreModel();
                            model.setPhoto(object.optString("logo", ""));
                            model.setName(object.optString("name", ""));
                            model.setPosition(object.optString("post", ""));
                            model.setNumber(object.optString("tel", ""));
                            list.add(model);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
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

    @Event(R.id.img_back)
    private void onClick(View v) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        list.clear();
        getData(type);
        adapter = new MembersAdapter(this, list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }
}
