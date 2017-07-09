package com.fanhong.cn.usedmarket;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.fanhong.cn.App;
import com.fanhong.cn.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class GoodsselectActivity extends Activity{
    private ImageView selectPostgoodsBack;
    private ListView postedListview;
    private List<ShopModel> models = new ArrayList<>();
    private MyPostGoodsAdapter myPostGoodsAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usedmarket_select_mypostgoods);
        init();
    }
    public void init(){
        selectPostgoodsBack = (ImageView) findViewById(R.id.select_postgoods_back_btn);
        selectPostgoodsBack.setOnClickListener(ocl);
        postedListview = (ListView) findViewById(R.id.mypostgoods_listview);
//        getData();
        myPostGoodsAdapter = new MyPostGoodsAdapter(models,GoodsselectActivity.this);

        myPostGoodsAdapter.setPostgoodsDelete(new MyPostGoodsAdapter.PostgoodsDelete() {
            @Override
            public void deleteposted(String s,int i) {
                //实现适配器中接口的方法
                final String id = s;
                final int position = i;
                new Thread(){
                    @Override
                    public void run() {
                        deletePostgoods(id,position);
                    }
                }.start();

            }
        });
        postedListview.setAdapter(myPostGoodsAdapter);
        //加载数据
        new Thread(){
            @Override
            public void run() {
                selectPostGoods();
            }
        }.start();
    }
    View.OnClickListener ocl = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.select_postgoods_back_btn:
                    finish();
                    break;
            }
        }
    };

    //传入uid查询
    public void selectPostGoods(){
        String str = App.CMDURL;
        OutputStream os = null;
        try {
            URL url = new URL(str);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
//        SharedPreferences preferences = getSharedPreferences("Setting", Context.MODE_PRIVATE);
//        String uid = preferences.getString("UserId","");
//        Log.i("userid",uid);
            http.setConnectTimeout(5000);
            http.setReadTimeout(5000);
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setDoInput(true);
            String content = "cmd=35"+"&uid="+ getSharedPreferences("Setting", Context.MODE_PRIVATE).getString("UserId","");
            os = http.getOutputStream();
            os.write(content.getBytes());
            if(http.getResponseCode() == 200){
                BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream(),"utf-8"));
                StringBuffer sb = new StringBuffer();
                String ss;
                while((ss = br.readLine())!=null){
                    sb.append(ss);
                }
                JSONObject jsonObject = new JSONObject(sb.toString());
                JSONArray jsonArray = jsonObject.optJSONArray("data");
                if(jsonArray == null){
                    Message msg = handler.obtainMessage();
                    msg.what = 0;
                    handler.sendMessage(msg);
                }else {
                    //name: tupian: ms: dh: dz: (uid)
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                        ShopModel shopModel = new ShopModel();
                        shopModel.setGoodsName("商品名："+jsonObject1.optString("name"));
                        shopModel.setGoodsPicture(jsonObject1.optString("tupian"));
                        shopModel.setGoodsMessages("商品描述："+jsonObject1.optString("ms"));
                        shopModel.setOwnerPhone("卖家电话："+jsonObject1.optString("dh"));
                        shopModel.setOwnerName("卖家姓名："+jsonObject1.optString("user"));
                        shopModel.setId(jsonObject1.optInt("id")+"");
                        models.add(shopModel);
                    }
                    Message message = handler.obtainMessage();
                    message.what = 1;
                    handler.sendMessage(message);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void deletePostgoods(String id,int position){
        String str = App.CMDURL;
        OutputStream os = null;
        try {
            URL url = new URL(str);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            String content = "cmd="+37+"&id="+id;
            os = httpURLConnection.getOutputStream();
            os.write(content.getBytes());
            if(httpURLConnection.getResponseCode() == 200){
                models.remove(position);
                Message msg = handler.obtainMessage();
                msg.what = 2;
                handler.sendMessage(msg);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        models.clear();
//        new Thread(){
//            @Override
//            public void run() {
//                selectPostGoods();
//            }
//        }.start();
        postedListview = (ListView) findViewById(R.id.mypostgoods_listview);
        myPostGoodsAdapter = new MyPostGoodsAdapter(models,GoodsselectActivity.this);
        myPostGoodsAdapter.setPostgoodsDelete(new MyPostGoodsAdapter.PostgoodsDelete() {
            @Override
            public void deleteposted(String s,int i) {
                //实现适配器中接口的方法
                final String id = s;
                final int position = i;
                new Thread(){
                    @Override
                    public void run() {
                        deletePostgoods(id,position);
                    }
                }.start();

            }
        });
        postedListview.setAdapter(myPostGoodsAdapter);
        myPostGoodsAdapter.notifyDataSetChanged();
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case 0:
                    Toast.makeText(GoodsselectActivity.this,"你没有上传的卖品",Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    myPostGoodsAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    Toast.makeText(GoodsselectActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                    myPostGoodsAdapter.notifyDataSetChanged();
                    break;
            }
            return true;
        }
    });
}
