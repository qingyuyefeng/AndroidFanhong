package com.fanhong.cn.expressage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fanhong.cn.R;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/8/10.
 */

public class ExpressTypeAdapter extends RecyclerView.Adapter<ExpressTypeAdapter.MyViewHolder>{
    private Context context;
    private List<String> list;
    private LayoutInflater inflater;

    private int status;
    public ExpressTypeAdapter(Context context, List<String> list,int status){
        this.context = context;
        this.list = list;
        this.status = status;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.express_type_item,null);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.express.setText(list.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getLayoutPosition();
                Intent intent = new Intent();
                intent.putExtra("string",list.get(pos));
                if(status == 2){
                    ((Activity) context).setResult(46,intent);
                    ((Activity) context).finish();
                }else if (status == 1){
                    ((Activity) context).setResult(45,intent);
                    ((Activity) context).finish();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView express;
        public MyViewHolder(View itemView) {
            super(itemView);
            express = (TextView) itemView.findViewById(R.id.tv_express_type);
            AutoUtils.autoSize(itemView);
        }
    }
}
