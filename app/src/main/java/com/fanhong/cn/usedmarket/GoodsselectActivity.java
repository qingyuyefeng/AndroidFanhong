package com.fanhong.cn.usedmarket;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fanhong.cn.App;
import com.fanhong.cn.R;
import com.fanhong.cn.util.JsonSyncUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/15.
 */

public class GoodsselectActivity extends Activity {
    private ImageView selectPostgoodsBack;
    private TextView title;
    private ListView postedListview;
    private List<ShopModel> models = new ArrayList<>();
    private MyPostGoodsAdapter myPostGoodsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usedmarket_select_mypostgoods);
        init();
    }

    public void init() {
        selectPostgoodsBack = (ImageView) findViewById(R.id.img_back);
        selectPostgoodsBack.setOnClickListener(ocl);
        title = (TextView) findViewById(R.id.tv_title);
        title.setText("我的卖品");
    }

    View.OnClickListener ocl = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.img_back:
                    finish();
                    break;
            }
        }
    };

    //传入uid查询
    public void selectPostGoods() {
        RequestParams params = new RequestParams(App.CMDURL);
        params.addBodyParameter("cmd","35");
        params.addBodyParameter("uid",getSharedPreferences("Setting", Context.MODE_PRIVATE).getString("UserId", ""));
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if(JsonSyncUtils.getJsonValue(result,"cw").equals("0")) {
                    try {
                        JSONArray jsonArray = new JSONObject(result).getJSONArray("data");
                        if (jsonArray == null) {
                            Toast.makeText(GoodsselectActivity.this, "你没有上传的卖品", Toast.LENGTH_SHORT).show();
                        } else {
                            //name: tupian: ms: dh: dz: (uid)
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                                ShopModel shopModel = new ShopModel();
                                shopModel.setGoodsName(jsonObject1.optString("name"));
                                shopModel.setGoodsPicture(jsonObject1.optString("tupian"));
                                shopModel.setGoodsMessages("商品描述：" + jsonObject1.optString("ms"));
                                shopModel.setOwnerPhone("卖家电话：" + jsonObject1.optString("dh"));
                                shopModel.setOwnerName("卖家姓名：" + jsonObject1.optString("user"));
                                shopModel.setPrice(jsonObject1.optString("jg"));
                                shopModel.setId(jsonObject1.optInt("id") + "");
                                models.add(shopModel);
                            }
                            Message message = handler.obtainMessage(1);
                            handler.sendMessage(message);
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

    public void deletePostgoods(String id, final int position) {
        RequestParams params = new RequestParams(App.CMDURL);
        params.addBodyParameter("cmd", "37");
        params.addBodyParameter("id", id);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (JsonSyncUtils.getJsonValue(result, "cw").equals("0")) {
                    models.remove(position);
                    Message msg = handler.obtainMessage(2);
                    handler.sendMessage(msg);
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


    @Override
    protected void onResume() {
        super.onResume();
        models.clear();
        new Thread() {
            @Override
            public void run() {
                selectPostGoods();
            }
        }.start();
        postedListview = (ListView) findViewById(R.id.mypostgoods_listview);
        myPostGoodsAdapter = new MyPostGoodsAdapter(models, GoodsselectActivity.this);
        myPostGoodsAdapter.setPostgoodsDelete(new MyPostGoodsAdapter.PostgoodsDelete() {
            @Override
            public void deleteposted(String id, int i) {
                //实现适配器中接口的方法
                deletePostgoods(id, i);
            }
        });
        postedListview.setAdapter(myPostGoodsAdapter);
        myPostGoodsAdapter.notifyDataSetChanged();
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    myPostGoodsAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    Toast.makeText(GoodsselectActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                    myPostGoodsAdapter.notifyDataSetChanged();
                    break;
            }
            return true;
        }
    });
}
