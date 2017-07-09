package com.fanhong.cn.bluetooth;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.fanhong.cn.R;
import com.fanhong.cn.bluetooth.util.BluetoothTools;
import com.fanhong.cn.bluetooth.util.TransmitBean;
import com.fanhong.cn.util.CommonUtility;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


//蓝牙主动连接界面
public class ClientActivity2 extends Activity {

	public static final int RESULT_CODE = 1000;    //选择文件   请求码
	public static final String SEND_FILE_NAME = "sendFileName";
	private  TextView serversText;
	private  EditText chatEditText;
	private  Button sendBtn;
	private Button filesendBtn;
	TextView mSendFileNameTV;
	private ProgressDialog spDialog;
	private ProgressDialog rpDialog;
	String deviceName;
	private TextView tv_title;


	@Override
	protected void onStart() {

		//开启后台service  因为之前关闭后台service，此处开启只是调用后台service的onStart方法，可以去掉这里的startService
//		Intent startService = new Intent(ClientActivity2.this, BluetoothClientService.class);
//		startService(startService);

		//注册BoradcasrReceiver
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothTools.ACTION_DATA_TO_GAME);
		intentFilter.addAction(BluetoothTools.ACTION_CONNECT_SUCCESS);
		intentFilter.addAction(BluetoothTools.ACTION_CONNECT_ERROR);
		intentFilter.addAction(BluetoothTools.ACTION_FILE_SEND_PERCENT);
		intentFilter.addAction(BluetoothTools.ACTION_FILE_RECIVE_PERCENT);
		registerReceiver(broadcastReceiver, intentFilter);
		super.onStart();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client2);
		tv_title = (TextView) this.findViewById(R.id.tv_title);

		Bundle bundle = this.getIntent().getExtras();
		deviceName = bundle.getString("deviceName");
		setTitle("连接"+deviceName);

		//  ActionBar actionBar = getActionBar();
		//actionBar.setDisplayHomeAsUpEnabled(true);
		spDialog=new ProgressDialog(ClientActivity2.this);
		rpDialog=new ProgressDialog(ClientActivity2.this);
		//	serversText = (TextView)findViewById(R.id.clientServersText);
		chatEditText = (EditText)findViewById(R.id.clientChatEditText);

		sendBtn = (Button)findViewById(R.id.clientSendMsgBtn);
		sendBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//发送消息
				TransmitBean data = new TransmitBean();

				SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMddHHmmss");
				String timeStr=sdf.format(new Date());

		  /*      int year,month,day,hour,minute,second;
		        Calendar c = Calendar.getInstance();
		        year = c.get(Calendar.YEAR);
		        month = c.get(Calendar.MONTH);
		        day = c.get(Calendar.DAY_OF_MONTH);
		        hour = c.get(Calendar.HOUR_OF_DAY);
		        minute = c.get(Calendar.MINUTE);
		        second = c.get(Calendar.SECOND);
		        Log.e("hu","将要设置的时间year="+year+" month="+month+" day="+day
		        		+" hour="+hour+" minute="+minute+" second="+second);
				*/
				data.setMsg(timeStr);
				Intent sendDataIntent = new Intent(BluetoothTools.ACTION_DATA_TO_SERVICE);
				sendDataIntent.putExtra(BluetoothTools.DATA, data);
				sendBroadcast(sendDataIntent);
				chatEditText.append("发送："+timeStr+"\n");
			}
		});

		filesendBtn = (Button)findViewById(R.id.fileSendBtn);
		filesendBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				/*发送文件  由于Intent无法传递很多数据，所以先将文件路径广播给BluetoothClientService，
				 * *由该Service读取文件后通过对象流发送给远程蓝牙设备
				 */

				TransmitBean transmit = new TransmitBean();
				String path = CommonUtility.recordFilePath;
				File f = new File(path);
				File[] files = f.listFiles();
				if (files != null) {
					int num = files.length;
					boolean fileExisted = false;
					for (int i = 0; i < num; i++) {
						if (files[i].getName().equals(CommonUtility.timefileName)){
							fileExisted = true;
							String filepath = path + CommonUtility.timefileName;
							transmit.setFilename(CommonUtility.timefileName);
							transmit.setFilepath(filepath);
							Intent sendDataIntent = new Intent(BluetoothTools.ACTION_DATA_TO_SERVICE);
							sendDataIntent.putExtra(BluetoothTools.DATA, transmit);
							sendBroadcast(sendDataIntent);
						}
					}

					postWarning(fileExisted);
					chatEditText.append("发送："+CommonUtility.timefileName+"\n");
				}

			}

		});
	}

	public void setTitle(CharSequence title) {
		tv_title.setText(title);
	}

	public void postWarning(boolean isExisted) {
		if (!isExisted) {
			Toast.makeText(this, "没有检测相应文件", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == RESULT_CODE){
			//请求为 "选择文件"
			try {
				//取得选择的文件名
				String sendFileName = data.getStringExtra(SEND_FILE_NAME);
				mSendFileNameTV.setText(sendFileName);
			} catch (Exception e) {
			}
		}
	}

	//广播接收器
	public  BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
//			if (BluetoothTools.ACTION_CONNECT_SUCCESS.equals(action)) {//连接成功
//				serversText.setText("连接成功");
//				sendBtn.setEnabled(true);
//			}
			if (BluetoothTools.ACTION_CONNECT_ERROR.equals(action)) {//连接失败
				spDialog.dismiss();
				rpDialog.dismiss();
				Toast.makeText(ClientActivity2.this, "通讯失败", 2000).show();
				//	serversText.setText("通讯失败");
				//	sendBtn.setEnabled(true);
			}
			if (BluetoothTools.ACTION_DATA_TO_GAME.equals(action)) {//接收数据
				TransmitBean transmit = (TransmitBean)intent.getExtras().getSerializable(BluetoothTools.DATA);
				String msg = "";
				if(transmit.getFilename()!=null&&!"".equals(transmit.getFilename())){
					msg = "receive file from remote " + new Date().toLocaleString() + " :\r\n" + transmit.getFilename() + "\r\n";
				}else{
					msg = "receive time from remote " + new Date().toLocaleString() + " :\r\n" + transmit.getMsg() + "\r\n";
				}
				if(transmit.isbackflag())
					chatEditText.append("返回："+msg);
				else
					chatEditText.append("收到："+msg);
			}
			if (BluetoothTools.ACTION_FILE_SEND_PERCENT.equals(action)) {//发送文件百分比
				TransmitBean data = (TransmitBean)intent.getExtras().getSerializable(BluetoothTools.DATA);
				spDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				spDialog.setTitle("提示");
				//	spDialog.setIcon(R.drawable.icon);
				if(!"0".equals(data.getTspeed())){
					spDialog.setMessage("文件发送速度:"+data.getTspeed()+"k/s");
				}
				spDialog.setMax(100);
				spDialog.setProgress(Integer.valueOf(data.getUppercent()));
				spDialog.setIndeterminate(false);
				spDialog.setCancelable(true);
//				spDialog.setButton("取消", new DialogInterface.OnClickListener(){
//				    @Override
//				    public void onClick(DialogInterface dialog, int which) {
//				        dialog.cancel();
//				    }
//				});
				spDialog.show();
				if(Integer.valueOf(data.getUppercent())==100){
					spDialog.dismiss();
					spDialog.setProgress(0);
				}
			}
			if (BluetoothTools.ACTION_FILE_RECIVE_PERCENT.equals(action)) {//接收文件百分比
				TransmitBean data = (TransmitBean)intent.getExtras().getSerializable(BluetoothTools.DATA);
				rpDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				rpDialog.setTitle("提示");
				//rpDialog.setIcon(R.drawable.icon);
				if(!"0".equals(data.getTspeed())){
					rpDialog.setMessage("文件接收速度:"+data.getTspeed()+"k/s");
				}
				rpDialog.setMax(100);
				rpDialog.setProgress(Integer.valueOf(data.getUppercent()));
				rpDialog.setIndeterminate(false);
				rpDialog.setCancelable(true);

				rpDialog.show();
				if(Integer.valueOf(data.getUppercent())==100){
					rpDialog.dismiss();
					rpDialog.setProgress(0);
				}
			}
		}
	};

//	@Override
//	public void onBackPressed() {
//	// 这里处理逻辑代码，注意：该方法仅适用于2.0或更新版的sdk
//		//关闭后台Service
//		Intent stopService = new Intent(BluetoothTools.ACTION_STOP_SERVICE);
//		sendBroadcast(stopService);
//		unregisterReceiver(broadcastReceiver);
//		super.onBackPressed();
//	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent selectDeviceIntent = new Intent(BluetoothTools.ACTION_ACL_DISCONNECTED);
			sendBroadcast(selectDeviceIntent);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				Intent selectDeviceIntent = new Intent(BluetoothTools.ACTION_ACL_DISCONNECTED);
				sendBroadcast(selectDeviceIntent);
				this.finish();
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(broadcastReceiver);
		super.onStop();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
}
