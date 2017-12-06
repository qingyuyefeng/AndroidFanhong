package com.fanhong.cn.party.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.fanhong.cn.App;
import com.fanhong.cn.R;
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
 * Created by Administrator on 2017/11/29.
 */
@ContentView(R.layout.activity_party_score)
public class PartyScoreActivity extends Activity {

    @ViewInject(R.id.tv_title)
    private TextView title;
    @ViewInject(R.id.score_recyclerview)
    private RecyclerView recyclerView;

    private List<ScoreModel> list = new ArrayList<>();
    private ScoreAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        title.setText("积分排名");
    }

    @Event({R.id.img_back})
    private void onClick(View v){
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        list.clear();
        getDatas();
        adapter = new ScoreAdapter(this,list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
    }

    private void getDatas(){
        RequestParams params = new RequestParams(App.CMDURL);
        params.addBodyParameter("cmd","105");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
//                Log.i("xq", "result ==>" + result);
                if(JsonSyncUtils.getJsonValue(result,"cmd").equals("106")){
                    try {
                        JSONArray jsonArray = new JSONObject(result).getJSONArray("data");
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject object = jsonArray.getJSONObject(i);
                            ScoreModel model = new ScoreModel();
                            model.setPhoto(object.optString("logo",""));
                            model.setName(object.optString("name",""));
                            model.setPosition(object.optString("post",""));
                            model.setRanking(object.optString("rank",""));
                            model.setNumber(object.optString("fen",""));
                            list.add(model);
                        }
                        handler.sendEmptyMessage(0);
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
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            adapter.notifyDataSetChanged();
            return true;
        }
    });
}
