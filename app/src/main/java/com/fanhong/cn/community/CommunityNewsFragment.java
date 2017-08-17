package com.fanhong.cn.community;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;


import com.fanhong.cn.App;
import com.fanhong.cn.R;
import com.fanhong.cn.adapters.CommunityNewsAdapter;
import com.fanhong.cn.bean.CommunityNewsBean;
import com.fanhong.cn.util.JsonSyncUtils;
import com.zhy.autolayout.AutoRelativeLayout;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/30.
 */
@ContentView(R.layout.fragment_comim_news)
public class CommunityNewsFragment extends Fragment {
    @ViewInject(R.id.lv_community_news)
    ListView lv_community_news;
    @ViewInject(R.id.lv_nearby_news)
    ListView lv_nearby_news;
    @ViewInject(R.id.progressBar_community)
    AutoRelativeLayout bar_community;
    @ViewInject(R.id.img_news_bar)
    ImageView img_bar;
    List<CommunityNewsBean> listcomm, listnews;
    CommunityNewsAdapter adaptercomm, adapternews;
    private SharedPreferences pref;

    AnimationDrawable anim;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = x.view().inject(this, inflater, container);
        pref = getActivity().getSharedPreferences("Setting", Context.MODE_PRIVATE);
//        initNewsData(1);
//        initNewsData(2);
        lv_community_news.setOnItemClickListener(listenerComm);
        lv_nearby_news.setOnItemClickListener(listenerNearby);
//        x.image().bind(img_bar,"assets://images/progressbar.gif",new ImageOptions.Builder().setIgnoreGif(false).build());
        img_bar.setImageResource(R.drawable.anim_progressbar);
        anim = (AnimationDrawable) img_bar.getDrawable();

        return view;
    }

    AdapterView.OnItemClickListener listenerComm = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getActivity(), NewsDetailsActivity.class);
            intent.putExtra("newsId", listcomm.get(position).getNewsId());
            startActivity(intent);
        }
    };
    AdapterView.OnItemClickListener listenerNearby = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getActivity(), NewsDetailsActivity.class);
            intent.putExtra("newsId", listnews.get(position).getNewsId());
            startActivity(intent);
        }
    };

    private void initNewsData() {
        getCommunityNewsData();
        listcomm = new ArrayList<>();
        adaptercomm = new CommunityNewsAdapter(getActivity(), listcomm);
        lv_community_news.setAdapter(adaptercomm);
        listnews = new ArrayList<>();
        adapternews = new CommunityNewsAdapter(getActivity(), listnews);
        lv_nearby_news.setAdapter(adapternews);

    }

    @Override
    public void onResume() {
        initNewsData();

        anim.start();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        bar_community.setVisibility(View.VISIBLE);
    }

    private void getCommunityNewsData() {
        RequestParams params = new RequestParams(App.CMDURL);
        String xid = pref.getString("gardenId", "");
        if (xid.equals("")) return;
        params.addParameter("cmd", "47");
        params.addParameter("xid", xid);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Log.i("mLog", s);
                //判断是否成功
                if (JsonSyncUtils.getJsonValue(s, "cw").equals("0")) {
                    //添加指定类型的新闻到列表中
                    listcomm = JsonSyncUtils.addNews(listcomm, s, CommunityNewsBean.TYPE_INFORM);//通知
                    listcomm = JsonSyncUtils.addNews(listcomm, s, CommunityNewsBean.TYPE_NOTICE);//公告
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bar_community.setVisibility(View.GONE);
                            adaptercomm.notifyDataSetChanged();
                            anim.stop();
                        }
                    });
                    listnews = JsonSyncUtils.addNews(listnews, s, CommunityNewsBean.TYPE_NEWS);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bar_community.setVisibility(View.GONE);
                            adapternews.notifyDataSetChanged();
                            anim.stop();
                        }
                    });

                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
}
