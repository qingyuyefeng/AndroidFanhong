package com.fanhong.cn.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fanhong.cn.R;
import com.fanhong.cn.models.Myorder;

import java.util.List;

/**
 * Created by Administrator on 2017/9/26.
 */

public class MyorderAdapter extends RecyclerView.Adapter<MyorderAdapter.ViewHolder>{
    List<Myorder> list;
    Context context;
    LayoutInflater inflater;
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.myorder_list_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
