package com.fanhong.cn.shippingaddress;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
    boolean controlable = false;

    public void setControlable(boolean controlable) {
        this.controlable = controlable;
    }

    public void setMyHolderClick(MyHolderClick myHolderClick) {
        this.myHolderClick = myHolderClick;
    }

    public interface MyHolderClick{
        void editAddress(String name, String phone, String cellName,String cellId,
                         String louName,String louId,String content,int status, int id);
        void deleteAddress(int id,int pos);
        void holderItemClick(String addr,String name,String phone,int addrid);
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
        final int pos = holder.getLayoutPosition();
        AddressModel addressModel = addressList.get(position);
        holder.tvName.setText(addressModel.getName());
        holder.tvPhone.setText(addressModel.getPhone());
        holder.tvAddress.setText(addressModel.getAddress());
        final String cellName = addressModel.getCellName();
        final String cellId = addressModel.getCellId();
        final String louName = addressModel.getLouName();
        final String louId = addressModel.getLouId();
        final String content = addressModel.getContent();
        final int id = addressModel.getAdrid();
        final int status = addressModel.getIsDefault();
        if(status == 1){
            holder.ivDefault.setVisibility(View.VISIBLE);
        }else {
            holder.ivDefault.setVisibility(View.GONE);
        }
        holder.tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                接编辑地址接口
                传入姓名、电话、
                小区名字、楼栋名字、填写的地址、
                小区id、楼栋id、地址、是否默认
                 */
                myHolderClick.editAddress(holder.tvName.getText().toString(),
                        holder.tvPhone.getText().toString(),
                        cellName,cellId,louName,louId,content,
                        status,id);
            }
        });
        holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                接入删除地址接口，传入地址id（后台标识）
                 */
                myHolderClick.deleteAddress(id,pos);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //item的点击
                myHolderClick.holderItemClick(holder.tvAddress.getText().toString(),
                        holder.tvName.getText().toString(),
                        holder.tvPhone.getText().toString(),
                        id);
            }
        });
        //是否可管理
        if(controlable){
            holder.tvEdit.setVisibility(View.VISIBLE);
            holder.tvDelete.setVisibility(View.VISIBLE);
        }else {
            holder.tvEdit.setVisibility(View.GONE);
            holder.tvDelete.setVisibility(View.GONE);
        }
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
