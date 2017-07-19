package com.fanhong.cn.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fanhong.cn.R;
import com.fanhong.cn.bean.HousekeepingRecommendBean;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.common.Callback;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by Administrator on 2017/7/19.
 */

public class HKRecyclerViewAdapter extends RecyclerView.Adapter<HKRecyclerViewAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private Context context;
    private List<HousekeepingRecommendBean> list;
    private ImageOptions options;

    public HKRecyclerViewAdapter(Context context, List<HousekeepingRecommendBean> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
        options = new ImageOptions.Builder().setLoadingDrawableId(R.drawable.img_default)
                .setFailureDrawableId(R.drawable.img_default).setUseMemCache(true).setIgnoreGif(false).build();
    }

    private OnItemCLickListener clickListener;

    private OnItemLongCLickListener longClickListener;

    public interface OnItemCLickListener {

        void onItemClick(View v, int position);
    }

    public interface OnItemLongCLickListener {
        void onItemLongClick(View v, int position);
    }

    public HKRecyclerViewAdapter setItemClickListener(OnItemCLickListener clickListener) {
        this.clickListener = clickListener;
        return this;
    }

    public HKRecyclerViewAdapter setItemLongClickListener(OnItemLongCLickListener longClickListener) {
        this.longClickListener = longClickListener;
        return this;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_hk_recommend, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        HousekeepingRecommendBean bean = list.get(position);
        holder.tv_title.setText(bean.getTitle());
        holder.tv_price.setText(bean.getPrice());
        holder.tv_count.setText(bean.getCount());
        holder.tv_reputation.setText(bean.getReputation());
        x.image().bind(holder.picture, bean.getImgUrl(), options, new Callback.CommonCallback<Drawable>() {
            @Override
            public void onSuccess(Drawable drawable) {
                int MaxHeight = holder.picture.getWidth() * 10;
                int picheight = (int) ((float) holder.picture.getWidth() / drawable.getMinimumWidth() * drawable.getMinimumHeight());
                holder.picture.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, picheight));
                holder.picture.setMaxHeight(MaxHeight);
                holder.picture.setImageDrawable(drawable);
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
        if (null != clickListener) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();
                    clickListener.onItemClick(holder.itemView, position);
                }
            });
        }
        if (null != clickListener) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = holder.getLayoutPosition();
                    longClickListener.onItemLongClick(holder.itemView, position);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @ViewInject(R.id.img_hk_recommend_picture)
        ImageView picture;
        @ViewInject(R.id.tv_hk_recommend_title)
        TextView tv_title;
        @ViewInject(R.id.tv_hk_recommend_price)
        TextView tv_price;
        @ViewInject(R.id.tv_hk_recommend_count)
        TextView tv_count;
        @ViewInject(R.id.tv_hk_recommend_reputation)
        TextView tv_reputation;

        public MyViewHolder(View itemView) {
            super(itemView);
            x.view().inject(this, itemView);
            AutoUtils.autoSize(itemView);
        }
    }
}
