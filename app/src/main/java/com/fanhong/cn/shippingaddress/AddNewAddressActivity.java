package com.fanhong.cn.shippingaddress;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanhong.cn.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

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
    @ViewInject(R.id.input_address_edt)
    private EditText inputAddress;
    @ViewInject(R.id.whether_set_default)
    private CheckBox ifDefault;

    int checked = -1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        backBtn.setOnClickListener(ocl);
        saveBtn.setOnClickListener(ocl);
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
    View.OnClickListener ocl = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.back_imgbtn:
                    AddNewAddressActivity.this.finish();
                    break;
                case R.id.save_changed_address:
                    //传添加地址的接口
                    /*
                        3个edittext的getText
                        checkBox的选中状态
                        选中为1，没选中为0
                    */
                    break;
            }
        }
    };
}
