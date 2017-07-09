package com.fanhong.cn;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import com.fanhong.cn.util.Encryption;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ResetPswActivity extends SampleActivity {
	private Button et_register;
	private EditText mUserName;
	private EditText mPassword;
	private EditText mVerification;
	private SharedPreferences mSettingPref;
	private SampleConnection mSafoneConnection;
	private Button btn_getcode;
	private Context context;
	private ImageView titleBackImageBtn;
	private String username;
	private String password;
	private String password_en;
	
	public synchronized void connectFail(int type) {
		Log.i("hu","*******connectFail");
		SampleConnection.USER = "";
		SampleConnection.USER_STATE = 0;
		et_register.setEnabled(true);
		mUserName.setEnabled(true);
		mPassword.setEnabled(true);
		mVerification.setEnabled(true);
		btn_getcode.setText(getString(R.string.randomcode));
		btn_getcode.setEnabled(true);
		Toast.makeText(this, getString(R.string.login_fail), Toast.LENGTH_SHORT).show();
	}

	public synchronized void connectSuccess(JSONObject json, int type) {
		int cmd = -1;
		int result = -1;
		String str;
		String name = "";
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
		if(cmd == 2){
			btn_getcode.setText(getString(R.string.randomcode));
			btn_getcode.setEnabled(true);
			mUserName.setEnabled(true);	
			if(result == 0){				
				Toast.makeText(this, getString(R.string.getcode_success), Toast.LENGTH_SHORT).show();
			}else if(result == 1){
				Toast.makeText(this, getString(R.string.getcode_reget), Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(this, getString(R.string.system_wrong), Toast.LENGTH_SHORT).show();
			}
			
		}else if(cmd == 8) {
			et_register.setEnabled(true);
			mUserName.setEnabled(true);
			mPassword.setEnabled(true);
			mVerification.setEnabled(true);
			if(result == 0){
//				mSettingPref.edit().putInt("Status", 0).commit();
				Toast.makeText(this, getString(R.string.resetpassword_success), Toast.LENGTH_SHORT).show();				
				Intent intent = new Intent();
				intent.putExtra("name", username);
				intent.putExtra("psw", password);				
				this.setResult(2, intent);
				this.finish();					
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
        setContentView(R.layout.activity_resetpsw);
        
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
        mUserName = (EditText)findViewById(R.id.et_phone);
        mPassword = (EditText)findViewById(R.id.password);
        mVerification = (EditText)findViewById(R.id.et_randomcode);
        
        et_register = (Button)findViewById(R.id.et_register);
        et_register.setOnClickListener(mListener);
        btn_getcode = (Button)findViewById(R.id.btn_getcode);
        btn_getcode.setOnClickListener(mListener);         
        
        titleBackImageBtn = (ImageView)findViewById(R.id.titleBackImageBtn);
        titleBackImageBtn.setOnClickListener(mListener);
        mSettingPref = getSharedPreferences("Setting", Context.MODE_PRIVATE);
    }

    private View.OnClickListener mListener = new View.OnClickListener() {
		public void onClick(View paramView) {
			if(paramView.getId()==R.id.titleBackImageBtn){
	    		ResetPswActivity.this.finish();
			}
			else if (paramView.getId() == R.id.et_register) {
				register();
			}
			else if(paramView.getId() == R.id.btn_getcode){
				getCode();
			}
		}
	};

	private Map<String, Object> genResetPsw(String username3, String password,String code) {		
		Map<String, Object> map = new HashMap<String, Object>(); 
		map.put("cmd", "7");
		map.put("yzm", code);
		map.put("name", username3);
		map.put("password", password);
		return map;
	}
	
	private void register() {
		username = mUserName.getText().toString();
		password = mPassword.getText().toString();
		password_en = Encryption.getEncryptString(password);
		String code = mVerification.getText().toString();
		if (username == null || username.isEmpty()
				|| password == null || password.isEmpty()
				|| code == null || code.isEmpty()){
			Toast.makeText(this, getString(R.string.login_noaccount1), Toast.LENGTH_LONG).show();
			return;
		}
		
		et_register.setEnabled(false);
		mUserName.setEnabled(false);
		mPassword.setEnabled(false);
		mVerification.setEnabled(false);
		
		mSafoneConnection = new SampleConnection(this, 0);
		mSafoneConnection.connectService1(genResetPsw(username, password_en,code));	
	}
	
	private void getCode() { 
		String username1 = mUserName.getText().toString();
		if(isMobileNO(username1)){
			if(mSafoneConnection == null){
				mSafoneConnection = new SampleConnection(this, 0);
			}
			mSafoneConnection.connectService1(genCode(username1));
			startCount();
		}else{
			Toast.makeText(this, getString(R.string.phoneiswrong), Toast.LENGTH_LONG).show();
		}
	}

	private TimerTask timerTask;
	private Timer timer;
	public void startCount() {
		timer = new Timer();
		timerTask = new TimerTask() {
			int count = 30;
			@Override
			public void run() {
				if (count > 0) {
					Message message = new Message();
					message.what = 80;
					message.arg1 = count;
					handler.sendMessage(message);
				}
				else {
					Message message1 = new Message();
					message1.what = 81;
					handler.sendMessage(message1);
				}
				count--;
			}
		};
		timer.schedule(timerTask, 0, 1000);
	}

	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			int tt = msg.what;
			switch (tt){
				case 80:
					btn_getcode.setText(msg.arg1+"秒后可重试");
					btn_getcode.setEnabled(false);
					break;
				case 81:
					btn_getcode.setText("重新获取验证码");
					btn_getcode.setEnabled(true);
					timer.cancel();
					break;
			}
		}
	};
	
	public boolean isMobileNO(String mobiles) {
//		String regExp = "^[1]([3][0-9]{1}|59|58|88|89)[0-9]{8}$";
//		String regExp1 = "^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$";
//		Pattern p = Pattern.compile(regExp);
//		Matcher m = p.matcher(mobiles);
//		return m.matches();
		if(mobiles.length() == 11){
			if(mobiles.charAt(0) == '1'){
				char ch = mobiles.charAt(1);
				if(ch == '3'|| ch == '4' || ch == '5'|| ch == '7' || ch == '8'){
					return true;
				}else {
					return false;
				}
			}else {
				return false;
			}
		}else {
			return false;
		}

	}
	
	private Map<String, Object> genCode(String username2) {
		Map<String, Object> map = new HashMap<String, Object>(); 
		map.put("cmd", "1");
		map.put("name", username2);
		return map;
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
}
