package com.fanhong.cn.view;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fanhong.cn.AboutActivity;
import com.fanhong.cn.AccountSettingsActivity;
import com.fanhong.cn.GeneralSettingsActivity;
import com.fanhong.cn.ImageLoaderPicture;
import com.fanhong.cn.LoginActivity;
import com.fanhong.cn.MyTradeNoActivity;
import com.fanhong.cn.NoticeActivity;
import com.fanhong.cn.R;
import com.fanhong.cn.SampleConnection;
import com.fanhong.cn.update;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class MineView1 extends BaseFragment implements OnClickListener{
	public static final int PAGER_INDEX = 3;
	private CircleImg iv_login;  //登录头像
	private TextView tv_login;	//点击登录文本条
	private SharedPreferences mSettingPref; //保存用户登录的信息（以分享给其他activity）
	private LinearLayout ll_userset;  //帐号设置
	private LinearLayout ll_mytradeno;	//我的订单
	private LinearLayout ll_notice;	//消息通知
	private LinearLayout ll_call;	//客户热线
	private LinearLayout ll_generalset;		//通用设置
	private LinearLayout ll_about;		//关于我们
	private LinearLayout ll_versionupgrade;		//系统更新
	private TextView tv_call;	//客户热线后面的号码
	private View mView;


	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_mine, null);
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

		// 启动activity时不自动弹出软键盘
//        this.getActivity().getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		//实例化sharedpreferences对象，以key值为Setting获取
		mSettingPref = this.getActivity().getSharedPreferences("Setting", Context.MODE_PRIVATE);

    	iv_login = (CircleImg)view.findViewById(R.id.iv_login);
    	iv_login.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(isLogined() == 0){
				//	 TODO Auto-generated method stub
					Intent intent = new Intent(MineView1.this.getActivity(), LoginActivity.class);
					MineView1.this.getBaseActivity().startActivityForResult(intent,2);
				}
			}
        });

 		tv_login = (TextView)view.findViewById(R.id.tv_login);
 		ll_userset = (LinearLayout)view.findViewById(R.id.ll_userset);
 		ll_userset.setOnClickListener(this);
 		ll_mytradeno = (LinearLayout)view.findViewById(R.id.ll_mytradeno);
 		ll_mytradeno.setOnClickListener(this);
 		ll_notice = (LinearLayout)view.findViewById(R.id.ll_notice);
 		ll_notice.setOnClickListener(this);
 		ll_call = (LinearLayout)view.findViewById(R.id.ll_call);
 		ll_call.setOnClickListener(this);
 		ll_generalset = (LinearLayout)view.findViewById(R.id.ll_generalset);
 		ll_generalset.setOnClickListener(this);
 		ll_about = (LinearLayout)view.findViewById(R.id.ll_about);
 		ll_about.setOnClickListener(this);
 		tv_call = (TextView)view.findViewById(R.id.tv_call);
 		ll_versionupgrade = (LinearLayout)view.findViewById(R.id.ll_versionupgrade);
 		ll_versionupgrade.setOnClickListener(this);

    }

	@Override
	public void onResume() {
		getUser(isLogined());
		super.onResume();
	}

	public void getUser(int status){
    	if(status == 1){
    		String str1 = "";
    		try{
    			str1 = mSettingPref.getString("Nick", "");
    		}catch (Exception e) {}
    		if(str1 == null || str1.length()==0){
    			try{
        			str1 = mSettingPref.getString("Name", "");
        		}catch (Exception e) {}
    		}
			tv_login.setText(str1);

				if(SampleConnection.LOGO_URL != null && SampleConnection.LOGO_URL.length() > 0){
					String ul = SampleConnection.LOGO_URL;
					//用ImageLoader加载图片
					ImageLoader.getInstance().displayImage(ul, iv_login,new ImageLoaderPicture(this.getActivity()).getOptions(),new SimpleImageLoadingListener());
				}
    	}else{
    		tv_login.setText(getString(R.string.keylogin));
			iv_login.setImageResource(R.drawable.ico_tx);
    	}
    }

    public synchronized void setFragment(int type,int k) {
    	switch(type)
    	{
	    	case 1:
	    		getUser(k);
	    		break;
    	}
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId())
		{
		case R.id.ll_userset:
			if(isLogined() == 1){
				Intent intent1 = new Intent(MineView1.this.getActivity(), AccountSettingsActivity.class);
				MineView1.this.getBaseActivity().startActivityForResult(intent1,2);
			}else{
				Toast.makeText(MineView1.this.getActivity(), getString(R.string.pleaselogin), Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.ll_mytradeno:
			if(isLogined() == 1){
				Intent intent1 = new Intent(MineView1.this.getActivity(), MyTradeNoActivity.class);
				MineView1.this.getBaseActivity().startActivityForResult(intent1,2);
			}else{
				Toast.makeText(MineView1.this.getActivity(), getString(R.string.pleaselogin), Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.ll_notice:
			//if(isLogined() == 1){
				Intent intent = new Intent(MineView1.this.getActivity(), NoticeActivity.class);
				MineView1.this.getBaseActivity().startActivityForResult(intent,2);
			//}else{
			//	Toast.makeText(MineView1.this.getActivity(), getString(R.string.pleaselogin), Toast.LENGTH_SHORT).show();
			//}
			break;
		case R.id.ll_call:
			String call = tv_call.getText().toString();
			showDialog(call);
			break;
		case R.id.ll_generalset:
			Intent intent2 = new Intent(MineView1.this.getActivity(), GeneralSettingsActivity.class);
			MineView1.this.getBaseActivity().startActivityForResult(intent2,2);
			break;
		case R.id.ll_about:
			Intent intent5 = new Intent(MineView1.this.getActivity(), AboutActivity.class);
			MineView1.this.getBaseActivity().startActivityForResult(intent5,2);
			break;
		case R.id.ll_versionupgrade:
//			Intent intent4 = new Intent(MineView1.this.getActivity(), update.class);
//			MineView1.this.getBaseActivity().startActivityForResult(intent4,2);
			Toast.makeText(getActivity(),"暂无版本更新",Toast.LENGTH_SHORT).show();
//			PackageManager pm = new PackageManager()
			break;
		}
	}

	private int isLogined(){
		int result = 0;
		try{
			result = mSettingPref.getInt("Status", 0);
		}catch (Exception e) {}
		return result;
	}

	private void showDialog(final String str){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("将要拨打"+str);
		builder.setMessage("是否立即拨打？");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				callNumber(str);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
//        builder.setCancelable(true);
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	private void callNumber(String num){
		Intent intent = new Intent(Intent.ACTION_CALL);
		Uri data = Uri.parse("tel:" + num);
		intent.setData(data);
		startActivity(intent);
	}
}
