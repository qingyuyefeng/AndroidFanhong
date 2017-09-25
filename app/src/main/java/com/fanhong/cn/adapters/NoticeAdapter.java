package com.fanhong.cn.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fanhong.cn.R;
import com.fanhong.cn.models.NoticeModel;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/9/19.
 */

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder> {
    public List<NoticeModel> list;
    Context context;
    LayoutInflater inflater;



    LayoutClick layoutClick;

    public interface LayoutClick{
        void click(int pos);
    }

    public void setLayoutClick(LayoutClick layoutClick) {
        this.layoutClick = layoutClick;
    }


    public NoticeAdapter(List<NoticeModel> list,Context context){
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.notice_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        holder.title.setText(list.get(position).getTitle());
//        holder.content.setText(list.get(position).getContent());
        final int pos = holder.getLayoutPosition();
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutClick.click(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(list!=null){
            return list.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        AutoLinearLayout layout;
        TextView title,content;
        public ViewHolder(View itemView) {
            super(itemView);
            layout = (AutoLinearLayout) itemView.findViewById(R.id.notice_layout);
            title = (TextView) itemView.findViewById(R.id.notice_title);
            content = (TextView) itemView.findViewById(R.id.notice_content);
            AutoUtils.autoSize(itemView);
        }
    }
}
