package com.fanhong.cn.expressage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fanhong.cn.R;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/8/10.
 */

public class MySendexpressAdapter extends BaseAdapter{
    private Context context;
    private List<MysendModel> list;
    private LayoutInflater inflater;

    FollowClick followClick;

    public void setFollowClick(FollowClick followClick) {
        this.followClick = followClick;
    }

    public interface FollowClick{
        void btnClick();
    }
    public MySendexpressAdapter(Context context,List<MysendModel> list){
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
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
        MyViewHolder myViewHolder;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.sendexpress_item,null);
            myViewHolder = new MyViewHolder(convertView);
            convertView.setTag(myViewHolder);
        }else {
            myViewHolder = (MyViewHolder) convertView.getTag();
        }
        MysendModel mysendModel = list.get(position);
//        holder.tnumber.setText(mysendModel.getTrackingNumber());
//        holder.scity.setText(mysendModel.getSendCity());
//        holder.sname.setText(mysendModel.getSendName());
//        holder.rcity.setText(mysendModel.getReceiveCity());
//        holder.rname.setText(mysendModel.getReceiveName());
        myViewHolder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followClick.btnClick();
            }
        });
        return convertView;
    }

    class MyViewHolder{
        TextView tnumber,scity,sname,rcity,rname,follow;
        public MyViewHolder(View itemView) {
            tnumber = (TextView) itemView.findViewById(R.id.tracking_number);
            scity = (TextView) itemView.findViewById(R.id.send_express_city);
            sname = (TextView) itemView.findViewById(R.id.send_express_name);
            rcity = (TextView) itemView.findViewById(R.id.get_express_city);
            rname = (TextView) itemView.findViewById(R.id.get_express_name);
            follow = (TextView) itemView.findViewById(R.id.follow_sending);
            AutoUtils.autoSize(itemView);
        }
    }
}
