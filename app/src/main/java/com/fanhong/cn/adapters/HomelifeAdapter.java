package com.fanhong.cn.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanhong.cn.R;
import com.fanhong.cn.models.Homelife;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/10.
 */

public class HomelifeAdapter extends BaseAdapter {
    private Context context;
    private List<Homelife> homelifeList = new ArrayList<>();
    private LayoutInflater inflater;
    private final int TYPE1 = 0; //加载三张图片的布局
    private final int TYPE2 = 1; //加载一张图片的布局

    public HomelifeAdapter(Context context, List<Homelife> homelifeList) {
        this.context = context;
        this.homelifeList = homelifeList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return homelifeList.size();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
        return homelifeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (homelifeList.get(position).getType() == 0) {
            return TYPE1;
        } else {
            return TYPE2;
        }
    }

    ImageOptions options = new ImageOptions.Builder().setLoadingDrawableId(R.mipmap.pictureloading)
            .setFailureDrawableId(R.mipmap.picturefailedloading).setUseMemCache(true).build();

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder1 viewHolder1 = null;
        ViewHolder2 viewHolder2 = null;
        int type = getItemViewType(position);
        if (convertView == null) {
            switch (type) {
                case TYPE1:
                    convertView = inflater.inflate(R.layout.item_home_news_life_, null);
                    viewHolder1 = new ViewHolder1(convertView);
                    convertView.setTag(viewHolder1);
                    break;
                case TYPE2:
                    convertView = inflater.inflate(R.layout.item_home_news_life, null);
                    viewHolder2 = new ViewHolder2(convertView);
                    convertView.setTag(viewHolder2);
                    break;
            }
        } else {
            switch (type) {
                case TYPE1:
                    viewHolder1 = (ViewHolder1) convertView.getTag();
                    break;
                case TYPE2:
                    viewHolder2 = (ViewHolder2) convertView.getTag();
                    break;
            }
        }
        switch (type) {
            case TYPE1:
                Homelife homelife = homelifeList.get(position);
//                try {
                x.image().bind(viewHolder1.img1, homelife.getStrings().get(0), options);
                x.image().bind(viewHolder1.img2, homelife.getStrings().get(1), options);
                x.image().bind(viewHolder1.img3, homelife.getStrings().get(2), options);
//                }catch (IndexOutOfBoundsException e){
//                    Log.i("xq","数组越界==>"+homelife.getStrings().size());
//                }
                viewHolder1.title.setText(homelife.getTitle());
                viewHolder1.place.setText(homelife.getPlace());
                viewHolder1.time.setText(homelife.getTime());
                break;
            case TYPE2:
                x.image().bind(viewHolder2.img, homelifeList.get(position).getStrings().get(0), options);
                viewHolder2.title.setText(homelifeList.get(position).getTitle());
                viewHolder2.place.setText(homelifeList.get(position).getPlace());
                viewHolder2.time.setText(homelifeList.get(position).getTime());
                break;
        }
        return convertView;
    }

    private class ViewHolder1 {
        ImageView img1, img2, img3;
        TextView title, place, time;

        ViewHolder1(View view) {
            img1 = (ImageView) view.findViewById(R.id.img_news_picture1);
            img2 = (ImageView) view.findViewById(R.id.img_news_picture2);
            img3 = (ImageView) view.findViewById(R.id.img_news_picture3);
            title = (TextView) view.findViewById(R.id.tv_news_title);
            place = (TextView) view.findViewById(R.id.tv_news_author);
            time = (TextView) view.findViewById(R.id.tv_news_time);
            AutoUtils.autoSize(view);
        }
    }

    private class ViewHolder2 {
        ImageView img;
        TextView title, place, time;

        ViewHolder2(View view) {
            img = (ImageView) view.findViewById(R.id.img_news_picture);
            title = (TextView) view.findViewById(R.id.tv_news_title1);
            place = (TextView) view.findViewById(R.id.tv_news_author1);
            time = (TextView) view.findViewById(R.id.tv_news_time1);
            AutoUtils.autoSize(view);
        }
    }
}
