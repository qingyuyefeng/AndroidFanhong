package com.fanhong.cn.bluetooth;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fanhong.cn.R;
import com.fanhong.cn.bluetooth.util.BluetoothClientService;
import com.fanhong.cn.bluetooth.util.BluetoothTools;
import com.fanhong.cn.bluetooth.util.ClientListListener;


public class ClientActivity extends Activity {
	private Button SearchBtn;

	private static final String tag = "蓝牙";
	private static final int  REQUEST_DISCOVERABLE_BLUETOOTH = 3;
	private BluetoothAdapter bluetooth=BluetoothAdapter.getDefaultAdapter();
	private ArrayList<BluetoothDevice> unbondDevices=new ArrayList<BluetoothDevice>(); // 用于存放未配对蓝牙设备
	private ArrayList<BluetoothDevice> bondDevices=new ArrayList<BluetoothDevice>();  // 用于存放已配对蓝牙设备
	private ListView unbondDevicesListView ;
	private ListView bondDevicesListView;
	ProgressDialog progressDialog = null;
	private ClientListListener clientListListener;
	private TextView localdevice;
	private boolean client_visible_flag = false;
	int flag_class=0;
	private TextView tv_title;
	private ProgressBar progress;

	@Override
	protected void onStart() {
		// 注册Receiver来获取蓝牙设备相关的结果 将action指定为：ACTION_FOUND
		IntentFilter intent = new IntentFilter();
		intent.addAction(BluetoothDevice.ACTION_FOUND);// 用BroadcastReceiver来取得搜索结果
		//    intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		//    intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
		intent.addAction(BluetoothTools.ACTION_CONNECT_SUCCESS);
		intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		//注册广播接收器
		registerReceiver(searchDevices, intent);
		super.onStart();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// 设置这个窗口
		// requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client);

		//Bundle bundle = this.getIntent().getExtras();
		//flag_class = bundle.getInt("SID");

		//ActionBar actionBar = getActionBar();
		//actionBar.setDisplayHomeAsUpEnabled(true);
		tv_title = (TextView) this.findViewById(R.id.tv_title);
		progress = (ProgressBar) this.findViewById(R.id.progress);
		unbondDevicesListView = (ListView) this.findViewById(R.id.unbondDevices);
		bondDevicesListView = (ListView) this.findViewById(R.id.bondDevices);
		SearchBtn = (Button)findViewById(R.id.searchDevices);
		SearchBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//开始搜索
				unbondDevices.clear();
				//	bondDevices.clear();
				Log.i("调试", "开始搜索");
				setTitle("本机蓝牙地址：" + bluetooth.getAddress());
				//扫描蓝牙设备 最少要12秒，功耗也非常大（电池等） 是异步扫描意思就是一调用就会扫描
				bluetooth.startDiscovery();
			}
		});
		localdevice = (TextView)findViewById(R.id.localdevice);
		localdevice.setText(bluetooth.getName()+"       "+bluetooth.getAddress());
		operbluetooth();
		clientListListener=new ClientListListener (this, bluetooth, unbondDevices, bondDevices, unbondDevicesListView, bondDevicesListView,flag_class);
		clientListListener.addBondDevicesToListView();

		//开启后台service
		Log.i("调试" , "ClientActivity onStart开启后台Servic");
		Intent startService = new Intent(ClientActivity.this, BluetoothClientService.class);
		startService(startService);
	}

	public void setTitle(CharSequence title) {
		tv_title.setText(title);
	}

	public void setProgressVisibility(boolean vi) {
		if(vi)
			progress.setVisibility(View.VISIBLE);
		else
			progress.setVisibility(View.GONE);
	}

	//打开蓝牙
	public void operbluetooth() {
		//判断是否有Bluetooth设备
		if (bluetooth == null) {
			Toast.makeText(this, "没有检测到蓝牙设备", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		Log.i(tag , "检测到蓝牙设备!");
		//判断当前设备中的蓝牙设备是否已经打开（调用isEnabled()来查询当前蓝牙设备的状态，如果返回为false，则表示蓝牙设备没有开启）
		boolean originalBluetooth = (bluetooth != null && bluetooth.isEnabled());
		if(originalBluetooth){
			Log.i(tag , "蓝牙设备已经开启!");
			setTitle("选择连接的设备!");
			return;
		}else if (originalBluetooth == false) {
			//打开Bluetooth设备 这个无提示效果
			//bluetooth.enable();
			//也可以这样,这个有提示效果
			Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivity(intent);
		}
           /*确保蓝牙被发现*/
		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		//设置可见状态的持续时间为500秒，但是最多是300秒
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 500);
		startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE_BLUETOOTH);
	}

	//广播接收器
	private BroadcastReceiver searchDevices = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
				// progressDialog = ProgressDialog.show(context, "请稍等...","搜索蓝牙设备中...", true,true);
				setProgressVisibility(true);
				setTitle("扫描设备...");
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				Log.i("调试", "设备搜索完毕");
				setProgressVisibility(false);
				setTitle("选择连接的设备");
				//   clientListListener.addUnbondDevicesToListView();
				clientListListener.addBondDevicesToListView();
			} else if (BluetoothTools.ACTION_CONNECT_SUCCESS.equals(action)){
				Log.i("hu","*******2222222222222 start ClientActivity1");
				//String name = intent.getParcelableExtra("deviceName");
				//BluetoothDevice device = (BluetoothDevice)intent.getExtras().get(BluetoothTools.DEVICE);
				//	String name = device.getName();
				//		Log.i("hu","*******2222222222223 name="+name);
				if(!client_visible_flag && flag_class==0)
				{
					client_visible_flag = true;
					Intent intent1 = new Intent();
					intent1.setClassName(context,"com.fanhong.cn.bluetooth.ClientActivity2");
					//  intent1.putExtra("deviceName", name);
					startActivityForResult(intent1,5);
				}
			} else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				//获取到设备对象
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				String str= device.getName() + "|" + device.getAddress();
				System.out.println(str);

				short rssi = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);
				Log.i("调试" , "rssi:"+rssi);

				if (device.getBondState() == BluetoothDevice.BOND_BONDED) {

					if (!bondDevices.contains(device)) {
						bondDevices.add(device);
					}
				} else {
					if (!unbondDevices.contains(device)) {
						unbondDevices.add(device);
						clientListListener.addUnbondDevicesToListView();
					}
				}
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode,  Intent data)
	{
		if(requestCode == 5 && flag_class==0)
			client_visible_flag = false;
	}

	@Override
	public void onBackPressed() {

		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();

		}
		bluetooth.cancelDiscovery();
		// 这里处理逻辑代码，注意：该方法仅适用于2.0或更新版的sdk
		//关闭后台Service
		Intent stopService = new Intent(BluetoothTools.ACTION_STOP_SERVICE);
		sendBroadcast(stopService);
		super.onBackPressed();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				this.finish();
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(searchDevices);

		Log.i("调试" , "ClientActivity onStop关闭后台Servic");
		Intent startService = new Intent(ClientActivity.this, BluetoothClientService.class);
		stopService(startService);
		super.onStop();
	}
}
