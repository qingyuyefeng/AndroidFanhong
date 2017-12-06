package com.fanhong.cn.party.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fanhong.cn.R;
import com.fanhong.cn.party.models.ScoreModel;
import com.fanhong.cn.view.CircleImg;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by Administrator on 2017/11/30.
 */

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MyViewHolder>{

    private Context context;
    private List<ScoreModel> list;
    private LayoutInflater inflater;

    public MembersAdapter(Context context,List<ScoreModel> list){
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_party_member,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ImageOptions options1 = new ImageOptions.Builder().setLoadingDrawableId(R.mipmap.pictureloading)
                .setFailureDrawableId(R.mipmap.picturefailedloading).setCircular(true).setUseMemCache(true).build();
        ScoreModel model = list.get(position);
        if(TextUtils.isEmpty(model.getPhoto())){
            holder.photo.setImageResource(R.drawable.default_photo);
        }else
            x.image().bind(holder.photo,model.getPhoto(),options1);
        holder.name.setText(model.getName());
        holder.position.setText(model.getPosition());
        holder.number.setText(model.getNumber());
    }

    @Override
    public int getItemCount() {
        if(list.size()>0){
            return list.size();
        }
        return 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        @ViewInject(R.id.member_photo)
        CircleImg photo;
        @ViewInject(R.id.member_name)
        TextView name;
        @ViewInject(R.id.member_position)
        TextView position;
        @ViewInject(R.id.member_number)
        TextView number;
        public MyViewHolder(View itemView) {
            super(itemView);
            x.view().inject(this,itemView);
            AutoUtils.autoSize(itemView);
        }
    }
}
