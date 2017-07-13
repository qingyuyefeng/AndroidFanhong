package com.fanhong.cn.usedmarket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanhong.cn.R;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/15.
 */

public class ShopAdapter extends BaseAdapter {
    private List<ShopModel> shopModelList = new ArrayList<>();
    private Context context;
    private LayoutInflater inflater;

    Callseller callseller;

    public void setCallseller(Callseller callseller) {
        this.callseller = callseller;
    }

    public interface Callseller{
        void onCall(String str);
    }
    public ShopAdapter(List<ShopModel> shopModelList,Context context){
        this.shopModelList = shopModelList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return shopModelList.size();
    }

    @Override
    public Object getItem(int i) {
        return shopModelList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(view == null){
            view = inflater.inflate(R.layout.usedmarketitem,null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
        ShopModel shopModel = shopModelList.get(i);
        viewHolder.goodsName.setText(shopModel.getGoodsName());
        viewHolder.goodsPrice.setText(shopModel.getPrice());
//        LoadImage.Load(viewHolder.goodsPicture,shopModel.getGoodsPicture(),context);
        ImageOptions options = new ImageOptions.Builder().setLoadingDrawableId(R.mipmap.pictureloading)
                .setFailureDrawableId(R.mipmap.picturefailedloading).setUseMemCache(true).build();
        x.image().bind(viewHolder.goodsPicture,shopModel.getGoodsPicture(),options);
        viewHolder.goodsMessages.setText(shopModel.getGoodsMessages());
        viewHolder.ownerPhone.setText(shopModel.getOwnerPhone());
        viewHolder.ownerName.setText(shopModel.getOwnerName());
        viewHolder.goodsPrice.setText(shopModel.getPrice());
        final String str = shopModel.getOwnerPhone();
        viewHolder.callSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callseller.onCall(str);
            }
        });
        return view;
    }

    private class ViewHolder{
        TextView goodsName,goodsMessages,ownerPhone,ownerName,callSeller,goodsPrice;
        ImageView goodsPicture;
        ViewHolder(View view){
            goodsName = (TextView)view.findViewById(R.id.goodsname);
            goodsPicture = (ImageView) view.findViewById(R.id.goodspicture);
            goodsMessages = (TextView)view.findViewById(R.id.goodsmessage);
            goodsPrice = (TextView) view.findViewById(R.id.goodsprice);
            ownerPhone = (TextView)view.findViewById(R.id.ownerphone);
            ownerName = (TextView)view.findViewById(R.id.ownername);
            callSeller = (TextView) view.findViewById(R.id.call_seller);
        }
    }
}
