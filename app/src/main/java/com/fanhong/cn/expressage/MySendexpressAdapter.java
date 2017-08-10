package com.fanhong.cn.expressage;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fanhong.cn.R;

import java.util.List;

/**
 * Created by Administrator on 2017/8/10.
 */

public class MySendexpressAdapter extends RecyclerView.Adapter<MySendexpressAdapter.MyViewHolder> {
    private Context context;
    private List<MysendModel> list;
    private LayoutInflater inflater;

    public MySendexpressAdapter(Context context,List<MysendModel> list){
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.sendexpress_item,null);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MysendModel mysendModel = list.get(position);
        holder.tnumber.setText(mysendModel.getTrackingNumber());
        holder.scity.setText(mysendModel.getSendCity());
        holder.sname.setText(mysendModel.getSendName());
        holder.rcity.setText(mysendModel.getReceiveCity());
        holder.rname.setText(mysendModel.getReceiveName());
        holder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context,FollowOrderActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tnumber,scity,sname,rcity,rname,follow;
        public MyViewHolder(View itemView) {
            super(itemView);
            tnumber = (TextView) itemView.findViewById(R.id.tracking_number);
            scity = (TextView) itemView.findViewById(R.id.tracking_number);
            sname = (TextView) itemView.findViewById(R.id.tracking_number);
            rcity = (TextView) itemView.findViewById(R.id.tracking_number);
            rname = (TextView) itemView.findViewById(R.id.tracking_number);
            follow = (TextView) itemView.findViewById(R.id.follow_sending);
        }
    }
}
