package com.fanhong.cn;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.fanhong.cn.adapters.HomenewsAdapter;
import com.fanhong.cn.models.HomeNews;

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
 * Created by Administrator on 2017/7/11.
 */

public class HomeNewsALLActivity extends Activity {
    private ImageView backBtn;
    private ListView newsListView;
    private List<HomeNews> newsList = new ArrayList<>();
    private HomenewsAdapter homenewsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allhomenews);
        backBtn = (ImageView) findViewById(R.id.allnews_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
        initView();
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                getAllnews();
            }
        }.start();
    }

    private void initView() {
        newsListView = (ListView) findViewById(R.id.all_home_news);
        homenewsAdapter = new HomenewsAdapter(this, newsList);
        newsListView.setAdapter(homenewsAdapter);
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = newsList.get(position).getNewsUrl();
                Intent intent = new Intent(HomeNewsALLActivity.this, AgentWebActivity.class);
                intent.putExtra("url", url);
                startActivityForResult(intent, 1);
            }
        });
    }

    private void setList(String str) {
        try {
            JSONObject jsonObject = new JSONObject(str);
            JSONArray jsonArray = jsonObject.optJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                JSONArray logos = object.getJSONArray("logo");
                String logo = (String) logos.get(0);
                HomeNews homeNews = new HomeNews();
                homeNews.setNewsImage(logo);
                homeNews.setNewsTitle(object.optString("bt"));
                homeNews.setNewsWhere(object.optString("zz"));
                homeNews.setNewsTime(object.optString("time"));
                homeNews.setNewsUrl(object.optString("url"));
                newsList.add(homeNews);
            }
            handler.sendEmptyMessage(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getAllnews() {
        String url = SampleConnection.url;
        OutputStream os = null;
        try {
            URL url1 = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url1.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setReadTimeout(5000);
            String content = "cmd=" + 53;
            os = httpURLConnection.getOutputStream();
            os.write(content.getBytes());
            os.flush();
            int res = httpURLConnection.getResponseCode();
            if (res == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
                StringBuffer sb = new StringBuffer();
                String s;
                while ((s = br.readLine()) != null) {
                    sb.append(s);
                }
                newsList.clear();
                setList(sb.toString());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            //未联网情况下会报空指针
//        } finally {
//            try {
//                os.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            homenewsAdapter.notifyDataSetChanged();
            return true;
        }
    });
}
