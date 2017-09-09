package com.fanhong.cn.repair;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanhong.cn.R;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by Administrator on 2017/9/8.
 */

class DoorRecycAdapter extends RecyclerView.Adapter<DoorRecycAdapter.MyViewHolder> {
    private LayoutInflater inflater;
    private Context context;
    private List<OpenDoorBean> list;
    private ImageOptions options;

    private OnItemCLickListener clickListener;

    public DoorRecycAdapter(Context context, List<OpenDoorBean> list) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.list = list;
        this.options = new ImageOptions.Builder().setLoadingDrawableId(R.drawable.img_default)
                .setFailureDrawableId(R.drawable.img_default).setUseMemCache(true).build();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_repair_opendoor, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        //TODO bindViews
        holder.tvName.setText(list.get(position).getName());
        holder.tvPhone.setText(list.get(position).getPhone());
        holder.tvFar.setText(list.get(position).getFar() + "m");
        holder.tvExp.setText("已开锁" + list.get(position).getExp());
        x.image().bind(holder.imgLogo, list.get(position).getLogo(), options);
        if (null != clickListener) {
            holder.btnCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();
                    clickListener.onItemClick(holder.itemView, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemCLickListener {
        void onItemClick(View v, int position);
    }

    public void setClickListener(OnItemCLickListener clickListener) {
        this.clickListener = clickListener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @ViewInject(R.id.img_logo)
        ImageView imgLogo;
        @ViewInject(R.id.tv_name)
        TextView tvName;
        @ViewInject(R.id.tv_phone)
        TextView tvPhone;
        @ViewInject(R.id.tv_far)
        TextView tvFar;
        @ViewInject(R.id.tv_exp)
        TextView tvExp;
        @ViewInject(R.id.btn_call)
        Button btnCall;

        public MyViewHolder(View itemView) {
            super(itemView);
            x.view().inject(this, itemView);
            AutoUtils.autoSize(itemView);
        }
    }
}
