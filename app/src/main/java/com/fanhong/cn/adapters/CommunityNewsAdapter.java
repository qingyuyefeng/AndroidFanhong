package com.fanhong.cn.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanhong.cn.R;
import com.fanhong.cn.bean.CommunityNewsBean;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by Administrator on 2017/6/30.
 */

public class CommunityNewsAdapter extends BaseAdapter {

    private Context context;
    private List<CommunityNewsBean> list;
    private LayoutInflater mInflater;
    private ImageOptions options;
    public ViewHolder holder;

    public CommunityNewsAdapter(Context context, List<CommunityNewsBean> list) {
        this.context = context;
        this.list = list;
        this.mInflater = LayoutInflater.from(context);
        options = new ImageOptions.Builder().setLoadingDrawableId(R.drawable.ilon_yh).setFailureDrawableId(R.drawable.ilon_yh).setUseMemCache(true).build();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_news, null);
            holder = new ViewHolder();
            x.view().inject(holder, convertView);
            AutoUtils.autoSize(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CommunityNewsBean bean = list.get(position);
        x.image().bind(holder.news_photo, bean.getNews_photo(), options);
        holder.news_flag.setVisibility(View.INVISIBLE);
        if (bean.getNews_flag().equals("通知")) {
            holder.news_flag.setImageResource(R.drawable.ilon_inform);
            holder.news_flag.setVisibility(View.VISIBLE);
        } else if (bean.getNews_flag().equals("公告")) {
            holder.news_flag.setImageResource(R.drawable.ilon_notice);
            holder.news_flag.setVisibility(View.VISIBLE);
        }
        holder.tv_news_title.setText(bean.getTv_news_title());
        holder.tv_news_from.setText(bean.getTv_news_from());
        holder.tv_news_time.setText(bean.getTv_news_time());
        return convertView;
    }

    class ViewHolder {
        @ViewInject(R.id.img_news_photo)
        public ImageView news_photo;
        @ViewInject(R.id.news_flag)
        public ImageView news_flag;
        @ViewInject(R.id.tv_news_title)
        public TextView tv_news_title;
        @ViewInject(R.id.tv_news_from)
        public TextView tv_news_from;
        @ViewInject(R.id.tv_news_time)
        public TextView tv_news_time;
    }
}
