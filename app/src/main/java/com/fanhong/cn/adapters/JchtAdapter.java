package com.fanhong.cn.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanhong.cn.R;
import com.fanhong.cn.models.JchtModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/6.
 */

public class JchtAdapter extends BaseAdapter{
    private List<JchtModel> jchtModels = new ArrayList<>();
    private Context context;
    private LayoutInflater inflater;

    public JchtAdapter(Context context,List<JchtModel> jchtModels){
        this.context = context;
        this.jchtModels = jchtModels;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return jchtModels.size();
    }

    @Override
    public Object getItem(int position) {
        return jchtModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            convertView = inflater.inflate(R.layout.jcht_item,null);
            viewHolder = new ViewHolder();
            viewHolder.jchtImage1 = (ImageView) convertView.findViewById(R.id.jcht_image1);
            viewHolder.jchtImage2 = (ImageView) convertView.findViewById(R.id.jcht_image2);
            viewHolder.jchtText1 = (TextView) convertView.findViewById(R.id.jcht_text1);
            viewHolder.jchtText2 = (TextView) convertView.findViewById(R.id.jcht_text2);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();
        /*
         *这里设置数据库获取来的数据
         */
        return convertView;
    }
    private class ViewHolder{
        ImageView jchtImage1,jchtImage2;
        TextView jchtText1,jchtText2;
    }
}
