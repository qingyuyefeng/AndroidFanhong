package com.fanhong.cn;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.fanhong.cn.view.PayMoney;
import com.fanhong.cn.view.PayPopupWindow;
import com.fanhong.cn.listviews.ConfirmOrderListView;
import com.fanhong.cn.listviews.simpleListView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import me.nereo.multi_image_selector.bean.Image;

public class OrderDetailActivity extends SampleActivity implements PayMoney,OnCheckedChangeListener{
	private SharedPreferences mSettingPref;
	private SampleConnection mSafoneConnection;
	private simpleListView lv_list;
	private TextView tv_totalmoney;
	private PayPopupWindow menuWindow; // 自定义的立即支付编辑弹出框
	private Context mcontext;
	private LinearLayout ll_addaddress;
	private TextView tv_person,tv_phone,tv_address,tv_forecasttime,tv_arrivetime;
	float fl_total = 0.0f;  //合计价格
	private CheckBox checkbox_zfb,checkbox_wx;
	private String id = null;
	private String name,descript;
	private static final int SDK_PAY_FLAG = 1;
	private static final int SDK_CHECK_FLAG = 2;
	String orderid;
	String orderNum = null; //订单号
	private int payway = 0;   //支付方式    1支付宝，2微信
	private String goods_str = null;

	public synchronized void connectFail(int type) {
		SampleConnection.USER = "";
		SampleConnection.USER_STATE = 0;

		Toast.makeText(this, getString(R.string.login_fail), Toast.LENGTH_SHORT).show();
	}

	public synchronized void connectSuccess(JSONObject json, int type) {
		int cmd = -1;
		int result = -1;
		String str;
		try {
			str = json.getString("cmd");
			cmd = Integer.parseInt(str);
			str = json.getString("cw");
			result = Integer.parseInt(str);
		} catch (Exception e) {
			connectFail(type);
			return;
		}
		if(cmd == 24){
			if(result == 0){
				setData(json);
			}
		} else {
			connectFail(type);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_orderdetail);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			//透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			//透明导航栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		mcontext = getApplicationContext();
		mSettingPref = getSharedPreferences("Setting", Context.MODE_PRIVATE);



		tv_person = (TextView)findViewById(R.id.tv_person);
		tv_phone = (TextView)findViewById(R.id.tv_phone);
		tv_address = (TextView)findViewById(R.id.tv_address);
		tv_forecasttime = (TextView)findViewById(R.id.tv_forecasttime);
		tv_arrivetime = (TextView)findViewById(R.id.tv_arrivetime);
		checkbox_zfb = (CheckBox)findViewById(R.id.checkbox_zfb);
		checkbox_wx = (CheckBox)findViewById(R.id.checkbox_wx);


		ImageView titleBackImageBtn = findViewById(R.id.img_back);
		titleBackImageBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		TextView title = findViewById(R.id.tv_title);
		title.setText("订单详情");
		tv_totalmoney = (TextView)findViewById(R.id.tv_totalmoney);

		lv_list = (simpleListView) findViewById(R.id.lv_list);
		lv_list.setOnItemClickListener( //设置选项被单击的监听器
				new OnItemClickListener(){
					public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
						//重写选项被单击事件的处理方法
						lv_list.listItemAdapter.setSelectItem(position);
						lv_list.listItemAdapter.notifyDataSetInvalidated();
					}
				});
		getOrderDetails();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mSafoneConnection != null) {
			mSafoneConnection.close();
		}
	}

	private void getOrderDetails(){
		String userid = "";
		Bundle bundle = this.getIntent().getExtras();
		if(bundle != null){
			orderid = bundle.getString("iid");
			userid = bundle.getString("uid");
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cmd", "23");
		map.put("uid", userid);  //下订单用户ID
		map.put("iid", orderid);


		if(mSafoneConnection == null)
			mSafoneConnection = new SampleConnection(OrderDetailActivity.this, 0);
		mSafoneConnection.connectService1(map);
	}

	private void setData(JSONObject json){
		try {
			String str = json.getString("user");
			tv_person.setText(str);
			str = json.getString("dh");
			tv_phone.setText(str);
			str = json.getString("ldh");
			tv_address.setText(str);
			str = json.getString("yjtime");
			tv_forecasttime.setText(str);
			str = json.getString("ddtime");
			tv_arrivetime.setText(str);
			str = json.getString("zjje");
			tv_totalmoney.setText(str);
			str = json.getString("ddh");
			String orderid1 =str;

			str = json.getString("zffs");
			if(Integer.parseInt(str) == 1){
				checkbox_zfb.setChecked(true);
				checkbox_wx.setChecked(false);
			}else{
				checkbox_zfb.setChecked(false);
				checkbox_wx.setChecked(true);
			}

			str = json.getString("goods");
			lv_list.Bulid(orderid1,this);
			String[] goods = str.split(",");
			for(int i=0;i<goods.length;i++){
				String[] item = goods[i].split("x");
				String str1 = item[0];
				String num = item[1];
				// Log.i("hu","********str1="+str1+" num="+num);
				String[] buf = str1.split("\\.");
				String iid = buf[0];
				str1 = buf[1];
				String[] buf1 = str1.split(":");
				String name1 = buf1[0];
				String isassess = buf1[1];
				//str1 = item1[1];
				// item = str1.split(":");
				// String name = item[0];
				//String isassess = item[1];
				lv_list.addItem(name1,num,iid,isassess);
			}
			lv_list.listItemAdapter.notifyDataSetInvalidated();

		} catch (Exception e) {}

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

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
			case RESULT_OK:
				break;
			case 21:  //登录返回
				break;
			case 26:  //评价返回
				Bundle bundle = data.getExtras();
				if(bundle != null) {
					String iid = bundle.getString("iid");  //物品id
					updateAssessDate(iid);
				}
				break;
		}
	}

	public void updateAssessDate(String iid){
		int len = lv_list.listItems.size();
		for(int i=0;i<len;i++){
			Map<String, Object> map = lv_list.listItems.get(i);
			if(map.get("iid").equals(iid)){
				map.put("isassess","2");
				lv_list.listItems.set(i,map);
				lv_list.listItemAdapter.notifyDataSetInvalidated();
				break;
			}
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		switch (buttonView.getId())
		{
			case R.id.checkbox_zfb:
				if(isChecked){
					checkbox_wx.setChecked(false);
				}
				break;
//			case R.id.checkbox_wx:
//				if(isChecked){
//					checkbox_zfb.setChecked(false);
//				}
//				break;
		}
	}

	private void uploadTradeNO(){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cmd", "21");
		String str = mSettingPref.getString("UserId", "");
		map.put("uid", str);  //下订单用户ID
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timeStr=sdf.format(new Date());

		map.put("time", timeStr);
		map.put("zjje", String.valueOf(fl_total));  //支付金额
		map.put("zffs", String.valueOf(payway));	//支付方式（1支付宝，2微信）
		map.put("user", tv_person.getText().toString().trim());  //收货人姓名
		map.put("dh", tv_phone.getText().toString().trim()); //收货人手机号
		map.put("ldh", tv_address.getText().toString().trim());  //详细地址
		map.put("ddh", orderNum);   //订单号
		map.put("qid", "1");   //小区ID号
		map.put("goods", goods_str);    //商品

		if(mSafoneConnection == null)
			mSafoneConnection = new SampleConnection(OrderDetailActivity.this, 0);
		mSafoneConnection.connectService1(map);
	}
}
