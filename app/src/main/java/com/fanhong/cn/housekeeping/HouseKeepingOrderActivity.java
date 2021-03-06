package com.fanhong.cn.housekeeping;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.fanhong.cn.R;
import com.fanhong.cn.listviews.SpinerPopWindow;
import com.fanhong.cn.shippingaddress.AllAddressActivity;
import com.fanhong.cn.util.StringUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by Administrator on 2017/7/24.
 */
@ContentView(R.layout.activity_hk_order)
public class HouseKeepingOrderActivity extends Activity {
    @ViewInject(R.id.tv_addr_default)
    TextView tv_addr_default;
    @ViewInject(R.id.img_hk_order_details)
    ImageView img_details;
    @ViewInject(R.id.tv_hk_order_details_title)
    TextView tv_details_title;
    @ViewInject(R.id.tv_hk_order_details_time)
    TextView tv_details_time;
    @ViewInject(R.id.edt_hk_order_service_phone)
    EditText edt_phone;
    @ViewInject(R.id.checkbox_alipay)
    CheckBox cbox_alipay;
    @ViewInject(R.id.checkbox_weichatpay)
    CheckBox cbox_weichat;
    @ViewInject(R.id.tv_hk_order_money)
    TextView tv_order_money;
    @ViewInject(R.id.sb_hk_order_year)
    CheckBox sb_hk_order_year;
    @ViewInject(R.id.sb_hk_order_month)
    CheckBox sb_hk_order_month;
    @ViewInject(R.id.sb_hk_order_day)
    CheckBox sb_hk_order_day;

    private String service_title, service_price, phoneNum;//商品名称、预设价格、电话号码
    private boolean isinput = true;//拦截非用户输入的更改，防止死锁
    private int year, month, day, year0, month0, day0, payby = -1;//value用于保存选择日期，value0保存起始日期（明天），payby记录支付方式
    private static List<Integer> years, months, days;
    private SpinerPopWindow<Integer> popYears, popMonths, popDays;

    private final static int ALIPAY = 0, WEICHATPAY = 1, NONE = -1;

    static {
        years = new ArrayList<>();
        for (int i = 0; i < 50; i++)
            years.add(2017 + i);
        months = new ArrayList<>();
        days = new ArrayList<>();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        service_title = getIntent().getStringExtra("title");
        service_price = getIntent().getStringExtra("price");
        initViews();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode){
            case 121:
                String str = data.getStringExtra("address");
                tv_addr_default.setText(str);
                break;
        }
    }

    private void setdayscount(int year, int month, int type) {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);//当月最大天数
        if (year <= year0) {
            if (type == 1) {
                months.clear();
                for (int i = 0; i < 12 - month0 + 1; i++)
                    months.add(month0 + i);
                if (month < month0) {
                    month = month0;
                    sb_hk_order_month.setText(month + "月");
                }
                popMonths.notifyDataChanged();
            }
            if (month <= month0) {
                this.month = month0;
                days.clear();
                for (int i = 0; i < maxDate - day0 + 1; i++)
                    days.add(i + day0);
                if (day < day0) {
                    day = day0;
                    sb_hk_order_day.setText(day + "日");
                }
                popDays.notifyDataChanged();
            } else {
                days.clear();
                for (int i = 0; i < maxDate; i++)
                    days.add(i + 1);
                popDays.notifyDataChanged();
            }
            return;
        }
        months.clear();
        for (int i = 0; i < 12; i++)
            months.add(i + 1);
        popMonths.notifyDataChanged();
        days.clear();
        for (int i = 0; i < maxDate; i++)
            days.add(i + 1);
        popDays.notifyDataChanged();
    }

    private void initViews() {
        //为电话号码的输入框添加自动格式化
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
                    String str = StringUtils.addChar(3, edt_phone.getText().toString(), '-');
                    edt_phone.setText(str);
                    edt_phone.setSelection(str.length());
                } else isinput = true;
            }
        });
        //获取当前日期并显示到三个选项框
        Calendar c = Calendar.getInstance();
        c.add(c.DATE, 1);//
        year = year0 = c.get(Calendar.YEAR);
        month = month0 = c.get(Calendar.MONTH) + 1;
        day = day0 = c.get(Calendar.DAY_OF_MONTH);
        sb_hk_order_year.setText(year0 + "年");
        sb_hk_order_month.setText(month0 + "月");
        sb_hk_order_day.setText(day0 + "日");
        //初始化三个选项框的下拉列表（popUpWindow实现）
        popYears = new SpinerPopWindow<>(this, years, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                popYears.dismiss();
                year = years.get(position);
                setdayscount(year, month, 1);
                sb_hk_order_year.setText(year + "年");
            }
        }, "年");
        popYears.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                sb_hk_order_year.setChecked(false);
            }
        });
        popMonths = new SpinerPopWindow<>(this, months, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                popMonths.dismiss();
                month = months.get(position);
                setdayscount(year, month, 0);
                sb_hk_order_month.setText(month + "月");
            }
        }, "月");
        popMonths.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                sb_hk_order_month.setChecked(false);
            }
        });
        popDays = new SpinerPopWindow<>(this, days, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                popDays.dismiss();
                day = days.get(position);
                sb_hk_order_day.setText(day + "日");
            }
        }, "日");
        popDays.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                sb_hk_order_day.setChecked(false);
            }
        });
        setdayscount(year, month, 1);
    }


    @Event({R.id.tv_back, R.id.btn_hk_order_addr_default, R.id.btn_hk_order_pay_now, R.id.sb_hk_order_year,
            R.id.sb_hk_order_month, R.id.sb_hk_order_day, R.id.label_alipay, R.id.label_weichatpay})
    private void onClicks(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.btn_hk_order_addr_default://默认地址
                Intent intent = new Intent(this, AllAddressActivity.class);
                intent.putExtra("status",1);
                startActivityForResult(intent,0);
                break;
            case R.id.sb_hk_order_year://日期下拉框
                if (sb_hk_order_year.isChecked()) {
                    popYears.setWidth(sb_hk_order_year.getWidth());
                    popYears.showAsDropDown(sb_hk_order_year);
                }
                break;
            case R.id.sb_hk_order_month:
                if (sb_hk_order_month.isChecked()) {
                    popMonths.setWidth(sb_hk_order_month.getWidth());
                    popMonths.showAsDropDown(sb_hk_order_month);
                }
                break;
            case R.id.sb_hk_order_day:
                if (sb_hk_order_day.isChecked()) {
                    popDays.setWidth(sb_hk_order_day.getWidth());
                    popDays.showAsDropDown(sb_hk_order_day);
                }
                break;
            case R.id.btn_hk_order_pay_now://立即支付
                phoneNum = edt_phone.getText().toString().trim().replace("-", "");
                if (!StringUtils.isEmpty(phoneNum)) {
                    if (isPhoneNumber(phoneNum)) {
                        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("确认支付").setMessage("请确认您预约的时间")
                                .setPositiveButton("去支付", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        doPay();
                                    }
                                }).setNegativeButton("再看看", null).create();
                        if (payby == ALIPAY)
                            dialog.setIcon(R.drawable.zfb);
                        else if (payby == WEICHATPAY)
                            dialog.setIcon(R.drawable.wx);
                        dialog.show();
                    } else {
                        Toast.makeText(this, "请输入正确的联系电话", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "请输入您的联系电话", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.label_alipay:
                if (cbox_alipay.isChecked())
                    cbox_alipay.setChecked(false);
                else
                    cbox_alipay.setChecked(true);
                break;
            case R.id.label_weichatpay:
                if (cbox_weichat.isChecked())
                    cbox_weichat.setChecked(false);
                else
                    cbox_weichat.setChecked(true);
                break;
        }
    }

    @Event(value = {R.id.checkbox_alipay, R.id.checkbox_weichatpay}, type = CompoundButton.OnCheckedChangeListener.class)
    private void onchecks(CompoundButton view, boolean isChecked) {
        switch (view.getId()) {
            case R.id.checkbox_alipay:
                if (isChecked) {
                    payby = ALIPAY;
                    cbox_weichat.setChecked(false);
                } else {
                    payby = NONE;
                }
                break;
            case R.id.checkbox_weichatpay:
                if (isChecked) {
                    payby = WEICHATPAY;
                    cbox_alipay.setChecked(false);
                } else {
                    payby = NONE;
                }
                break;
        }
//        Toast.makeText(this, "payby=" + payby, 500).show();
    }

    private boolean isPhoneNumber(String phoneNum) {
        return StringUtils.validPhoneNum("2", phoneNum);
    }

    private void doPay() {
        switch (payby) {
            case ALIPAY:
                //pay by aliPay , if result is ok ,skip to order details
            {
                Intent intent = new Intent(this, HouseKeepingOrderDetailsActivity.class);
                intent.putExtra("title", service_title);
                intent.putExtra("price", service_price);
                startActivity(intent);
            }
            break;
            case WEICHATPAY:
                //pay by weiChat , if result is ok ,skip to order details
            {
                Intent intent = new Intent(this, HouseKeepingOrderDetailsActivity.class);
                intent.putExtra("title", service_title);
                intent.putExtra("price", service_price);
                startActivity(intent);
            }
            break;
            default:
                Toast.makeText(this, "请选择支付方式", Toast.LENGTH_SHORT).show();
        }
    }
}
