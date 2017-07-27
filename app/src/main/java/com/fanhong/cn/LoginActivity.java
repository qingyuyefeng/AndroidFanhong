package com.fanhong.cn;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fanhong.cn.database.Cartdb;
import com.fanhong.cn.util.Encryption;
import com.fanhong.cn.util.JsonSyncUtils;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.http.request.HttpRequest;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends SampleActivity {
    private Button mLogin;
    private EditText mUserName;
    private EditText mPassword;
    private EditText mVerification;
    private CheckBox mRemember;
    private TextView tv_startregister;
    private TextView tv_forgetpassword;
    private SharedPreferences mSettingPref;
    private SampleConnection mSafoneConnection;
    private Button btn_qrcode;
    private LinearLayout ll_code;
    private View view_code;
    private ImageView iv_showCode;
    private String realCode;
    int login_times = 0;
    Button btn_back;
    String psw;
    private String username1, password1, password_en;

    public synchronized void connectFail(int type) {
        SampleConnection.USER = "";
        SampleConnection.USER_STATE = 0;
        mLogin.setText(R.string.login);
        mLogin.setEnabled(true);
        mUserName.setEnabled(true);
        mPassword.setEnabled(true);
        mRemember.setEnabled(true);
        Toast.makeText(this, getString(R.string.login_fail), Toast.LENGTH_SHORT).show();
    }

    public synchronized void connectSuccess(JSONObject json, int type) {
        int cmd = -1;
        int result = -1;
        String name = "";
        String str;
        String nick = "";
        String userid = "";
        String logo = "";
        Log.i("xq", "LoginAcivity.java json=" + json.toString());
        try {
            str = json.getString("cmd");
            cmd = Integer.parseInt(str);
            str = json.getString("cw");
            result = Integer.parseInt(str);
            try {
                name = json.getString("name");
            } catch (Exception e) {
            }
            try {
                nick = json.getString("user");
            } catch (Exception e) {
            }
            try {
                userid = json.getString("id");
            } catch (Exception e) {
            }
            try {
                //头像图片名字
                logo = json.getString("logo");
            } catch (Exception e) {
            }
        } catch (Exception e) {
            connectFail(type);
            return;
        }
        Log.i("xq", "********result=" + result);
        if (cmd == 6) {
            mLogin.setText(R.string.login);
            mLogin.setEnabled(false);
            mUserName.setEnabled(false);
            mPassword.setEnabled(false);
            mRemember.setEnabled(false);
            if (result == 0) {
                SampleConnection.USER = name;
                SampleConnection.ALIAS = nick;
                SampleConnection.USER_STATE = 1;
                SampleConnection.LOGO_URL = logo;
                getToken(userid);
                examineDbUser(name, userid);
                //登录成功，将用户信息提交保存到SharedPreferences中
                mSettingPref.edit().putString("Name", name).commit();
                mSettingPref.edit().putString("Password", psw).commit();
                mSettingPref.edit().putString("Nick", nick).commit();
                mSettingPref.edit().putInt("Remember", 1).commit();
                mSettingPref.edit().putInt("Status", 1).commit();
                mSettingPref.edit().putString("Logo", logo).commit();
                mLogin.setText(R.string.login_success);
                Intent intent = new Intent();
                this.setResult(21, intent);
                this.finish();
            } else {
                SampleConnection.USER_STATE = 0;
                mSettingPref.edit().putInt("Remember", 0).commit();
                mSettingPref.edit().putInt("Status", 0).commit();
                connectFail(type);
            }
        } else {
            connectFail(type);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

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
        SampleConnection.USER = "";
        mUserName = (EditText) findViewById(R.id.username);
        mPassword = (EditText) findViewById(R.id.password);
        mVerification = (EditText) findViewById(R.id.et_code);
        ll_code = (LinearLayout) findViewById(R.id.ll_code);
        view_code = (View) findViewById(R.id.view_code);
        ll_code.setVisibility(View.GONE);
        view_code.setVisibility(View.GONE);

        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(mListener);

        iv_showCode = (ImageView) findViewById(R.id.iv_showCode);
        iv_showCode.setImageBitmap(Code.getInstance().createBitmap());
        realCode = Code.getInstance().getCode().toLowerCase();
        iv_showCode.setOnClickListener(mListener);

        tv_forgetpassword = (TextView) findViewById(R.id.tv_forgetpassword);
        tv_startregister = (TextView) findViewById(R.id.tv_startregister);
        tv_forgetpassword.setOnClickListener(mListener);
        tv_startregister.setOnClickListener(mListener);

        mRemember = (CheckBox) findViewById(R.id.remember);
        mLogin = (Button) findViewById(R.id.login);
        mLogin.setOnClickListener(mListener);
        btn_qrcode = (Button) findViewById(R.id.btn_qrcode);
        btn_qrcode.setOnClickListener(mListener);
        //这里实例化的sharedpreferences对象提交保存的用户登录后的信息，key值设置为Setting，以后要获取用户登录的信息必须以这个key值来实例化对象
        mSettingPref = getSharedPreferences("Setting", Context.MODE_PRIVATE);
        //  mRemember.setChecked(mSettingPref.getBoolean("Remember", false));
        // if (mSettingPref.getBoolean("Remember", false)) {
        //	mUserName.setText(mSettingPref.getString("UserName", ""));
        //	mPassword.setText(mSettingPref.getString("Password", ""));
        //}
    }

    private View.OnClickListener mListener = new View.OnClickListener() {
        public void onClick(View paramView) {
            switch (paramView.getId()) {
                case R.id.btn_back:
                    Intent intent1 = new Intent();
                    setResult(22, intent1);
                    finish();
                    break;
                case R.id.login:
                    login();
                    break;
                case R.id.btn_qrcode:
                    Intent intent = new Intent();
                    //	intent.setClass(LoginActivity.this, MipcaActivityCapture.class);
                    //	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //	startActivityForResult(intent, 1);
                    break;
                case R.id.iv_showCode:
                    iv_showCode.setImageBitmap(Code.getInstance().createBitmap());
                    realCode = Code.getInstance().getCode().toLowerCase();
                    Log.i("hu", "realCode" + realCode);
                    break;
                case R.id.tv_startregister:
                    Intent intent3 = new Intent();
                    intent3.setClass(LoginActivity.this, RegisterActivity.class);
                    startActivityForResult(intent3, 1);
                    break;
                case R.id.tv_forgetpassword:
                    Intent intent2 = new Intent();
                    intent2.setClass(LoginActivity.this, ResetPswActivity.class);
                    startActivityForResult(intent2, 1);
                    break;
            }

        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent1 = new Intent();
            setResult(22, intent1);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private Map<String, Object> genAccount(String username, String password) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("cmd", "5");
        map.put("name", username);
        map.put("password", password);
        return map;
    }

    private void login() {
        username1 = mUserName.getText().toString();
        password1 = mPassword.getText().toString();
        //将密码采用MD5加密，后台人员不能看到密码明码，保证用户密码的安全
        password_en = Encryption.getEncryptString(password1);
        if (username1 == null || username1.isEmpty()
                || password1 == null || password1.isEmpty()) {
            Toast.makeText(this, getString(R.string.login_noaccount), Toast.LENGTH_SHORT).show();
            return;
        }
        login_times++;
        if (login_times == 4) {
            Toast.makeText(this, getString(R.string.login_verificationcode), Toast.LENGTH_SHORT).show();
            ll_code.setVisibility(View.VISIBLE);
            view_code.setVisibility(View.VISIBLE);
            return;
        }

        if (login_times > 3) {
            String phonecode = mVerification.getText().toString().toLowerCase();
            Log.i("hu", "*******test code:phonecode=" + phonecode + " realCode=" + realCode);
            if (phonecode == null || !phonecode.equals(realCode)) {
                Toast.makeText(this, getString(R.string.input_randomcode2), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        //mSettingPref.edit().putBoolean("Remember", mRemember.isChecked()).commit();
        //if (mRemember.isChecked()) {
        //	mSettingPref.edit().putString("UserName", username).commit();
        //	mSettingPref.edit().putString("Password", password).commit();
        //}
        int status = mSettingPref.getInt("Status", 0);
        Log.i("hu", "*****login status=" + status);
        if (status > 0) {
            String name = mSettingPref.getString("Name", "");
            if (!username1.equals(name)) {
                okToBeChange();
                return;
            }
        }
        mLogin.setText(R.string.logining);
        mLogin.setEnabled(false);
        mUserName.setEnabled(false);
        mPassword.setEnabled(false);
        mRemember.setEnabled(false);
        mSettingPref.edit().putInt("Status", 0).commit();
        mSafoneConnection = new SampleConnection(this, 0);
        SampleConnection.PASSWORD = password_en;
        SampleConnection.USER = username1;
        psw = password_en;
        mSafoneConnection.connectService1(genAccount(username1, password_en));

        //	Intent i = new Intent(this, MainTabActivity.class);
    /*	Intent i = new Intent(this, FragmentMainActivity.class);
	//	i.putExtra("RunType", 0);
		startActivity(i);
		finish();*/
        //	sendDataThread();
    }

    private void okToBeChange() {
        AlertDialog alert = new AlertDialog.Builder(LoginActivity.this)
                .setMessage("登录其他用户后会丢失原用户数据，你确定要继续登录？")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                continueLogin();
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

    private void continueLogin() {
        mLogin.setText(R.string.logining);
        mLogin.setEnabled(false);
        mUserName.setEnabled(false);
        mPassword.setEnabled(false);
        mRemember.setEnabled(false);
        mSettingPref.edit().putInt("Status", 0).commit();
        mSafoneConnection = new SampleConnection(this, 0);
        SampleConnection.PASSWORD = password_en;
        SampleConnection.USER = username1;
        psw = password_en;
        mSafoneConnection.connectService1(genAccount(username1, password_en));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                break;
            case 2: //注册成功，改变手机号和密码
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    mUserName.setText(bundle.getString("name"));
                    mPassword.setText(bundle.getString("psw"));
                }
                break;
        }
    }

    private void examineDbUser(String user, String id) {
        String name = null;
        try {
            name = mSettingPref.getString("Datebaseuser", "");
        } catch (Exception e) {
        }
        if (name != null && name.length() > 0) {
            if (!name.equals(user)) {
                mSettingPref.edit().putString("Datebaseuser", user).commit();
                Cartdb _dbad = new Cartdb(this);
                _dbad.open();
                _dbad.deleteAllItem();
                _dbad.deleteAllAddressItem();
                _dbad.close();
                mSettingPref.edit().putString("UserId", id).commit();
            }
        } else
            mSettingPref.edit().putString("UserId", id).commit();
    }

    private void getToken(String userId) {
        RequestParams params = new RequestParams(App.CMDURL);
        params.addParameter("cmd", "55");
        params.addParameter("id", userId);
        Log.i("getToken json=id:", userId);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                mSettingPref.edit().putString("token",JsonSyncUtils.getJsonValue(s, "token")).commit();
                SampleConnection.TOKEN = JsonSyncUtils.getJsonValue(s, "token");
                x.image().clearMemCache();
                Log.i("getToken json=", "onSuccess:" + s);
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                Log.i("getToken json=", "onError:" + throwable.getMessage());
            }

            @Override
            public void onCancelled(CancelledException e) {
				Log.i("getToken json=","onCancelled:"+e.getMessage());
            }

            @Override
            public void onFinished() {
                Log.i("getToken json=", "onFinished:");
            }
        });
    }

}
