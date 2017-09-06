package com.fanhong.cn;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.fanhong.cn.database.Cartdb;
import com.fanhong.cn.listviews.ShopCartListView;
import com.fanhong.cn.view.CountMoney;
import com.fanhong.cn.view.PayMoney;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import static com.fanhong.cn.R.id.lv_list;
import static com.fanhong.cn.R.id.tv_totalmoney;

@ContentView(R.layout.activity_shoppingcart)
public class ShoppingCartActivity extends Activity implements PayMoney, CountMoney {
    @ViewInject(lv_list)
    private ShopCartListView listView;
    @ViewInject(tv_totalmoney)
    private TextView tvPriceCount;

    private float fl_total = 0.0f;

    private Cartdb _dbad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        _dbad = new Cartdb(this);
        initData();
        _dbad.open();
        fl_total = _dbad.getTotalPrice();
        OnCountMoney(fl_total);
        _dbad.close();

    }

    private void initData() {
        _dbad.open();
        listView.Bulid(this);
        Cursor cursor = _dbad.selectConversationList();
        while (cursor.moveToNext()) {
            Log.i("hu", "cursor.getString(1)=" + cursor.getString(1) + " cursor.getString(2)=" + cursor.getString(2)
                    + " cursor.getString(3)=" + cursor.getString(3) + " cursor.getInt(4)=" + cursor.getInt(4)
                    + " cursor.getString(5)=" + cursor.getString(5) + " cursor.getInt(6)=" + cursor.getInt(6));
            listView.addItem(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4), cursor.getString(5), cursor.getInt(6));
        }
        listView.listItemAdapter.notifyDataSetInvalidated();
        _dbad.close();
        //setListViewHeightBasedOnChildren(lv_list);
    }

    @Override
    public void OnPayMoney(String name, String phone, String address, int payment) {
        Log.e("hu","*****pay*****name="+name+" phone="+phone+" address="+address+" payment="+payment);
    }

    @Override
    public void OnCountMoney(float amount) {
        Log.e("hu","*****pay amount*****amount="+amount);
        fl_total = amount;
        tvPriceCount.setText("￥"+String.valueOf(fl_total));
    }

    @Event(value = lv_list, type = OnItemClickListener.class)
    private void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//重写选项被单击事件的处理方法
        listView.listItemAdapter.setSelectItem(position);
        listView.listItemAdapter.notifyDataSetInvalidated();
    }

    @Event({R.id.btn_back, R.id.btn_account})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                Intent intent = new Intent();
                ShoppingCartActivity.this.setResult(RESULT_OK, intent);
                ShoppingCartActivity.this.finish();
                break;
            case R.id.btn_account:
                if (fl_total <= 0.0f) {
                    Toast.makeText(ShoppingCartActivity.this, "请选择商品!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent1 = new Intent();
                intent1.setClass(ShoppingCartActivity.this, ConfirmOrderActivity.class);
                intent1.putExtra("iscart", 1);
                startActivityForResult(intent1, 1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                break;
            case 2:
                break;
            case 51:   //支付返回
                Intent intent = new Intent();
                this.setResult(RESULT_OK, intent);
                this.finish();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent intent = new Intent();
            ShoppingCartActivity.this.setResult(RESULT_OK, intent);
            ShoppingCartActivity.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
