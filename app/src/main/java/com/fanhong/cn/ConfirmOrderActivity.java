package com.fanhong.cn;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.pay.demo.util.OrderInfoUtil2_0;
import com.fanhong.cn.database.Cartdb;
import com.fanhong.cn.pay.OrderInfo;
import com.fanhong.cn.pay.ParameterConfig;
import com.fanhong.cn.pay.PayResult;
import com.fanhong.cn.pay.WXpayUtil;
import com.fanhong.cn.view.PayMoney;
import com.fanhong.cn.view.PayPopupWindow;
import com.fanhong.cn.listviews.ConfirmOrderListView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ConfirmOrderActivity extends SampleActivity implements PayMoney,OnCheckedChangeListener{
	private SharedPreferences mSettingPref;
	private SampleConnection mSafoneConnection;
	private ConfirmOrderListView lv_list;
	private TextView tv_totalmoney;
	private Button btn_ok;
	private PayPopupWindow menuWindow; // 自定义的立即支付编辑弹出框
	private Context mcontext;
	Cartdb _dbad;
	private LinearLayout ll_addaddress;
	private TextView tv_person,tv_phone,tv_address;
	float fl_total = 0.0f;  //合计价格
	private CheckBox checkbox_zfb,checkbox_wx;
	private String id = null;
	private String name,descript;
	private static final int SDK_PAY_FLAG = 1;
	private static final int SDK_CHECK_FLAG = 2;
	int isCart = 0;
	String orderNum = null; //订单号
	private int payway = 0;   //支付方式    1支付宝，2微信
	private String goods_str = null;

	Handler zfb_handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 9000://支付宝支付成功 Intent it;
					//finish();
					Log.e("hu","***2222**支付成功");
					Log.e("hu","*22**支付成功!  orderNum="+orderNum+" name="+name+" descript="+descript+" fl_total="+fl_total);
					uploadTradeNO();
					if(isCart == 1)
						deletCartItem();
					break;
				case 8000://支付宝支付失败
					//finish();
					Log.e("hu","***2222**支付失败");
					btn_ok.setEnabled(true);
					payFailure();
					break;
				default:
					break;
			}
		}
	};

	public synchronized void connectFail(int type) {
		Log.i("hu","*******connectFail");
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
		if(cmd == 22){
			if(result == 0){
				//finish();
				paySuccess();
			}else{
				Toast.makeText(this, "订单出错！请联系客服人员", Toast.LENGTH_SHORT).show();
			}
		} else {
			connectFail(type);
		}
	}

	private BroadcastReceiver myReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals(SampleConnection.MYPAY_RECEIVER)) {
				Log.e("hu","***********payresult receiver**********");
				int code = intent.getExtras().getInt("errcode");
				switch (code) {
					case 0:
						// Toast.makeText(this, "支付成功",0).show();
						uploadTradeNO();
						if(isCart == 1)
							deletCartItem();
						break;
					case -1:
						// Toast.makeText(this, "支付失败",0).show();
						btn_ok.setEnabled(true);
						payFailure();
						break;
					case -2:
						// Toast.makeText(this, "支付取消",0).show();
						btn_ok.setEnabled(true);
						payOrderCancel();
						break;
					default:
						break;
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirmorder);
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

		Button titleBackImageBtn = (Button)findViewById(R.id.btn_back);
		titleBackImageBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		tv_totalmoney = (TextView)findViewById(R.id.tv_totalmoney);
		btn_ok = (Button)findViewById(R.id.btn_ok);
		btn_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//accountPay();
				if(checkbox_zfb.isChecked()){
					payway = 1;
					//pay_zfb(v);
					v.setEnabled(false);
					payByzfb(zfb_handler);
				}else if(checkbox_wx.isChecked()){
					v.setEnabled(false);
					payway = 2;
					//saveOrder();
					payByWX();
					//Intent intent = new Intent();
					//ConfirmOrderActivity.this.setResult(51, intent);
					//ConfirmOrderActivity.this.finish();
				}else{
					Toast.makeText(ConfirmOrderActivity.this, "请先选择支付方式", Toast.LENGTH_SHORT).show();
				}
			}
		});

		ll_addaddress = (LinearLayout)findViewById(R.id.ll_addaddress);
		ll_addaddress.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent2 = new Intent();
				intent2.setClass(ConfirmOrderActivity.this, AddressActivity.class);
				startActivityForResult(intent2, 1);
			}
		});

		lv_list = (ConfirmOrderListView) findViewById(R.id.lv_list);
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

		checkbox_zfb = (CheckBox)findViewById(R.id.checkbox_zfb);
		checkbox_wx = (CheckBox)findViewById(R.id.checkbox_wx);
		checkbox_zfb.setOnCheckedChangeListener(this);
		checkbox_wx.setOnCheckedChangeListener(this);
		//暂不支持微信
		checkbox_wx.setEnabled(false);

		IntentFilter filter = new IntentFilter();
		filter.addAction(SampleConnection.MYPAY_RECEIVER);
		registerReceiver(myReceiver, filter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mSafoneConnection != null) {
			mSafoneConnection.close();
		}
		unregisterReceiver(myReceiver);
	}

	private void initData(){
		lv_list.Bulid();
		Bundle bundle = this.getIntent().getExtras();
		if(bundle != null)
			isCart = bundle.getInt("iscart");
		StringBuffer buf = new StringBuffer();
		if(isCart == 0){
			id = bundle.getString("id");
			name = bundle.getString("name");
			descript = bundle.getString("describe");
			String logourl = bundle.getString("logourl");
			String price = bundle.getString("price");
			int amount = bundle.getInt("amount");
			float fl = Float.parseFloat(price);
			fl_total = fl * amount;
			tv_totalmoney.setText(String.valueOf(fl_total));
			buf.append(id);
			buf.append(":");
			buf.append(amount);
			lv_list.addItem(id,name, price,amount,logourl);
		}else{
			_dbad.open();
			Cursor cursor = _dbad.selectConversationList();
			if(cursor.moveToNext())
			{
				if(cursor.getInt(6) == 1){
					lv_list.addItem(cursor.getString(0),cursor.getString(1),cursor.getString(3),cursor.getInt(4),cursor.getString(5));
					buf.append(cursor.getString(0));
					buf.append(":");
					buf.append(cursor.getInt(4));

					id = cursor.getString(0);
					name = cursor.getString(1);
					descript = cursor.getString(2);
				}
			}
			while(cursor.moveToNext())
			{
				if(cursor.getInt(6) == 1){
					lv_list.addItem(cursor.getString(0),cursor.getString(1),cursor.getString(3),cursor.getInt(4),cursor.getString(5));
					buf.append(",");
					buf.append(cursor.getString(0));
					buf.append(":");
					buf.append(cursor.getInt(4));
				}
			}
			fl_total = _dbad.getTotalPrice();
			tv_totalmoney.setText(String.valueOf(fl_total));
			_dbad.close();
		}
		goods_str = buf.toString();
		lv_list.listItemAdapter.notifyDataSetInvalidated();
		setListViewHeightBasedOnChildren(lv_list);
	}

	public void setListViewHeightBasedOnChildren(ConfirmOrderListView listView) {
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
		lv_list.Bulid();
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
			case RESULT_OK:
				break;
			case 30:   //选择地址返回
				Log.i("hu","******选择地址返回");
				Bundle bundle = data.getExtras();
				if(bundle != null){
					int id = bundle.getInt("id");
					_dbad.open();
					Cursor cur = _dbad.getAddressItem(id);
					if(cur != null){
						cur.moveToNext();
						Log.i("hu","***777*****cursor.getString(1)="+cur.getString(1)+" cursor.getString(2)="+cur.getString(2)
								+" cursor.getString(3)="+cur.getString(3)+" cursor.getString(4)="+cur.getString(4));
						tv_person.setText(cur.getString(1));
						tv_phone.setText(cur.getString(2));
						tv_address.setText(cur.getString(4));
					}
					_dbad.close();
				}
				break;
			case 21:  //登录返回
				break;
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
			case R.id.checkbox_wx:
				if(isChecked){
					checkbox_zfb.setChecked(false);
				}
				break;
		}
	}

	private void uploadTradeNO(){
		Log.i("hu","*******uploadTradeNO()");
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
			mSafoneConnection = new SampleConnection(ConfirmOrderActivity.this, 0);
		mSafoneConnection.connectService1(map);
	}

	/** * 调用支付宝支付 * @param handler * @param order */
	private void payByzfb(Handler handler) {
		OrderInfo order = initOrderInfo();
		//if (!isZfbAvilible(this)) {
		//	Toast.makeText(this, "请先安装支付宝", Toast.LENGTH_SHORT).show();
		//	return;
		//}
		//uploadTradeNO();
		//支付宝支付
		//AlipayUtil alipay=new AlipayUtil(this,order,handler);
		payV2();
	}

	public void payV2() {
		if (TextUtils.isEmpty(ParameterConfig.APPID) || (TextUtils.isEmpty(ParameterConfig.RSA2_PRIVATE) && TextUtils.isEmpty(ParameterConfig.RSA_PRIVATE))) {
			new AlertDialog.Builder(this).setTitle("警告").setMessage("需要配置APPID | RSA_PRIVATE")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface, int i) {
							finish();
						}
					}).show();
			return;
		}

		/**
		 * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
		 * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
		 * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
		 *
		 * orderInfo的获取必须来自服务端；
		 */
		boolean rsa2 = (ParameterConfig.RSA2_PRIVATE.length() > 0);
		Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(ParameterConfig.APPID, rsa2);
		String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

		String privateKey = rsa2 ? ParameterConfig.RSA2_PRIVATE : ParameterConfig.RSA_PRIVATE;
		String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
		final String orderInfo = orderParam + "&" + sign;

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				PayTask alipay = new PayTask(ConfirmOrderActivity.this);
				Map<String, String> result = alipay.payV2(orderInfo, true);
				Log.i("msp", result.toString());

				//	Message msg = new Message();
				//	msg.what = SDK_PAY_FLAG;
				//	msg.obj = result;
				//	mHandler.sendMessage(msg);


				PayResult payResult = new PayResult((Map<String, String>) result);
				/**
				 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
				 */
				String resultInfo = payResult.getResult();// 同步返回需要验证的信息
				String resultStatus = payResult.getResultStatus();
				// 判断resultStatus 为9000则代表支付成功
				if (TextUtils.equals(resultStatus, "9000")) {
					// 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
					zfb_handler.sendEmptyMessage(9000);
					//Toast.makeText(PayDemoActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
				} else {
					// 该笔订单真实的支付结果，需要依赖服务端的异步通知。
					zfb_handler.sendEmptyMessage(8000);
					//Toast.makeText(PayDemoActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
				}
			}
		};

		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	/** * 调用微信支付 * @param handler * @param orderInfo2 */
	private void payByWX() {
		OrderInfo order = initOrderInfo();
		WXpayUtil wxpay=new WXpayUtil(this,order);
	}

	private OrderInfo initOrderInfo() {
		OrderInfo order = new OrderInfo();
		order.outtradeno = getOutTradeNo();
		order.productname = name;
		order.desccontext = descript;
		//order.totalamount = String.valueOf(fl_total);
		order.totalamount = "0.01";
		return order;
	}

	/** * 检查支付包是否存在 * @param context * @return */
	private boolean isZfbAvilible(Context context) {
		PackageManager packageManager = context.getPackageManager();// 获取packagemanager
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
		// 获取所有已安装程序的包信息
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				//System.out.println(pinfo.get(i).packageName);
				if (pn.equals("com.alipay.android.app")) {
					return true;
				}
			}
		}
		return false;
	}

	/** * 检查微信是否存在 * @param context * @return */
	public boolean isWeixinAvilible(Context context) {
		PackageManager packageManager = context.getPackageManager();// 获取packagemanager
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
		// 获取所有已安装程序的包信息
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				System.out.println(pinfo.get(i).packageName);
				if (pn.equals("com.tencent.mm")) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * get the out_trade_no for an order. 获取外部订单号
	 *
	 */
	public String getOutTradeNo() {
		//SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
		//		Locale.getDefault());
		//Date date = new Date();
		//String key = format.format(date);

		long t1 = System.currentTimeMillis() / 1000;
		Log.e("hu","**********t1="+t1);

		Random r = new Random();
		String key = String.valueOf(t1);

		key = key + r.nextInt(100000);
		key = key.substring(0, 15);
		Log.e("hu","**********订单号:"+key);
		orderNum = key;
		return key;
	}

	private void deletCartItem(){
		//支付成功后删除购物车里购买的商品
		_dbad.open();
		_dbad.deleteSelectedItem();
		_dbad.close();
	}

	private void saveOrder(){
		//把订单详情存起来，以便微信支付成功后上传
		mSettingPref.edit().putString("order_total", String.valueOf(fl_total)).commit();
		mSettingPref.edit().putString("order_username", tv_person.getText().toString().trim()).commit();
		mSettingPref.edit().putString("order_userphone", tv_phone.getText().toString().trim()).commit();
		mSettingPref.edit().putString("order_address", tv_address.getText().toString().trim()).commit();
		mSettingPref.edit().putString("order_id", orderNum).commit();
		mSettingPref.edit().putString("order_cellnum", "1").commit();  //小区号
		mSettingPref.edit().putString("order_goods", goods_str).commit();
	}

	private void paySuccess(){
		AlertDialog alert = new AlertDialog.Builder(ConfirmOrderActivity.this)
				.setMessage("订单支付成功！")
				.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								Intent intent = new Intent();
								ConfirmOrderActivity.this.setResult(51, intent);
								ConfirmOrderActivity.this.finish();
							}
						})
				.create();
		alert.show();
	}

	private void payFailure(){
		AlertDialog alert = new AlertDialog.Builder(ConfirmOrderActivity.this)
				.setMessage("订单支付失败！")
				.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {

							}
						})
				.create();
		alert.show();
	}

	private void payOrderCancel(){
		AlertDialog alert = new AlertDialog.Builder(ConfirmOrderActivity.this)
				.setMessage("订单支付取消！")
				.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {

							}
						})
				.create();
		alert.show();
	}
}
