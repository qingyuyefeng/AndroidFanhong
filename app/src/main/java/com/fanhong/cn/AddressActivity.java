package com.fanhong.cn;


import java.util.HashMap;

import org.json.JSONObject;

import com.fanhong.cn.database.Cartdb;
import com.fanhong.cn.listviews.AddressListView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AddressActivity extends SampleActivity implements OnClickListener{
	private Button btn_save;
	private SharedPreferences mSettingPref;
	private SampleConnection mSafoneConnection;
	private Context context;
	private ImageView titleBackImageBtn;
	AddressListView lv_list;
	Cartdb _dbad;
	private EditText et_person,et_phone,et_address;

	public synchronized void connectFail(int type) {
		SampleConnection.USER = "";
		SampleConnection.USER_STATE = 0;

		Toast.makeText(this, getString(R.string.login_fail), Toast.LENGTH_SHORT).show();
	}

	public synchronized void connectSuccess(JSONObject json, int type) {
		int cmd = -1;
		int result = -1;
		int state = 0;
		String str;
		Log.i("hu","ResetPswActivity.java json="+json.toString());
		try {
			str = json.getString("cmd");
			cmd = Integer.parseInt(str);
			str = json.getString("cw");
			result = Integer.parseInt(str);

		} catch (Exception e) {
			connectFail(type);
			return;
		}
		if(cmd == 12) {

		} else {
			connectFail(type);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			//透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			//透明导航栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}

		context = getApplicationContext();
		init();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mSafoneConnection != null) {
			mSafoneConnection.close();
		}
	}

	private void init() {
		et_person = (EditText)findViewById(R.id.et_person);
		et_phone = (EditText)findViewById(R.id.et_phone);
		et_address = (EditText)findViewById(R.id.et_address);
		et_person.setOnClickListener(this);
		et_phone.setOnClickListener(this);
		et_address.setOnClickListener(this);

		titleBackImageBtn = (ImageView)findViewById(R.id.titleBackImageBtn);
		titleBackImageBtn.setOnClickListener(this);

		btn_save = (Button)findViewById(R.id.btn_save);
		btn_save.setOnClickListener(this);

		mSettingPref = getSharedPreferences("Setting", Context.MODE_PRIVATE);

		lv_list = (AddressListView) findViewById(R.id.lv_list);
		lv_list.setOnItemClickListener( //设置选项被单击的监听器
				new OnItemClickListener(){
					public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
						//重写选项被单击事件的处理方法
						lv_list.listItemAdapter.setSelectItem(position);
						lv_list.listItemAdapter.notifyDataSetInvalidated();
						HashMap<String, Object> map = new HashMap<String, Object>();
						map = (HashMap<String, Object>)lv_list.listItemAdapter.getItem(position);
						String strid = map.get("id").toString();
						int aid = Integer.parseInt(strid);

						Intent intent = new Intent();
						intent.putExtra("id", aid);
						AddressActivity.this.setResult(30, intent);
						AddressActivity.this.finish();
					}
				});
		lv_list.setOnItemLongClickListener(new OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
										   int position, long id) {
				// TODO Auto-generated method stub
				deleteAddress(position);
				return true;
			}
		});

		_dbad = new Cartdb(this);
		initData();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
			case RESULT_OK:
				break;
			case 2:
				break;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId())
		{
			case R.id.titleBackImageBtn:
				this.finish();
				break;
			case R.id.btn_save:
				String person = et_person.getText().toString();
				String phone = et_phone.getText().toString();
				String address =et_address.getText().toString();
				if(person == null || person.length() == 0){
					Toast.makeText(this, "联系人不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				if(phone == null || phone.length() == 0){
					Toast.makeText(this, "联系电话不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				_dbad.open();
				_dbad.insertAddress(person, phone, "", address);
				_dbad.close();
				et_person.setText("");
				et_phone.setText("");
				et_address.setText("");
				btn_save.setVisibility(View.GONE);
				initData();
				break;
			case R.id.et_person:
			case R.id.et_phone:
			case R.id.et_address:
				btn_save.setVisibility(View.VISIBLE);
				break;
		}
	}


	private void initData(){
		_dbad.open();
		lv_list.Bulid();
		Cursor cursor = _dbad.queryAddressList();
		while(cursor.moveToNext())
		{
			Log.i("hu","cursor.getString(0)="+cursor.getString(0)+"cursor.getString(1)="+cursor.getString(1)
					+" cursor.getString(2)="+cursor.getString(2)
					+" cursor.getString(3)="+cursor.getString(3)+" cursor.getString(4)="+cursor.getString(4));
			lv_list.addItem(cursor.getString(0),cursor.getString(1), cursor.getString(2),cursor.getString(3),cursor.getString(4));
		}
		lv_list.listItemAdapter.notifyDataSetInvalidated();
		_dbad.close();
	}

	private void deleteAddress(int position){
		if(position<0 || lv_list.listItemAdapter.getCount()<=position){
			return;
		}

		HashMap<String, Object> map = new HashMap<String, Object>();
		map = (HashMap<String, Object>)lv_list.listItemAdapter.getItem(position);
		String alertid = map.get("id").toString();
		final int aid = Integer.parseInt(alertid);
		if(aid>0){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("是否确实要删除此地址？")
					.setCancelable(false)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									_dbad.open();
									_dbad.deleteAddress(aid);
									_dbad.close();
									initData();
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
		}
	}
}
