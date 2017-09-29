package com.fanhong.cn.shippingaddress;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fanhong.cn.App;
import com.fanhong.cn.R;
import com.fanhong.cn.listviews.SpinerPopWindow;
import com.fanhong.cn.util.JsonSyncUtils;
import com.fanhong.cn.util.StringUtils;
import com.fanhong.cn.util.TopBarUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/24.
 */
@ContentView(R.layout.activity_add_address)
public class AddNewAddressActivity extends Activity{
    @ViewInject(R.id.back_imgbtn)
    private ImageView backBtn;
    @ViewInject(R.id.save_new_address)
    private TextView saveBtn;
    @ViewInject(R.id.input_name_edt)
    private EditText inputName;
    @ViewInject(R.id.input_phone_edt)
    private EditText inputPhone;
    @ViewInject(R.id.address_choosecell)
    private TextView chooseCell;
    @ViewInject(R.id.address_chooselou)
    private TextView chooseLou;
    @ViewInject(R.id.input_address_edt)
    private EditText inputAddress;
    @ViewInject(R.id.whether_set_default)
    private CheckBox ifDefault;

    int checked = -1;
    private SharedPreferences mSettingPref;
    private SpinerPopWindow<String> ssp;
    private List<String> ids = new ArrayList<>(), names = new ArrayList<>();

    private String uid,cellId,louId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

//        TopBarUtil.initStatusBar(this);

        mSettingPref = getSharedPreferences("Setting", Context.MODE_PRIVATE);
        uid = mSettingPref.getString("UserId", "");

        backBtn.setOnClickListener(ocl);
        saveBtn.setOnClickListener(ocl);
        chooseCell.setOnClickListener(ocl);
        chooseLou.setOnClickListener(ocl);
        inputAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!TextUtils.isEmpty(s)){
                    setEnableds(true,true,true);
                }else {
                    setEnableds(true,true,false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ifDefault.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    checked = 1;
                }else {
                    checked = 0;
                }
            }
        });
    }
    private void setEnableds(boolean louClick,boolean editAdress,boolean canChecked){
        chooseLou.setEnabled(louClick);
        inputAddress.setEnabled(editAdress);
        ifDefault.setEnabled(canChecked);
    }
    View.OnClickListener ocl = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.back_imgbtn:
                    AddNewAddressActivity.this.finish();
                    break;
                case R.id.save_new_address:
                    //传添加地址的接口
                    /*
                        3个edittext的getText
                        checkBox的选中状态
                        选中为1，没选中为0
                    */
                    if(TextUtils.isEmpty(inputName.getText().toString()) ||
                            TextUtils.isEmpty(inputPhone.getText().toString()) ||
                            TextUtils.isEmpty(inputAddress.getText().toString())){
                        Toast.makeText(AddNewAddressActivity.this,"资料填写不完整",Toast.LENGTH_SHORT).show();
                    }else if(!StringUtils.validPhoneNum("2",inputPhone.getText().toString())){
                        Toast.makeText(AddNewAddressActivity.this,"请输入正确的电话号码",Toast.LENGTH_SHORT).show();
                    }else {
                        addNewAddress();
                    }
                    break;
                case R.id.address_choosecell:
                    getCells();
                    break;
                case R.id.address_chooselou:
                    ssp = new SpinerPopWindow<>(AddNewAddressActivity.this, louNames,
                            new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            chooseLou.setText(louNames.get(position));
                            louId = louIds.get(position);
                            setEnableds(true,true,false);
                            ssp.dismiss();
                        }
                    }, "");
                    ssp.setWidth(chooseLou.getWidth());
                    ssp.showAsDropDown(chooseLou,0,0);
                    break;
            }
        }
    };
    private void addNewAddress(){
        RequestParams params = new RequestParams(App.CMDURL);
        params.addParameter("cmd","59");
        params.addParameter("uid",uid);
        params.addParameter("mr",checked);
        params.addParameter("xid",cellId);
        params.addParameter("ldh",louId);
        params.addParameter("dizhi",inputAddress.getText().toString());
        params.addParameter("dh",inputPhone.getText().toString());
        params.addParameter("name",inputName.getText().toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                String cw = JsonSyncUtils.getJsonValue(s,"cw");
                if(cw.equals("0")){
                    Toast.makeText(AddNewAddressActivity.this,"收货地址添加成功",Toast.LENGTH_SHORT).show();
                    AddNewAddressActivity.this.finish();
                }else {
                    Toast.makeText(AddNewAddressActivity.this,"添加失败，请重试",Toast.LENGTH_SHORT).show();
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
    private void getCells(){
        RequestParams params = new RequestParams(App.CMDURL);
        params.addParameter("cmd","29");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                if(JsonSyncUtils.getJsonValue(s,"cw").equals("0")){
                    String data = JsonSyncUtils.getJsonValue(s,"data");
                    names = JsonSyncUtils.getStringList(data,"name");
                    ids = JsonSyncUtils.getStringList(data,"id");

                    ssp = new SpinerPopWindow<String>(AddNewAddressActivity.this, names, new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            chooseCell.setText(names.get(position));
                            cellId = ids.get(position);
                            setEnableds(true,false,false);
                            ssp.dismiss();
                            chooseLou.setText(R.string.chooselou);
                            getLous();
                        }
                    },"");
                    ssp.setWidth(chooseCell.getWidth());
                    ssp.showAsDropDown(chooseCell,0,0);
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
    private List<String> louIds = new ArrayList<>(),louNames = new ArrayList<>();
    private void getLous(){
        louIds.clear();
        louNames.clear();
        RequestParams params = new RequestParams(App.CMDURL);
        params.addParameter("cmd","45");
        params.addParameter("xid",cellId);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                if(JsonSyncUtils.getJsonValue(s,"cw").equals("0")){
                    String str = JsonSyncUtils.getJsonValue(s,"data");
                    String[] strings = str.split(",");
                    for(int i=0;i<strings.length;i++){
                        String[] strings1 = strings[i].split("<");
                        louNames.add(strings1[0]);
                        louIds.add(strings1[1]);
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
}
