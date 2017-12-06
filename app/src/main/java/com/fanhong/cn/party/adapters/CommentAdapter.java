package com.fanhong.cn.party.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fanhong.cn.R;
import com.fanhong.cn.party.models.CommentModel;
import com.fanhong.cn.view.CircleImg;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by Administrator on 2017/12/4.
 */

public class CommentAdapter extends BaseAdapter {
    private Context context;
    private List<CommentModel> list;
    private LayoutInflater inflater;

    public CommentAdapter(Context context,List<CommentModel> list){
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        if(list.size()>0)
            return list.size();
        return 0;
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
        ViewHolder viewHolder = null;
        if(convertView==null){
            convertView = inflater.inflate(R.layout.item_party_detail,parent,false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ImageOptions options1 = new ImageOptions.Builder().setLoadingDrawableId(R.mipmap.pictureloading)
                .setFailureDrawableId(R.mipmap.picturefailedloading).setCircular(true).setUseMemCache(true).build();
        CommentModel model = list.get(position);
        if(TextUtils.isEmpty(model.getPhoto())){
            viewHolder.photo.setImageResource(R.drawable.default_photo);
        }else
            x.image().bind(viewHolder.photo,model.getPhoto(),options1);
        viewHolder.name.setText(model.getName());
        viewHolder.time.setText(model.getTime());
        viewHolder.content.setText(model.getContent());
        return convertView;
    }

    class ViewHolder{
        @ViewInject(R.id.photo)
        CircleImg photo;
        @ViewInject(R.id.name)
        TextView name;
        @ViewInject(R.id.time)
        TextView time;
        @ViewInject(R.id.content)
        TextView content;
        public ViewHolder(View view){
            x.view().inject(this,view);
            AutoUtils.autoSize(view);
        }
    }
}
