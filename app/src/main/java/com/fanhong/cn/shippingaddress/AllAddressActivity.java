package com.fanhong.cn.shippingaddress;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.fanhong.cn.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/24.
 */
@ContentView(R.layout.activity_allshipping_address)
public class AllAddressActivity extends Activity{
    @ViewInject(R.id.back_img_b)
    private ImageView backBtn;
    @ViewInject(R.id.add_new_address)
    private ImageView addNewAddress;
    @ViewInject(R.id.shipping_address_recyclerview)
    private RecyclerView addressRecyclerView;

    private List<AddressModel> addressModelList = new ArrayList<>();
    private MyAddressAdapter myAddressAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        backBtn.setOnClickListener(onClickListener);
        addNewAddress.setOnClickListener(onClickListener);
    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.back_img_b:
                    AllAddressActivity.this.finish();
                    break;
                case R.id.add_new_address:
                    startActivity(new Intent(AllAddressActivity.this,AddNewAddressActivity.class));
                    break;
            }
        }
    };

    public List<AddressModel> getAddressData(){
        for (int i=0;i<3;i++){
            AddressModel addressModel = new AddressModel();
            addressModelList.add(addressModel);
        }
        return addressModelList;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //传查询所有地址的接口
        addressModelList.clear();
        myAddressAdapter = new MyAddressAdapter(this,getAddressData());
        myAddressAdapter.setMyHolderClick(new MyAddressAdapter.MyHolderClick() {
            @Override
            public void editAddress(String name, String phone, String address, int status) {
                Intent intent = new Intent(AllAddressActivity.this,EditAddressActivity.class);
                startActivity(intent);
            }

            @Override
            public void deleteAddress(int id) {
                Toast.makeText(AllAddressActivity.this,"删不了假数据滴",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void holderItemClick() {
                Toast.makeText(AllAddressActivity.this,"item的点击,",Toast.LENGTH_SHORT).show();

            }
        });
        addressRecyclerView.setAdapter(myAddressAdapter);
        addressRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,false));//是否翻转，false
    }
}
