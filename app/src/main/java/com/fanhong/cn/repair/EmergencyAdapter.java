package com.fanhong.cn.repair;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Administrator on 2017/9/7.
 */

public class EmergencyAdapter extends RecyclerView.Adapter<EmergencyAdapter.ItemViewHolder>{

    private List<EmergencyModel> modelList;
    private Context context;
    private LayoutInflater inflater;

    public EmergencyAdapter(List<EmergencyModel> modelList,Context context){
        this.modelList = modelList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{

        public ItemViewHolder(View itemView) {
            super(itemView);
        }
    }
}
