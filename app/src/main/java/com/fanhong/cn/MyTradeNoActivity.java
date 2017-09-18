package com.fanhong.cn;


import android.app.Activity;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.fanhong.cn.util.TopBarUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_mytradeno)
public class MyTradeNoActivity extends Activity {
	@ViewInject(R.id.tv_title)
	private TextView title;
	@ViewInject(R.id.order_group)
	private RadioGroup orderGroup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		TopBarUtil.initStatusBar(this);
	}
}
