package com.fanhong.cn.shippingaddress;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fanhong.cn.App;
import com.fanhong.cn.LoginActivity;
import com.fanhong.cn.R;
import com.fanhong.cn.fenxiao.FenXiaoActivity;
import com.fanhong.cn.listviews.SpinerPopWindow;
import com.fanhong.cn.util.JsonSyncUtils;
import com.fanhong.cn.util.StringUtils;

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
@ContentView(R.layout.activity_edit_address)
public class EditAddressActivity extends Activity{
    @ViewInject(R.id.change_name_edt)
    private EditText changName;
    @ViewInject(R.id.change_phone_edt)
    private EditText changPhone;
    @ViewInject(R.id.change_address_choosecell)
    private TextView changecell;
    @ViewInject(R.id.change_address_chooselou)
    private TextView changelou;
    @ViewInject(R.id.change_address_edt)
    private EditText changAddress;
    @ViewInject(R.id.whether_default)
    private CheckBox ifDefault;

    private SharedPreferences mSharedPref;
    private SpinerPopWindow<String> ssp;
    private List<String> cellIds = new ArrayList<>(), cellNames = new ArrayList<>();

    private int checked = -1;
    private int addId = 0;
    private String cellId,louId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        mSharedPref = getSharedPreferences("Setting", Context.MODE_PRIVATE);
        Intent intent = getIntent();
        changName.setText(intent.getStringExtra("adName"));
        changPhone.setText(intent.getStringExtra("adPhone"));
        addId = intent.getIntExtra("adrId",0);
        Log.i("xq","默认地址选中状态==>"+intent.getIntExtra("adStatus",0));
        if(intent.getIntExtra("adStatus",0) == 1){
            ifDefault.setChecked(true);
        }else {
            ifDefault.setChecked(false);
        }

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
    private void getCells(){
        RequestParams params = new RequestParams(App.CMDURL);
        params.addParameter("cmd","29");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                if(JsonSyncUtils.getJsonValue(s,"cw").equals("0")){
                    String data = JsonSyncUtils.getJsonValue(s,"data");
                    cellNames = JsonSyncUtils.getStringList(data,"name");
                    cellIds = JsonSyncUtils.getStringList(data,"id");

                    ssp = new SpinerPopWindow<String>(EditAddressActivity.this, cellNames, new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            changecell.setText(cellNames.get(position));
                            cellId = cellIds.get(position);
                            setEnableds(true,false);
                            ssp.dismiss();
                            changelou.setText(R.string.chooselou);
                            getLous();
                        }
                    },"");
                    ssp.setWidth(changecell.getWidth());
                    ssp.showAsDropDown(changecell,0,0);
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
    @Event({R.id.back_img,R.id.save_changed_address,R.id.change_address_choosecell,R.id.change_address_chooselou})
    private void onClick(View v){
        switch (v.getId()){
            case R.id.back_img:
                createDialog();
                break;
            case R.id.save_changed_address:
                if(TextUtils.isEmpty(changName.getText().toString()) ||
                        TextUtils.isEmpty(changPhone.getText().toString()) ||
                        TextUtils.isEmpty(changAddress.getText().toString())){
                    Toast.makeText(EditAddressActivity.this,"资料填写不完整",Toast.LENGTH_SHORT).show();
                }else if(!StringUtils.validPhoneNum("2",changPhone.getText().toString())){
                    Toast.makeText(EditAddressActivity.this,"请输入正确的电话号码",Toast.LENGTH_SHORT).show();
                }else {
                    saveChangeAddress();
                }
                break;
            case R.id.change_address_choosecell:
                getCells();
                break;
            case R.id.change_address_chooselou:
                ssp = new SpinerPopWindow<>(EditAddressActivity.this, louNames,
                        new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                changelou.setText(louNames.get(position));
                                louId = louIds.get(position);
                                setEnableds(true,true);
                                ssp.dismiss();
                            }
                        }, "");
                ssp.setWidth(changelou.getWidth());
                ssp.showAsDropDown(changelou,0,0);
                break;
        }
    }

    private void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("更改尚未保存");
        builder.setMessage("是否放弃修改？");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditAddressActivity.this.finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
//        builder.setCancelable(true);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void saveChangeAddress(){
        RequestParams params = new RequestParams(App.CMDURL);
        params.addBodyParameter("cmd","61");
        params.addBodyParameter("uid",mSharedPref.getString("UserId",""));
        params.addBodyParameter("id",addId+"");
        params.addBodyParameter("xid",cellId);
        params.addBodyParameter("ldh",louId);
        params.addParameter("mr",checked);
        params.addParameter("dizhi",changAddress.getText().toString());
        params.addParameter("dh",changPhone.getText().toString());
        params.addParameter("name",changName.getText().toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                String cw = JsonSyncUtils.getJsonValue(s,"cw");
                if(cw.equals("0")){
                    Toast.makeText(EditAddressActivity.this,"收货地址修改成功",Toast.LENGTH_SHORT).show();
                    EditAddressActivity.this.finish();
                }else {
                    Toast.makeText(EditAddressActivity.this,"修改失败，请重试",Toast.LENGTH_SHORT).show();
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
    private void setEnableds(boolean louClick,boolean editAdress){
        changelou.setEnabled(louClick);
        changAddress.setEnabled(editAdress);
    }
}
