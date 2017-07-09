package com.fanhong.cn;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.fanhong.cn.database.Cartdb;
import com.fanhong.cn.view.CountMoney;
import com.fanhong.cn.view.PayMoney;
import com.fanhong.cn.view.PayPopupWindow;
import com.fanhong.cn.listviews.ShopCartListView;

public class ShoppingCartActivity extends Activity implements PayMoney, CountMoney{
	private ShopCartListView lv_list;
	private TextView tv_totalmoney;
	private Button btn_account;
	private PayPopupWindow menuWindow; // 自定义的立即支付编辑弹出框
	private Context mcontext;
	Cartdb _dbad;
	float fl_total = 0.0f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shoppingcart);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			//透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			//透明导航栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		mcontext = getApplicationContext();
		Button titleBackImageBtn = (Button)findViewById(R.id.btn_back);
		titleBackImageBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				ShoppingCartActivity.this.setResult(RESULT_OK, intent);
				ShoppingCartActivity.this.finish();
			}
		});
		tv_totalmoney = (TextView)findViewById(R.id.tv_totalmoney);
		btn_account = (Button)findViewById(R.id.btn_account);
		btn_account.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//accountPay();
				if(fl_total <= 0.0f){
					Toast.makeText(ShoppingCartActivity.this, "请选择商品!", Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent1 = new Intent();
				intent1.setClass(ShoppingCartActivity.this, ConfirmOrderActivity.class);
				intent1.putExtra("iscart", 1);
				startActivityForResult(intent1, 1);
			}
		});

		lv_list = (ShopCartListView) findViewById(R.id.lv_list);
		lv_list.setOnItemClickListener( //设置选项被单击的监听器
				new OnItemClickListener(){
					public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
						//重写选项被单击事件的处理方法
						lv_list.listItemAdapter.setSelectItem(position);
						lv_list.listItemAdapter.notifyDataSetInvalidated();
					}
				});
		// setList();
		_dbad = new Cartdb(this);
		initData();
		_dbad.open();
		fl_total = _dbad.getTotalPrice();
		OnCountMoney(fl_total);
		_dbad.close();
	}

	private void initData(){
		_dbad.open();
		lv_list.Bulid(this);
		Cursor cursor = _dbad.selectConversationList();
		while(cursor.moveToNext())
		{
			Log.i("hu","cursor.getString(1)="+cursor.getString(1)+" cursor.getString(2)="+cursor.getString(2)
					+" cursor.getString(3)="+cursor.getString(3)+" cursor.getInt(4)="+cursor.getInt(4)
					+" cursor.getString(5)="+cursor.getString(5)+" cursor.getInt(6)="+cursor.getInt(6));
			lv_list.addItem(cursor.getString(0),cursor.getString(1), cursor.getString(2),cursor.getString(3),cursor.getInt(4),cursor.getString(5),cursor.getInt(6));
		}
		lv_list.listItemAdapter.notifyDataSetInvalidated();
		_dbad.close();
		//setListViewHeightBasedOnChildren(lv_list);
	}

	public void setListViewHeightBasedOnChildren(ShopCartListView listView) {
		// 获取ListView对应的Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
			// listAdapter.getCount()返回数据项的数目
			View listItem = listAdapter.getView(i, null, listView);
			// 计算子项View 的宽高
			listItem.measure(0, 0);
			// 统计所有子项的总高度
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}

	private void setList(){
		lv_list.Bulid(this);
		for(int i=0;i<4;i++){
			//lv_list.addItem("双百上等大米", "2016年新米上等东北大米农家蟹田绿色种植10kg20斤","￥50.8");
		}
		//  if(mImageViewArray.length>0){
		//lv_carlist.listItemAdapter.setSelectItem(1);
		lv_list.listItemAdapter.notifyDataSetInvalidated();
		//  }
		//setListViewHeightBasedOnChildren(lv_list);
	}
	private void accountPay(){
		menuWindow = new PayPopupWindow(mcontext, this);
		menuWindow.showAtLocation(findViewById(R.id.mainLayout),
				Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
	}

	@Override
	public void OnPayMoney(String name, String phone, String address,
						   int payment) {
		// TODO Auto-generated method stub
		Log.e("hu","*****pay*****name="+name+" phone="+phone+" address="+address+" payment="+payment);
	}

	@Override
	public void OnCountMoney(float amount) {
		// TODO Auto-generated method stub
		Log.e("hu","*****pay amount*****amount="+amount);
		fl_total = amount;
		tv_totalmoney.setText("￥"+String.valueOf(fl_total));
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
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent();
			ShoppingCartActivity.this.setResult(RESULT_OK, intent);
			ShoppingCartActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
