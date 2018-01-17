package com.fanhong.cn.party.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanhong.cn.ImageLoaderPicture;
import com.fanhong.cn.R;
import com.fanhong.cn.SampleConnection;
import com.fanhong.cn.party.models.FxItemModel;
import com.fanhong.cn.view.CircleImg;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

import me.nereo.multi_image_selector.bean.Image;

/**
 * Created by Administrator on 2017/11/21.
 */

public class FxAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int NO_PICTURE = 1;
    private static final int A_PICTURE = 2;

    private List<FxItemModel> list;
    private Context context;
    private LayoutInflater inflater;

    ItemClick click;

    public void setClick(ItemClick click) {
        this.click = click;
    }

    public interface ItemClick{
        void itemclick(int id,String content,String imgurl);
    }

    public FxAdapter(Context context,List<FxItemModel> list){
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        if(list.get(position).getPicUrl().length()>0){
            return A_PICTURE;
        }else
            return NO_PICTURE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder holder = null;
        switch (viewType){
            case NO_PICTURE:
                view = inflater.inflate(R.layout.item_fx,parent,false);
                holder = (MyViewHolder1)new MyViewHolder1(view);
                break;
            case A_PICTURE:
                view = inflater.inflate(R.layout.item_fx_1,parent,false);
                holder = (MyViewHolder2)new MyViewHolder2(view);
                break;
            default:
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ImageOptions options = new ImageOptions.Builder().setLoadingDrawableId(R.mipmap.pictureloading)
                .setFailureDrawableId(R.mipmap.picturefailedloading).setUseMemCache(true).build();
        ImageOptions options1 = new ImageOptions.Builder().setLoadingDrawableId(R.mipmap.pictureloading)
                .setFailureDrawableId(R.mipmap.picturefailedloading).setCircular(true).setUseMemCache(true).build();
        FxItemModel model = list.get(position);
        switch (getItemViewType(position)){
            case NO_PICTURE:
                MyViewHolder1 myViewHolder1 = (MyViewHolder1) holder;
                final String str = model.getContent();
                if(str.length()>8){
                    myViewHolder1.title.setText(str.substring(0,8)+"...");
                }else {
                    myViewHolder1.title.setText(str);
                }
                myViewHolder1.content.setText(str);
                if(!TextUtils.isEmpty(model.getPhotoUrl())){
                    String ul = model.getPhotoUrl();
                    x.image().bind(myViewHolder1.photo,ul,options1);
                }else {
                    myViewHolder1.photo.setImageResource(R.drawable.default_photo);
                }
                myViewHolder1.author.setText(model.getAuthor());
                myViewHolder1.time.setText(model.getTime());
                final int id = model.getId();
                myViewHolder1.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        click.itemclick(id,str,"");
                    }
                });
                break;
            case A_PICTURE:
                MyViewHolder2 myViewHolder2 = (MyViewHolder2) holder;
                final String str1 = model.getContent();
                if(str1.length()>8){
                    myViewHolder2.title1.setText(str1.substring(0,8)+"...");
                }else {
                    myViewHolder2.title1.setText(str1);
                }
                myViewHolder2.content1.setText(str1);
                if(!TextUtils.isEmpty(model.getPhotoUrl())){
                    String ul = model.getPhotoUrl();
                    x.image().bind(myViewHolder2.photo1,ul,options1);
                }else {
                    myViewHolder2.photo1.setImageResource(R.drawable.default_photo);
                }
                myViewHolder2.author1.setText(model.getAuthor());
                myViewHolder2.time1.setText(model.getTime());
                x.image().bind(myViewHolder2.fxImage,model.getPicUrl(),options);
                final String imgurl = model.getPicUrl();
                final int id1 = model.getId();
                myViewHolder2.layout1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        click.itemclick(id1,str1,imgurl);
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        if(list.size()>0)
            return list.size();
        return 0;
    }


    class MyViewHolder1 extends RecyclerView.ViewHolder{
        @ViewInject(R.id.fx_layout)
        AutoRelativeLayout layout;
        @ViewInject(R.id.title)
        TextView title;
        @ViewInject(R.id.content)
        TextView content;
        @ViewInject(R.id.author_photo)
        CircleImg photo;
        @ViewInject(R.id.author)
        TextView author;
        @ViewInject(R.id.time)
        TextView time;

        public MyViewHolder1(View itemView) {
            super(itemView);
            x.view().inject(this,itemView);
            AutoUtils.autoSize(itemView);
        }
    }
    class MyViewHolder2 extends RecyclerView.ViewHolder{
        @ViewInject(R.id.fx_layout_1)
        AutoLinearLayout layout1;
        @ViewInject(R.id.title1)
        TextView title1;
        @ViewInject(R.id.content1)
        TextView content1;
        @ViewInject(R.id.author_photo1)
        CircleImg photo1;
        @ViewInject(R.id.author1)
        TextView author1;
        @ViewInject(R.id.time1)
        TextView time1;
        @ViewInject(R.id.fx_image)
        ImageView fxImage;

        public MyViewHolder2(View itemView) {
            super(itemView);
            x.view().inject(this,itemView);
            AutoUtils.autoSize(itemView);
        }
    }
}
