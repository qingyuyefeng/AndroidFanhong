package com.fanhong.cn.shippingaddress;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanhong.cn.R;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by Administrator on 2017/7/25.
 */

public class MyAddressAdapter extends RecyclerView.Adapter<MyAddressAdapter.MyViewHolder> {
    private Context context;
    private List<AddressModel> addressList;
    private LayoutInflater inflater;

    MyHolderClick myHolderClick;

    public void setMyHolderClick(MyHolderClick myHolderClick) {
        this.myHolderClick = myHolderClick;
    }

    public interface MyHolderClick{
        void editAddress(String name,String phone,String address,int status);
        void deleteAddress(int id);
        void holderItemClick();
    }

    public MyAddressAdapter(Context context,List<AddressModel> addressList){
        this.context = context;
        this.addressList = addressList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.shipping_address_item,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        AddressModel addressModel = addressList.get(position);
//        holder.tvName.setText(addressModel.getName());
//        holder.tvPhone.setText(addressModel.getPhone());
//        holder.tvAddress.setText(addressModel.getAddress());
        final int id = addressModel.getAdrid();
        final int status = addressModel.getIsDefault();
//        if(status == 1){
//            holder.ivDefault.setVisibility(View.VISIBLE);
//        }else {
//            holder.ivDefault.setVisibility(View.GONE);
//        }
        holder.tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                接编辑地址接口
                传入姓名、电话、地址、是否默认
                 */
                myHolderClick.editAddress(holder.tvName.getText().toString(),
                        holder.tvPhone.getText().toString(),
                        holder.tvAddress.getText().toString(),
                        status);
            }
        });
        holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                接入删除地址接口，传入地址id（后台标识）
                 */
                myHolderClick.deleteAddress(id);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myHolderClick.holderItemClick();
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        @ViewInject(R.id.whose_address)
        TextView tvName;
        @ViewInject(R.id.phone_of_address)
        TextView tvPhone;
        @ViewInject(R.id.detail_of_address)
        TextView tvAddress;
        @ViewInject(R.id.edit_address)
        TextView tvEdit;
        @ViewInject(R.id.delete_address)
        TextView tvDelete;
        @ViewInject(R.id.iv_default)
        ImageView ivDefault;

        public MyViewHolder(View itemView) {
            super(itemView);
            x.view().inject(this,itemView);
            AutoUtils.autoSize(itemView);
        }
    }
}