package com.fanhong.cn.usedmarket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanhong.cn.R;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/17.
 */

public class MyPostGoodsAdapter extends BaseAdapter{
    private List<ShopModel> modelList = new ArrayList<>();
    Context context;
    private LayoutInflater inflater;
    public MyPostGoodsAdapter(List<ShopModel> modelList,Context context){
        this.modelList = modelList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return modelList.size();
    }

    @Override
    public Object getItem(int i) {
        return modelList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private Button delete;
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null){
            view = inflater.inflate(R.layout.usedmarket_select_mypostgoods_item,null);
            delete = (Button) view.findViewById(R.id.delete_mypostgoods);
            viewHolder = new ViewHolder();
            viewHolder.goodsName = (TextView)view.findViewById(R.id.goodsname);
            viewHolder.goodsPicture = (ImageView) view.findViewById(R.id.goodspicture);
            viewHolder.goodsMessages = (TextView)view.findViewById(R.id.goodsmessage);
            viewHolder.ownerPhone = (TextView)view.findViewById(R.id.ownerphone);
            viewHolder.ownerName = (TextView)view.findViewById(R.id.ownername);
            viewHolder.goodsprice = (TextView) view.findViewById(R.id.goodsprice);
            viewHolder.id = (TextView) view.findViewById(R.id.goodsid);
            view.setTag(viewHolder);
            //删除(传入id)
            final ViewHolder finalviewHolder = viewHolder;
            final int position = i;
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String s = finalviewHolder.id.getText().toString();
                    postgoodsDelete.deleteposted(s,position);
                }
            });
        }
        viewHolder = (ViewHolder) view.getTag();
        ShopModel shopModel = modelList.get(i);
        viewHolder.goodsName.setText(shopModel.getGoodsName());
//        LoadImage.Load(viewHolder.goodsPicture,shopModel.getGoodsPicture(),context);
        ImageOptions options = new ImageOptions.Builder().setLoadingDrawableId(R.mipmap.pictureloading)
                .setFailureDrawableId(R.mipmap.picturefailedloading).setUseMemCache(true).build();
        x.image().bind(viewHolder.goodsPicture,shopModel.getGoodsPicture(),options);
        viewHolder.goodsMessages.setText(shopModel.getGoodsMessages());
        viewHolder.ownerPhone.setText(shopModel.getOwnerPhone());
        viewHolder.ownerName.setText(shopModel.getOwnerName());
        viewHolder.goodsprice.setText(shopModel.getPrice());
        viewHolder.id.setText(shopModel.getId());
        return view;
    }
    private class ViewHolder{
        private TextView goodsName;
        private ImageView goodsPicture;
        private TextView goodsMessages;
        private TextView ownerPhone;
        private TextView ownerName;
        private TextView goodsprice;
        private TextView id;
    }
    PostgoodsDelete postgoodsDelete;
    //自定义一接口
    public interface PostgoodsDelete{
        void deleteposted(String string,int position);
    }
    //设定一个set方法
    public void setPostgoodsDelete(PostgoodsDelete postgoodsDelete) {
        this.postgoodsDelete = postgoodsDelete;
    }
}
