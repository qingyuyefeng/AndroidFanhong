package com.fanhong.cn;


import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.fanhong.cn.util.Encryption;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ModifyPswActivity extends SampleActivity implements OnClickListener{
	private EditText et_initialpassword;
	private EditText et_newpassword;
	private EditText et_confirmpassword;
	private Button btn_save;
	private SharedPreferences mSettingPref;
	private SampleConnection mSafoneConnection;
	private Context context;
	private ImageView titleBackImageBtn;
	
	public synchronized void connectFail(int type) {
		SampleConnection.USER = "";
		SampleConnection.USER_STATE = 0;
		btn_save.setEnabled(true);
		Toast.makeText(this, getString(R.string.login_fail), Toast.LENGTH_SHORT).show();
	}

	public synchronized void connectSuccess(JSONObject json, int type) {
		int cmd = -1;
		int result = -1;
		String str;
		String name = "";
		String phonenumber = "";
		String alias = "";
		Log.i("hu","ResetPswActivity.java json="+json.toString());
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
		Log.i("hu","********phonenumber="+phonenumber+" alias="+alias+" name="+name);
		if(cmd == 10) {	
			btn_save.setEnabled(true);		
			if(result == 0){
				SampleConnection.USER = "";
				SampleConnection.USER_STATE = 0;
				mSettingPref.edit().putInt("Status", 0).commit();
				Intent intent = new Intent();
				this.setResult(3, intent);
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
        setContentView(R.layout.activity_modifypsw);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			//???????  
	        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);  
	        //?????????  
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
    	et_initialpassword = (EditText)findViewById(R.id.et_initialpassword);   
    	et_newpassword = (EditText)findViewById(R.id.et_newpassword); 
    	et_confirmpassword = (EditText)findViewById(R.id.et_confirmpassword); 
        
        titleBackImageBtn = (ImageView)findViewById(R.id.titleBackImageBtn);
        titleBackImageBtn.setOnClickListener(this);
        
        btn_save = (Button)findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);
        
        mSettingPref = getSharedPreferences("Setting", Context.MODE_PRIVATE);
       
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
			String str1 = et_initialpassword.getText().toString();
			String str2 = et_newpassword.getText().toString();
			String str3 = et_confirmpassword.getText().toString();
			String psw = "";
			String name = "";
			try{
				psw = mSettingPref.getString("Password", "");
				//psw = SampleConnection.PASSWORD;
				//name = SampleConnection.USER;
				name = mSettingPref.getString("Name", "");
			}catch (Exception e) {}
		//	if(mSettingPref.getInt("Status", 0)==0){
		//		Toast.makeText(this, getString(R.string.pleaselogin), Toast.LENGTH_SHORT).show();
		//		return;
		//	}
			String str1_en = Encryption.getEncryptString(str1);
			String str2_en = Encryption.getEncryptString(str2);
				
			if(!str1_en.equals(psw)){
				Toast.makeText(this, getString(R.string.initalpswwrong), Toast.LENGTH_SHORT).show();
				return;
			}else{
				if(TextUtils.isEmpty(str2)){
					Toast.makeText(this, "密码不能为空!", Toast.LENGTH_SHORT).show();
					return;
				}
				if(!str2.equals(str3)){
					Toast.makeText(this, getString(R.string.newpswwrong), Toast.LENGTH_SHORT).show();
					return;
				}
				if(str2.equals(str1)){
					Toast.makeText(this, getString(R.string.initalandnewissame), Toast.LENGTH_SHORT).show();
					return;
				}
			}
			btn_save.setEnabled(false);
			mSafoneConnection = new SampleConnection(this, 0);
			mSafoneConnection.connectService1(genResetAccount(name, str2_en));
			//this.finish();
			break;
		}
	}
	
	private Map<String, Object> genResetAccount(String name, String psw) {
		Map<String, Object> map = new HashMap<String, Object>(); 
		map.put("cmd", "9");
		map.put("name", name);
		map.put("password", psw);
		return map;	
	}
}
