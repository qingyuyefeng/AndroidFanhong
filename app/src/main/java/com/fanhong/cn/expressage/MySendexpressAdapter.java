package com.fanhong.cn.expressage;

import android.content.Context;
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
        myViewHolder.scity.setText(mysendModel.getSendCity());
        myViewHolder.sname.setText(mysendModel.getSendName());
        myViewHolder.rcity.setText(mysendModel.getReceiveCity());
        myViewHolder.rname.setText(mysendModel.getReceiveName());
        return convertView;
    }

    class MyViewHolder{
        TextView scity,sname,rcity,rname;
        public MyViewHolder(View itemView) {
            scity = (TextView) itemView.findViewById(R.id.send_express_city);
            sname = (TextView) itemView.findViewById(R.id.send_express_name);
            rcity = (TextView) itemView.findViewById(R.id.get_express_city);
            rname = (TextView) itemView.findViewById(R.id.get_express_name);
            AutoUtils.autoSize(itemView);
        }
    }
}
