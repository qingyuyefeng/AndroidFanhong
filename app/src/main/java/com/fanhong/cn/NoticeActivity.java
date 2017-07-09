package com.fanhong.cn;

import com.fanhong.cn.listviews.ListViewnoticelist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;



public class NoticeActivity extends Activity implements OnClickListener{
	private final String noticeNameStr[]={"最新活动","最新活动","最新活动"};
	private final String noticeNameStr1[]={"双11提前狂欢！惊喜不断！","双11提前狂欢！惊喜不断！","双11提前狂欢！惊喜不断！"};
	ListViewnoticelist lv_list;
	Context mcontext;
	Button btn_back;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_notice);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			//透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			//透明导航栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		// 启动activity时不自动弹出软键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		mcontext = getApplicationContext();
		init();
	}

	private void init(){
		btn_back = (Button)findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);

		lv_list = (ListViewnoticelist)findViewById(R.id.lv_list);

		lv_list.setOnItemClickListener( //设置选项被单击的监听器
				new OnItemClickListener(){
					public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
						//重写选项被单击事件的处理方法
						lv_list.listItemAdapter.setSelectItem(position);
						lv_list.listItemAdapter.notifyDataSetInvalidated();
					}
				});
		setList();
	}

	private void setList(){
		lv_list.Bulid();
		for(int i=0;i<noticeNameStr.length;i++){
			lv_list.addItem(noticeNameStr[i] ,noticeNameStr1[i]);

		}
		if(noticeNameStr.length>0){
			//lv_carlist.listItemAdapter.setSelectItem(1);
			lv_list.listItemAdapter.notifyDataSetInvalidated();
		}
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if(view.getId()==R.id.btn_back){
			this.finish();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent();
			this.setResult(2, intent);
			this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
	 