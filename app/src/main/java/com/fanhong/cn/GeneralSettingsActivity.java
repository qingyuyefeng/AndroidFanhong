package com.fanhong.cn;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.fanhong.cn.util.DataCleanManager;

import org.json.JSONObject;
import org.xutils.x;

public class GeneralSettingsActivity extends SampleActivity implements OnClickListener{
	private TextView tv_cach;
	private LinearLayout ll_erasecaching;
	private Button btn_exitaccount;
	private SharedPreferences mSettingPref;
	private SampleConnection mSafoneConnection;
	private Context context;
	private ImageView titleBackImageBtn;
	private ToggleButton mToggleButton;
	private final int CLEAN_SUC=1001;
	private final int CLEAN_FAIL=1002;

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
		String name = "";
		String phonenumber = "";
		String alias = "";
		try {
			str = json.getString("cmd");
			cmd = Integer.parseInt(str);
			str = json.getString("cw");
			result = Integer.parseInt(str);

			try{
				name = json.getString("name");
			} catch (Exception e) {}
		} catch (Exception e) {
			connectFail(type);
			return;
		}
		if(cmd == 2 && result == 0){

		}else if(cmd == 4) {

			if(result == 0){
				mSettingPref.edit().putInt("Status", 0).commit();
				Intent intent = new Intent();
				this.setResult(2, intent);
				this.finish();
				Toast.makeText(this, getString(R.string.resetpassword_success), Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(this, getString(R.string.resetpassword_fail), Toast.LENGTH_SHORT).show();
			}
		} else {
			connectFail(type);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_generalsettings);

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
		tv_cach = (TextView)findViewById(R.id.tv_cach);

		ll_erasecaching = (LinearLayout)findViewById(R.id.ll_erasecaching);
		ll_erasecaching.setOnClickListener(this);
		btn_exitaccount = (Button)findViewById(R.id.btn_exitaccount);
		btn_exitaccount.setOnClickListener(this);
		titleBackImageBtn = (ImageView)findViewById(R.id.titleBackImageBtn);
		titleBackImageBtn.setOnClickListener(this);
		mToggleButton = (ToggleButton) findViewById(R.id.tgl_Notice);
		mToggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
										 boolean isChecked) {
				// TODO Auto-generated method stub
				checkSetting(isChecked);
			}

		});

		mSettingPref = getSharedPreferences("Setting", Context.MODE_PRIVATE);
		boolean check = false;
		try{
			check = mSettingPref.getBoolean("msg_notification", false);
		}catch (Exception e) {}
		mToggleButton.setChecked(check);
		caculateCacheSize();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId())
		{
			case R.id.titleBackImageBtn:
				this.finish();
				break;
			case R.id.ll_erasecaching:
				cleanDialog();
				break;
			case R.id.btn_exitaccount:
				exitAccountDialog();
				break;
		}
	}

	private void exitAccount(){
		//注销登录，清空
		mSettingPref.edit().putString("Name", "").commit();
		mSettingPref.edit().putString("Password", "").commit();
		mSettingPref.edit().putString("Nick", "").commit();
		mSettingPref.edit().putString("Logo", "").commit();
		mSettingPref.edit().putInt("Remember", 0).commit();
		mSettingPref.edit().putString("gardenName", "").commit();
		mSettingPref.edit().putString("gardenId", "").commit();
		mSettingPref.edit().putString("gardenProperty", "").commit();
		mSettingPref.edit().putInt("Status", 0).commit();
		Intent intent = new Intent();
		setResult(555,intent);
		GeneralSettingsActivity.this.finish();
	}

	private void checkSetting(boolean isChecked){
		mSettingPref.edit().putBoolean("msg_notification", isChecked).commit();
	}

	private void cleanDialog(){
		AlertDialog alert = new AlertDialog.Builder(GeneralSettingsActivity.this)
				.setMessage("是否清除缓存？")
				.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {//设置确定按钮
							//处理确定按钮点击事件
							public void onClick(DialogInterface dialog, int which) {
								clearAppCache();
								//   tvCache.setText("0KB");
							}
						})
				.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {//设置取消按钮
							//取消按钮点击事件
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();//对话框关闭。
							}
						}).create();
		alert.show();
	}

	private void exitAccountDialog(){
		AlertDialog alert = new AlertDialog.Builder(GeneralSettingsActivity.this)
				.setMessage("是否确定要退出账号？")
				.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								if(isLogined() == 1){
									exitAccount();
								}else{
									Toast.makeText(GeneralSettingsActivity.this,"未处于登录状态", Toast.LENGTH_LONG).show();
									dialog.cancel();
								}
							}
						})
				.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
							}
						}).create();
		alert.show();
	}
	private int isLogined(){
		int result = 0;
		try{
			result = mSettingPref.getInt("Status", 0);
		}catch (Exception e) {}
		return result;
	}

	/**
	 * 计算缓存的大小
	 */
	private void caculateCacheSize() {
		String cacheSize = "0KB";
		try {
			cacheSize = DataCleanManager.getTotalCacheSize(context);
			Log.i("hu","***缓存的大小:"+cacheSize);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tv_cach.setText(cacheSize);
	}
	/**
	 * 清除app缓存
	 */
	public void myclearaAppCache() {
		DataCleanManager.clearAllCache(context);
		//caculateCacheSize();
	}

	/**
	 * 清除app缓存
	 *
	 * @param
	 */
	public void clearAppCache() {

		new Thread() {
			@Override
			public void run() {
				Message msg = new Message();
				try {
					myclearaAppCache();
					msg.what = CLEAN_SUC;
					x.image().clearMemCache();
					x.image().clearCacheFiles();
				} catch (Exception e) {
					e.printStackTrace();
					msg.what = CLEAN_FAIL;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case CLEAN_FAIL:
					caculateCacheSize();
					Toast.makeText(GeneralSettingsActivity.this, R.string.clean_fail, Toast.LENGTH_LONG).show();
					break;
				case CLEAN_SUC:
					caculateCacheSize();
					Toast.makeText(GeneralSettingsActivity.this, R.string.clean_success, Toast.LENGTH_LONG).show();
					break;
			}
		};
	};
}