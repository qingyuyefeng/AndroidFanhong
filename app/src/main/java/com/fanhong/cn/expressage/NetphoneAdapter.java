package com.fanhong.cn.expressage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fanhong.cn.R;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/9/11.
 */

public class NetphoneAdapter extends RecyclerView.Adapter<NetphoneAdapter.ViewHolder> {
    private Context context;
    private List<NetphoneModel> list;
    private LayoutInflater inflater;

    public NetphoneAdapter(Context context, List<NetphoneModel> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.telphone_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NetphoneModel model = list.get(position);
        holder.name.setText(model.getName());
        holder.phone.setText(model.getPhone());
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, phone;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.net_name);
            phone = (TextView) itemView.findViewById(R.id.net_phone);
            AutoUtils.autoSize(itemView);
        }
    }
}
