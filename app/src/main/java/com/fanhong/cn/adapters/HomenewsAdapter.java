package com.fanhong.cn.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanhong.cn.R;
import com.fanhong.cn.models.HomeNews;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/5.
 */

public class HomenewsAdapter extends BaseAdapter{

    private Context context;
    private List<HomeNews> newsList = new ArrayList<>();
    private LayoutInflater inflater;

    public HomenewsAdapter(Context context,List<HomeNews> newsList){
        this.context = context;
        this.newsList = newsList;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return newsList.size();
    }

    @Override
    public Object getItem(int position) {
        return newsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.homenews_item,null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        HomeNews homeNews = newsList.get(position);
        ImageOptions options = new ImageOptions.Builder().setLoadingDrawableId(R.mipmap.pictureloading)
                .setFailureDrawableId(R.mipmap.picturefailedloading).setUseMemCache(true).build();
        x.image().bind(viewHolder.newsPic,homeNews.getNewsImage(),options);
        viewHolder.newsTitle.setText(homeNews.getNewsTitle());
        viewHolder.newsPlace.setText(homeNews.getNewsWhere());
        viewHolder.newsTime.setText(homeNews.getNewsTime());
        return convertView;
    }

    private class ViewHolder{
        ImageView newsPic;
        TextView newsTitle,newsPlace,newsTime;
        ViewHolder(View view){
            newsPic = (ImageView) view.findViewById(R.id.news_picture);
            newsTitle = (TextView) view.findViewById(R.id.news_title);
            newsPlace = (TextView) view.findViewById(R.id.news_place);
            newsTime = (TextView) view.findViewById(R.id.news_time);
        }
    }
}
