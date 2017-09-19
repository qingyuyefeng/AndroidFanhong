package com.fanhong.cn.expressage;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fanhong.cn.R;
import com.fanhong.cn.util.JsonSyncUtils;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

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
 * Created by Administrator on 2017/8/9.
 */

@ContentView(R.layout.activity_check_express)
public class CheckExpressActivity extends Activity {
    @ViewInject(R.id.tv_title)
    private TextView title;
    @ViewInject(R.id.input_express_number)
    private EditText edtExpressNumber;
    @ViewInject(R.id.wuliu_layout)
    private AutoLinearLayout wuliuLayout;
    @ViewInject(R.id.show_wuliu)
    private TextView content;
    @ViewInject(R.id.my_send_ex_recyc)
    private ListView listView;

    String host = "http://jisukdcx.market.alicloudapi.com";
    String path = "/express/query";
    String appcode = "62b004f79e4e48579d154afc59aadb5b";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        title.setText("查快递");
        listView.setVisibility(View.GONE);
    }

    @Event({R.id.img_back, R.id.img_search_express})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.img_search_express:
                checkexpressage();
                break;
        }
    }

    private void checkexpressage() {
        RequestParams params = new RequestParams(host + path);
        params.addHeader("Authorization", "APPCODE " + appcode);
        params.addBodyParameter("number", edtExpressNumber.getText().toString());
        params.addBodyParameter("type", "AUTO");    //自动识别快递类型
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("CheckExpress", result);
                String status = JsonSyncUtils.getJsonValue(result, "status");
                if (status.equals("0")) {
                    content.setText(JsonSyncUtils.getJsonValue(result, "result"));
                } else {
                    content.setText(result);
                }
                ruleText(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                content.setText(JsonSyncUtils.getJsonValue(ex.toString(), "result"));
            }

            @Override
            public void onCancelled(CancelledException cex) {
                content.setText(JsonSyncUtils.getJsonValue(cex.toString(), "result"));
            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void ruleText(String json) {
        String result = JsonSyncUtils.getJsonValue(json, "result");
        String number = JsonSyncUtils.getJsonValue(result, "number");
        String type = JsonSyncUtils.getJsonValue(result, "type");
        content.setText("订单号：" + number + "\n" + "快递公司：" + type + "\n" + "物流信息：");
        List<ListOrder> list = getJsonList(result);
        Log.i("CheckExpress", "list.size==>" + list.size());
        for (int i=0;i<list.size();i++){
            TextView tv = new TextView(this);
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            tv.setText(list.get(i).time+"\n"+list.get(i).detail);
            wuliuLayout.addView(tv);
        }
    }

    private List<ListOrder> getJsonList(String json) {
        List<ListOrder> l = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(json);
            JSONArray arr = obj.getJSONArray("list");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj1 = arr.getJSONObject(i);
                l.add(new ListOrder(obj1.getString("time"),
                        obj1.getString("status")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return l;
    }

    class ListOrder {
        String time;
        String detail;

        public ListOrder(String time, String detail) {
            this.time = time;
            this.detail = detail;
        }
    }
}
