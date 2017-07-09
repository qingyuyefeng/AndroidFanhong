package com.fanhong.cn;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AuthorizeOpenDoorActivity extends SampleActivity implements OnClickListener{
	private EditText et_nickname;
	private Button btn_save;
	private SharedPreferences mSettingPref;
	private SampleConnection mSafoneConnection;
	private Context context;
	private ImageView titleBackImageBtn;
	private String nick;

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
			if(result == 0){
				mSettingPref.edit().putString("Nick", nick).commit();
				Intent intent = new Intent();
				intent.putExtra("nick", nick);
				this.setResult(2, intent);
				this.finish();
				Toast.makeText(this, getString(R.string.modifynick_success), Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(this, getString(R.string.modifynick_fail), Toast.LENGTH_SHORT).show();
			}
		} else {
			connectFail(type);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authorizeopendoor);

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
		et_nickname = (EditText)findViewById(R.id.et_nickname);

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
				String str = et_nickname.getText().toString();
				String name = SampleConnection.USER;
				if(str.length()==0){
					Toast.makeText(this, getString(R.string.inputisempty), Toast.LENGTH_SHORT).show();
					return;
				}

				//nick = str;
				//mSafoneConnection = new SampleConnection(this, 0);
				//mSafoneConnection.connectService1(genModifyNickName(name, str));
				break;
		}
	}

	private Map<String, Object> genModifyNickName(String name, String nick) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cmd", "11");
		map.put("name", name);
		map.put("user", nick);
		return map;
	}
}
