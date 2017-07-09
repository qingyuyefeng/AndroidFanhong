package com.fanhong.cn.view;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


import com.fanhong.cn.R;
import com.fanhong.cn.adapters.CommunityNewsAdapter;
import com.fanhong.cn.bean.CommunityNewsBean;

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
    List<CommunityNewsBean> list;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = x.view().inject(this, inflater, container);
        initNewsData(1);
        initNewsData(2);
        return view;
    }

    private void initNewsData(int i) {
        switch (i) {
            case 1:
                list = new ArrayList<>();
                CommunityNewsBean bean1 = new CommunityNewsBean();
                bean1.setNews_photo("http://pic.baike.soso.com/p/20130604/bki-20130604215110-1851060998.jpg");
                bean1.setNews_flag("通知");
                bean1.setTv_news_title("因城市道路整改，本校区将于今天早上六点至下午九点停止供水，请...");
                bean1.setTv_news_from("天安数码城");
                bean1.setTv_news_time("2017年6月19日 17:00");
                list.add(bean1);
                for (i = 0; i < 1; i++) {
                    CommunityNewsBean bean2 = new CommunityNewsBean("http://pic.baike.soso.com/p/20130604/bki-20130604215110-1851060998.jpg", "公告", "根据重庆交通部的报告，平安夜、圣诞节及元旦节当日进行交通管制，届时...", "大渡口区交警大队", "2017年6月19日 17:01");
                    list.add(bean2);
                }
                CommunityNewsAdapter adapter = new CommunityNewsAdapter(getActivity(), list);
                lv_community_news.setAdapter(adapter);
                break;
            case 2:
                list = new ArrayList<>();
                CommunityNewsBean bean3 = new CommunityNewsBean("http://pic.baike.soso.com/p/20130604/bki-20130604215110-1851060998.jpg", " ", "老人雨中摔倒 民警蹲着为他撑伞近20分钟", "重庆商报", "2017年6月19日 08:51");
                CommunityNewsBean bean4 = new CommunityNewsBean("http://pic.baike.soso.com/p/20130604/bki-20130604215110-1851060998.jpg", " ", "重庆建智慧游大数据分析平台 可科学预测旅游市场", "重庆商报", "2017年6月20日 09:52");
                CommunityNewsBean bean5 = new CommunityNewsBean("http://pic.baike.soso.com/p/20130604/bki-20130604215110-1851060998.jpg", " ", "孙政才：牢记嘱托 不辱使命 在新起点上努力开创发展新局面", "重庆商报", "2017年6月19日 16:25");
                list.add(bean3);
//                list.add(bean4);
//                list.add(bean5);
                CommunityNewsAdapter adapter2 = new CommunityNewsAdapter(getActivity(), list);
                lv_nearby_news.setAdapter(adapter2);
                break;
        }
    }
}
