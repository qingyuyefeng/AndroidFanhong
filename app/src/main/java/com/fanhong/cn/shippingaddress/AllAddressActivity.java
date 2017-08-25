package com.fanhong.cn.shippingaddress;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.fanhong.cn.App;
import com.fanhong.cn.R;
import com.fanhong.cn.util.JsonSyncUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/24.
 */
@ContentView(R.layout.activity_allshipping_address)
public class AllAddressActivity extends Activity {
    @ViewInject(R.id.shipping_address_recyclerview)
    private RecyclerView addressRecyclerView;
    @ViewInject(R.id.control_address)
    private CheckBox mControl;

    private List<AddressModel> addressModelList = new ArrayList<>();
    private MyAddressAdapter myAddressAdapter;

    private SharedPreferences mSettingPref;
    private String uid;
    private int status = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        mSettingPref = getSharedPreferences("Setting", Context.MODE_PRIVATE);
        uid = mSettingPref.getString("UserId", "");
//        Log.i("xq","我的用户id===>"+uid);//uid===>4
//        Toast.makeText(this,uid,Toast.LENGTH_SHORT).show();
        status = getIntent().getIntExtra("status",0);
        mControl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mControl.setText("完成");
                    myAddressAdapter.setControlable(true);
                    myAddressAdapter.notifyDataSetChanged();
                }else {
                    mControl.setText("管理");
                    myAddressAdapter.setControlable(false);
                    myAddressAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Event({R.id.back_img_b, R.id.control_address, R.id.add_new_address})
    private void onClick(View v){
        switch (v.getId()) {
            case R.id.back_img_b:
                AllAddressActivity.this.finish();
                break;
            case R.id.add_new_address:
                startActivity(new Intent(AllAddressActivity.this, AddNewAddressActivity.class));
//                startActivity(new Intent(AllAddressActivity.this, TestAmapActivity.class));
                break;
        }
    }


    private void getAddressData() {
        RequestParams params = new RequestParams(App.CMDURL);
        params.addParameter("cmd", "57");
        params.addParameter("uid", uid);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                if (JsonSyncUtils.getJsonValue(s, "cw").equals("0")) {
                    try {
                        JSONObject object = new JSONObject(s);
                        JSONArray jsonArray = object.optJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object1 = jsonArray.getJSONObject(i);
                            AddressModel addressModel = new AddressModel();
                            addressModel.setAdrid(object1.optInt("id"));
                            addressModel.setName(object1.optString("name"));
                            addressModel.setPhone(object1.optString("dh"));
                            addressModel.setIsDefault(object1.optInt("mr"));
                            addressModel.setCellId(object1.optString("xid"));
                            addressModel.setLouId(object1.optString("ldh"));
                            addressModel.setAddress(object1.optString("shdz"));
                            addressModelList.add(addressModel);
                        }
                        handler.sendEmptyMessage(0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        mControl.setChecked(false);
        //传查询所有地址的接口
        addressModelList.clear();
        getAddressData();
        myAddressAdapter = new MyAddressAdapter(this, addressModelList);
        myAddressAdapter.setMyHolderClick(new MyAddressAdapter.MyHolderClick() {
            @Override
            public void editAddress(String name, String phone, String address, int status,int id) {
                Intent intent = new Intent(AllAddressActivity.this, EditAddressActivity.class);
                intent.putExtra("adName",name);
                intent.putExtra("adPhone",phone);
                intent.putExtra("adAddress",address);
                intent.putExtra("adStatus",status);
                intent.putExtra("adrId",id);
                startActivity(intent);
            }

            @Override
            public void deleteAddress(int id, final int pos) {
                RequestParams params = new RequestParams(App.CMDURL);
                params.addBodyParameter("cmd","63");
                params.addBodyParameter("id",id+"");
                x.http().post(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if(JsonSyncUtils.getJsonValue(s,"cw").equals("0")){
                            addressModelList.remove(pos);
//                            myAddressAdapter.notifyDataSetChanged();
                            myAddressAdapter.notifyItemRemoved(pos);
                            Toast.makeText(AllAddressActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        }
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
            }

            @Override
            public void holderItemClick(String string) {
//                Toast.makeText(AllAddressActivity.this, "item的点击,", Toast.LENGTH_SHORT).show();
                if(status == 1){
                    Intent intent = new Intent();
                    intent.putExtra("address",string);
                    setResult(121,intent);
                    finish();
                }
            }
        });
        addressRecyclerView.setAdapter(myAddressAdapter);
        addressRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));//是否翻转，false
    }
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            myAddressAdapter.notifyDataSetChanged();
            return true;
        }
    });
}
