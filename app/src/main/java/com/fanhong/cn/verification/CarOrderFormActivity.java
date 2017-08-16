package com.fanhong.cn.verification;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fanhong.cn.Code;
import com.fanhong.cn.R;
import com.fanhong.cn.listviews.SpinerPopWindow;
import com.fanhong.cn.util.StringUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2017/8/9.
 */
@ContentView(R.layout.activity_verification_car_form)
public class CarOrderFormActivity extends Activity {
    @ViewInject(R.id.tv_title)
    TextView title;
    @ViewInject(R.id.edt_input_name)
    EditText edt_name;
    @ViewInject(R.id.edt_input_phoneNumber)
    EditText edt_phone;
    @ViewInject(R.id.tv_input_licence)
    TextView sb_licence;
    @ViewInject(R.id.edt_input_licence)
    EditText edt_licence;
    @ViewInject(R.id.rg_input_car_type)
    RadioGroup rg_type;
    @ViewInject(R.id.edt_input_engine)
    EditText edt_engine;
    @ViewInject(R.id.edt_input_idCard)
    EditText edt_idCard;
    @ViewInject(R.id.edt_input_getCarAddr)
    EditText edt_addr;
    @ViewInject(R.id.edt_input_code)
    EditText edt_code;
    @ViewInject(R.id.img_input_code)
    ImageView img_code;

    private String[] provinces = {"渝", "京", "津", "冀", "晋", "蒙", "辽", "吉",
            "黑", "沪", "苏", "浙", "皖", "闽", "赣", "鲁", "豫", "鄂", "湘", "粤",
            "桂", "琼", "川", "贵", "云", "藏", "陕", "甘", "青", "宁", "新", "台"};
    private CarOrderForm formModel;//表单数据
    private boolean isinput = true;
    private SpinerPopWindow sp_licences;
    private List<String> list_province = new ArrayList<>();
    private String truecode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        init();
    }

    private void init() {
        //设置标题
        title.setText("资料填写");
        //设置电话自动格式化
        edt_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isinput) {
                    isinput = false;
                    String text = s.toString().trim().replace("-", "");
                    if (text.length() > 0) {
                        if (text.charAt(0) == '1') {//以‘1’开头的说明是电话号码，否则认为是座机号码
                            text = StringUtils.addChar(3, text, '-');
                            edt_phone.setText(text);
                        } else {
                            text = StringUtils.addChar(4, text, '-');
                            edt_phone.setText(text);
                        }
                    }
                    edt_phone.setSelection(text.length());
                } else isinput = true;
            }
        });
        //设置省号下拉框
        list_province = Arrays.asList(provinces);
        sp_licences = new SpinerPopWindow<>(this, list_province, new AdapterView.OnItemClickListener()

        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sp_licences.dismiss();
                sb_licence.setText(list_province.get(position));
            }
        }, "");
        //设置限定输入格式
        String digists_licence = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        edt_licence.setKeyListener(DigitsKeyListener.getInstance(digists_licence));
        String digists_idCard = "0123456789X";
        edt_idCard.setKeyListener(DigitsKeyListener.getInstance(digists_idCard));
        String digists_code = "0123456789abcdefghigklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        edt_code.setKeyListener(DigitsKeyListener.getInstance(digists_code));
        //初始化验证码
        img_code.setImageBitmap(Code.getInstance().createBitmap());
        truecode = Code.getInstance().getCode().toLowerCase();
        //初始化表单对象
        formModel = new CarOrderForm();
        formModel.setType("小型车辆");
        //添加车型选择事件
        rg_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()

        {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb_car_mini:
                        formModel.setType("小型车辆");
                        break;
                    case R.id.rb_car_large:
                        formModel.setType("大型车辆");
                        break;
                    case R.id.rb_trailer:
                        formModel.setType("挂车");
                        break;
                }
            }
        });
    }

    //表单验证
    private boolean getValues() throws ParseException {
        String name = edt_name.getText().toString();
        if (StringUtils.isEmpty(name)) {
            Toast.makeText(this, "请输入姓名！", Toast.LENGTH_SHORT).show();
            return false;
        }
        String phone = edt_phone.getText().toString().trim().replace("-", "");
        if (!StringUtils.validPhoneNum("2", phone)) {
            Toast.makeText(this, "联系电话错误或为空！", Toast.LENGTH_SHORT).show();
            return false;
        }
        String licence0 = sb_licence.getText().toString();
        if (StringUtils.isEmpty(licence0)) {
            Toast.makeText(this, "请选择省号！", Toast.LENGTH_SHORT).show();
            return false;
        }
        String licence1 = edt_licence.getText().toString();
        if (StringUtils.isEmpty(licence1)) {
            Toast.makeText(this, "请输入车牌号！", Toast.LENGTH_SHORT).show();
            return false;
        }
        String engine = edt_engine.getText().toString().trim();
        if (StringUtils.isEmpty(engine)) {
            Toast.makeText(this, "请输入发动机号！", Toast.LENGTH_SHORT).show();
            return false;
        }
        String idCard = edt_idCard.getText().toString().trim();
        if (!StringUtils.IDCardValidate(idCard)) {
            Toast.makeText(this, "身份证号错误或为空！", Toast.LENGTH_SHORT).show();
            return false;
        }
        String addr = edt_addr.getText().toString();
        if (StringUtils.isEmpty(engine)) {
            Toast.makeText(this, "请输入取车地址！", Toast.LENGTH_SHORT).show();
            return false;
        }
        String code = edt_code.getText().toString().trim();
        if (StringUtils.isEmpty(code) || code.equals(truecode)) {
            Toast.makeText(this, getString(R.string.input_randomcode2), Toast.LENGTH_SHORT).show();
            return false;
        }
        formModel.setName(name);
        formModel.setPhone(phone);
        formModel.setLicence(licence0 + licence1);
        formModel.setEngine(engine);
        formModel.setIdCard(idCard);
        formModel.setAddress(addr);
        return true;
    }

    @Event({R.id.img_back, R.id.tv_input_licence, R.id.btn_next, R.id.img_input_code})
    private void onClicks(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.tv_input_licence:
                sp_licences.setWidth(sb_licence.getWidth());
                sp_licences.showAsDropDown(sb_licence);
                break;
            case R.id.img_input_code:
                //更新验证码
                img_code.setImageBitmap(Code.getInstance().createBitmap());
                truecode = Code.getInstance().getCode().toLowerCase();
                break;
            case R.id.btn_next:
                try {
                    if (getValues()) {
//                        Toast.makeText(this, "验证通过！", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, CarFormConfirmActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("model", formModel);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "您的输入有误，请仔细验证", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
