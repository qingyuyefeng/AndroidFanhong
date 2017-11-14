package com.fanhong.cn.expressage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fanhong.cn.App;
import com.fanhong.cn.R;
import com.fanhong.cn.util.JsonSyncUtils;
import com.fanhong.cn.util.MySharedPrefUtils;
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
 * Created by Administrator on 2017/9/9.
 */
@ContentView(R.layout.activity_myexpress_order)
public class ExpressOrderActivity extends Activity {
    @ViewInject(R.id.tv_title)
    private TextView title;
    @ViewInject(R.id.my_send_ex_recyc)
    private ListView sListView;
    @ViewInject(R.id.empty_order)
    private TextView emptyOrder;


    private List<MysendModel> mysendModelList = new ArrayList<>();
    private MySendexpressAdapter mySendexpressAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        title.setText("我的订单");
    }

    private void initDatas() {
        RequestParams params = new RequestParams(App.CMDURL);
        params.addBodyParameter("cmd", "87");
        params.addBodyParameter("uid", MySharedPrefUtils.getUserId(this));
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (JsonSyncUtils.getJsonValue(result, "cw").equals("0")) {
                    try {
                        JSONArray jsonArray = new JSONObject(result).optJSONArray("data");
                        if (jsonArray.length() > 0) {
                            handler.sendEmptyMessage(2);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.optJSONObject(i);
                                MysendModel mysendModel = new MysendModel();
                                mysendModel.setId(object.optInt("id",0));
                                mysendModel.setSendName(object.optString("jmz"));
                                mysendModel.setSendCity(object.optString("jsf","null"));
                                mysendModel.setReceiveName(object.optString("smz"));
                                mysendModel.setReceiveCity(object.optString("ssf","null"));
                                mysendModelList.add(mysendModel);
                            }
                            handler.sendEmptyMessage(1);
                        } else {
                            handler.sendEmptyMessage(3);
                        }
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
        mysendModelList.clear();
        initDatas();
        mySendexpressAdapter = new MySendexpressAdapter(this, mysendModelList);
        mySendexpressAdapter.setItemClick(new MySendexpressAdapter.ItemClick() {
            @Override
            public void Click(final int id, final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ExpressOrderActivity.this);
                builder.setTitle("删除此条数据！");
                builder.setMessage("确定删除此条运单吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RequestParams params = new RequestParams(App.CMDURL);
                        params.addBodyParameter("cmd","91");
                        params.addBodyParameter("id",id+"");
                        x.http().post(params, new Callback.CommonCallback<String>() {
                            @Override
                            public void onSuccess(String result) {
                                if(JsonSyncUtils.getJsonValue(result,"cw").equals("0")){
                                    Toast.makeText(ExpressOrderActivity.this,R.string.delete_success,Toast.LENGTH_SHORT).show();
                                    mysendModelList.remove(position);
                                    handler.sendEmptyMessage(1);
                                }else {
                                    Toast.makeText(ExpressOrderActivity.this,R.string.delete_failed,Toast.LENGTH_SHORT).show();
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
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        sListView.setAdapter(mySendexpressAdapter);

        //TODO 这里一直打印的长度为0
        //Log.i("xxxx", "list.size()==>" + mysendModelList.size());

    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (mySendexpressAdapter != null) {
                        mySendexpressAdapter.notifyDataSetChanged();
                    }
                    break;
                case 2:
                    sListView.setVisibility(View.VISIBLE);
                    emptyOrder.setVisibility(View.GONE);
                    break;
                case 3:
                    sListView.setVisibility(View.GONE);
                    emptyOrder.setVisibility(View.VISIBLE);
                    break;
            }
            return true;
        }
    });
}
