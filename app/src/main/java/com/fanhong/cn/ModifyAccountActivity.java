package com.fanhong.cn;


import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ModifyAccountActivity extends SampleActivity implements OnClickListener{
	private EditText et_account;
	private Button btn_save;
	private SharedPreferences mSettingPref;
	private SampleConnection mSafoneConnection;
	private Context context;
	private ImageView titleBackImageBtn;
	
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
        setContentView(R.layout.activity_modifyaccount);
        
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
    	et_account = (EditText)findViewById(R.id.et_account);      
        
        titleBackImageBtn = (ImageView)findViewById(R.id.titleBackImageBtn);
        titleBackImageBtn.setOnClickListener(this);
        
        btn_save = (Button)findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);
        
        mSettingPref = getSharedPreferences("Setting", Context.MODE_PRIVATE);
        String name = null;        
        try{
        	name = mSettingPref.getString("Name", "");
		}catch (Exception e) {}
        if(name != null && name.length()>0)
        	et_account.setText(name);
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
			String str = et_account.getText().toString();
			mSettingPref.edit().putString("Nick", str).commit();
			Intent intent = new Intent();
			intent.putExtra("nick", str);
			this.setResult(2, intent);
			this.finish();
			break;
		}
	}
}
