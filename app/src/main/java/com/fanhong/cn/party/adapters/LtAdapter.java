package com.fanhong.cn.party.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanhong.cn.R;
import com.fanhong.cn.party.models.LtItemModel;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/8.
 */

public class LtAdapter extends RecyclerView.Adapter<LtAdapter.MyViewHolder> {

    private List<LtItemModel> list = new ArrayList<>();
    private Context context;
    private LayoutInflater inflater;

    public interface LtInterface{
        void details(int position);
        void reply(int position);
    }
    LtInterface ltInterface;

    public void setLtInterface(LtInterface ltInterface) {
        this.ltInterface = ltInterface;
    }

    public LtAdapter(Context context, List<LtItemModel> list){
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.lt_item,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ImageOptions options1 = new ImageOptions.Builder().setLoadingDrawableId(R.mipmap.pictureloading)
                .setFailureDrawableId(R.mipmap.picturefailedloading).setCircular(true).setUseMemCache(true).build();
        LtItemModel model = list.get(position);
        if(TextUtils.isEmpty(model.getPhoto())){
            holder.photo.setImageResource(R.drawable.default_photo);
        }else
            x.image().bind(holder.photo,model.getPhoto(),options1);
        holder.name.setText(model.getName());
        holder.content.setText(model.getContent());
    }

    @Override
    public int getItemCount() {
        if(list.size()>0){
            return list.size();
        }
        return 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        @ViewInject(R.id.lt_photo)
        ImageView photo;
        @ViewInject(R.id.lt_name)
        TextView name;
        @ViewInject(R.id.lt_content)
        TextView content;


        public MyViewHolder(View itemView) {
            super(itemView);
            x.view().inject(this,itemView);
            AutoUtils.autoSize(itemView);
        }
    }
}
