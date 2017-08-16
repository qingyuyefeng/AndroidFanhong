package com.fanhong.cn.shippingaddress;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanhong.cn.LoginActivity;
import com.fanhong.cn.R;
import com.fanhong.cn.fenxiao.FenXiaoActivity;
import com.fanhong.cn.listviews.SpinerPopWindow;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

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

    int checked = -1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);


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
    @Event({R.id.back_img,R.id.save_changed_address,R.id.change_address_choosecell,R.id.change_address_chooselou})
    private void onClick(View v){
        switch (v.getId()){
            case R.id.back_img:
                createDialog();
                break;
            case R.id.save_changed_address:
                break;
            case R.id.change_address_choosecell:
                break;
            case R.id.change_address_chooselou:
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

    private void setEnableds(boolean louClick,boolean editAdress,boolean canChecked){
        changelou.setEnabled(louClick);
        changAddress.setEnabled(editAdress);
        ifDefault.setEnabled(canChecked);
    }
}
