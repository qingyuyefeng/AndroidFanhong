package com.fanhong.cn.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fanhong.cn.App;
import com.fanhong.cn.R;
import com.fanhong.cn.applydoors.AddGuardActivity;
import com.fanhong.cn.applydoors.CellListAdapter;
import com.fanhong.cn.models.Cellmodel;
import com.fanhong.cn.models.Keymodel;
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
 * Created by Administrator on 2017/12/20.
 */
@ContentView(R.layout.fragment_accesscontrol1)
public class AccesscontrolView2 extends BaseFragment {
    @ViewInject(R.id.cell_list)
    private ListView listView;
    @ViewInject(R.id.no_key_txt)
    private TextView noControl;

    public List<Cellmodel> cellList = new ArrayList<>();
    private List<Keymodel> keyList = new ArrayList<>();
    private CellListAdapter cellListAdapter;
    private ImageView imageView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);
    }

    @Event({R.id.btn_reflush, R.id.btn_add})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_reflush:
                AccesscontrolView2.this.onResume();
                break;
            case R.id.btn_add:
                Intent intent2 = new Intent(getActivity(), AddGuardActivity.class);
                startActivity(intent2);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        cellList.clear();
        keyList.clear();
        getBaseActivity().getAccessControl();
        cellListAdapter = new CellListAdapter(getActivity(), cellList);
        cellListAdapter.setItemClick(new CellListAdapter.ItemClick() {
            @Override
            public void open(String key, ImageView view) {
                imageView = view;
                handler.sendEmptyMessage(10);
                openDoor(key);
            }

            @Override
            public void none() {
                handler.sendEmptyMessage(14);
            }
        });
        listView.setAdapter(cellListAdapter);
    }

    public void getKeylist(String s) {
        try {
            JSONArray cellArray = new JSONObject(s).getJSONArray("data");
            if (cellArray.length() > 0) {
                handler.sendEmptyMessage(112);
                for (int i = 0; i < cellArray.length(); i++) {
                    JSONObject object = cellArray.getJSONObject(i);
                    Cellmodel cellmodel = new Cellmodel();
                    cellmodel.setCellName(object.getString("xq"));
                    cellmodel.setDetails(object.getString("content"));
                    cellList.add(cellmodel);
                }
                handler.sendEmptyMessage(111);
            } else {
                handler.sendEmptyMessage(113);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 111:
                    cellListAdapter.notifyDataSetChanged();
                    break;
                case 112:
                    listView.setVisibility(View.VISIBLE);
                    noControl.setVisibility(View.GONE);
                    break;
                case 113:
                    listView.setVisibility(View.GONE);
                    noControl.setVisibility(View.VISIBLE);
                    break;
                case 10:
                    Toast.makeText(getActivity(), "正在开门...", Toast.LENGTH_LONG).show();
                    imageView.setEnabled(false);
                    break;
                case 11:
                    Toast.makeText(getActivity(), "开门成功！", Toast.LENGTH_SHORT).show();
                    break;
                case 12:
                    Toast.makeText(getActivity(), "开门失败，请重试！", Toast.LENGTH_SHORT).show();
                    break;
                case 13:
                    imageView.setEnabled(true);
                    break;
                case 14:
                    Toast.makeText(getActivity(), "钥匙审核中，请等待审核!", Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    //访问接口开门
    public synchronized void openDoor(String str) {
        RequestParams params = new RequestParams(App.OPEN_URL);
        params.addParameter("key", str);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (JsonSyncUtils.getJsonValue(result, "error").equals("succ")) {
                    String s = JsonSyncUtils.getJsonValue(result, "data");
                    final String uuid = JsonSyncUtils.getJsonValue(s, "cmd_uuid");
                    Log.i("xq", s + "..." + uuid);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            checkOpen(uuid);
                        }
                    }, 3000);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                handler.sendEmptyMessage(12);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void checkOpen(String uuid) {
        RequestParams params = new RequestParams(App.CHECK_URL);
        params.addBodyParameter("cmd_uuid", uuid);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("xq", "reslut==>" + result);
                if (JsonSyncUtils.getJsonValue(result, "result").equals("0")) {
                    handler.sendEmptyMessage(11);
                } else {
//                    网络延时超过3秒的则不提示了
//                    handler.sendEmptyMessage(12);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                handler.sendEmptyMessage(12);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                handler.sendEmptyMessage(13);
            }
        });
    }

}
