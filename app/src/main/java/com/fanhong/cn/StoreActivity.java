package com.fanhong.cn;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fanhong.cn.database.Cartdb;
import com.fanhong.cn.view.GoodsFragment;
import com.fanhong.cn.view.LoadData;
import com.fanhong.cn.listviews.MyFragmentPagerAdapter;
import com.jauker.widget.BadgeView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreActivity extends SampleActivity implements LoadData{
	private SharedPreferences mSettingPref;
	private SampleConnection mSafoneConnection = null;
	private Context context;
	private ImageView titleBackImageBtn;
	private Button btn_cart;
	private LinearLayout header_ll;
	private ImageView iv_horizontal;
	HorizontalScrollView h_scrollview;
	//定义数组来存放按钮图片
	private int mImageViewArray1[] = {R.drawable.icon_rice, R.drawable.icon_oil,R.drawable.icon_nol};
	//定义数组文字
	private int mTextviewArray1[] = {R.string.dami, R.string.you, R.string.mian};
	private int mCurrentChecked = 0;
	private ViewPager mViewPager;
	private MyFragmentPagerAdapter adapter;
	private int PagerCount = mImageViewArray1.length;
	private int item_width = 200;
	private float mCurrentCheckedRadioLeft;// 当前被选中的RadioButton距离左侧的距离
	private BadgeView badgeCart;//购物车记录数图标
	Cartdb _dbad;

	public synchronized void connectFail(int type) {
		Log.i("hu","*******connectFail");
		SampleConnection.USER = "";
		SampleConnection.USER_STATE = 0;

		Toast.makeText(this, getString(R.string.getdatafailed), Toast.LENGTH_SHORT).show();
	}

	public synchronized void connectSuccess(JSONObject json, int type) {
		int cmd = -1;
		int result=-1;
		String str;
		String name = "";
		Log.i("xq","StoreActivity.java json="+json.toString());
		try {
			str = json.getString("cmd");
			cmd = Integer.parseInt(str);
			str = json.getString("cw");
			result = Integer.parseInt(str);

		} catch (Exception e) {
			connectFail(type);
			return;
		}
		if(cmd == 16 && result == 0){
			int count = 0;
			int page = 0;
			try {
				str = json.getString("data");
				String str2 = json.getString("count");
				count = Integer.parseInt(str2);
				str2 = json.getString("page");
				page = Integer.parseInt(str2);
			} catch (Exception e) {
				//connectFail(type);
				return;
			}
			GoodsFragment fragment = (GoodsFragment) adapter.getItem(mCurrentChecked);
			fragment.connectResult(16, str,count, page);
		}else if(cmd == 8) {

		} else {
			connectFail(type);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_store);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			//透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			//透明导航栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		// 启动activity时不自动弹出软键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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
		titleBackImageBtn = (ImageView)findViewById(R.id.titleBackImageBtn);
		titleBackImageBtn.setOnClickListener(mListener);
		mSettingPref = getSharedPreferences("Setting", Context.MODE_PRIVATE);

		btn_cart = (Button) findViewById(R.id.btn_cart);
		btn_cart.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isLogined() > 0){
					Intent intent1 = new Intent(StoreActivity.this, ShoppingCartActivity.class);
					startActivityForResult(intent1,11);
				}else{
					Intent intent1 = new Intent(StoreActivity.this, LoginActivity.class);
					startActivityForResult(intent1,1);
				}

			}
		});

		header_ll = (LinearLayout)findViewById(R.id.header_ll);
		h_scrollview = (HorizontalScrollView)findViewById(R.id.h_scrollview);
		initHorizontalScrollView();

		iv_horizontal = (ImageView) findViewById(R.id.img_h);

		ViewGroup.LayoutParams params =  iv_horizontal.getLayoutParams();
		params.width = item_width;
		iv_horizontal.setLayoutParams(params);


		mViewPager = (ViewPager) findViewById(R.id.viewPager);
		mViewPager.setOnPageChangeListener(new MyPagerOnPageChangeListener());
		mViewPager.setOffscreenPageLimit(1); //设置向左和向右都缓存limit个页面
		mViewPager.setCurrentItem(0);
		mCurrentCheckedRadioLeft = getCurrentCheckedRadioLeft(header_ll);
		initData();
		_dbad = new Cartdb(this);
		badgeCart = new BadgeView(this);
		badgeCart.setTargetView(btn_cart);
		badgeCart.setTextSize(6.0f);
		updateCartCount();
	}

	/**
	 * 更新购物车记录数
	 */
	private void updateCartCount(){
		Log.i("hu","*****updateCartCount()");
		_dbad.open();
		long unReadCount = _dbad.getCountItem();
		if(unReadCount>0){
			badgeCart.setBadgeCount((int)unReadCount);
		}else{
			badgeCart.setBadgeCount(0);
		}
		_dbad.close();
	}

	private void initData() {
		List<Fragment> fragments = new ArrayList<Fragment>();
		for(int i=0;i<PagerCount;i++){
			GoodsFragment mTab = new GoodsFragment();
			mTab.initData(i+1,this);
			fragments.add(mTab);
		}

		adapter = new MyFragmentPagerAdapter(getFragmentManager(), fragments);
		mViewPager.setAdapter(adapter);
	}

	private void initHorizontalScrollView(){
		int pad = 0;
		for (int i = 0; i < PagerCount; i++) {
			View coupon_home_ad_item = LayoutInflater.from(this).inflate(
					R.layout.operatehorizontal, null);
			ImageView icon = (ImageView) coupon_home_ad_item
					.findViewById(R.id.ItemImage);
			TextView tv = (TextView) coupon_home_ad_item
					.findViewById(R.id.ItemTitle);

			icon.setBackgroundResource(mImageViewArray1[i]);
			tv.setText(this.getResources().getString(mTextviewArray1[i]));

			if(i==0){
				int	width =View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
				int	height =View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
				coupon_home_ad_item.measure(width,height);
				width = coupon_home_ad_item.getMeasuredWidth();

				WindowManager wm = this.getWindowManager();
				int totalwidth = wm.getDefaultDisplay().getWidth();

//				Log.i("hu","**********totalwidth="+totalwidth+" width="+width);
				//if(totalwidth > width * PagerCount){
				if(PagerCount < 6){
					pad = (totalwidth - (width * PagerCount))/(PagerCount*2);
					item_width=totalwidth/PagerCount;
//					Log.i("hu","*****1****pad ="+pad);
				}
				else{
					pad = 40;
					item_width=totalwidth/5;
//					Log.i("hu","*****2****pad ="+pad);
				}
//				Log.i("hu","*********item_width="+item_width);
			}
			//int px = dip2px(context, pad);
			//coupon_home_ad_item.setPadding(pad, 0, pad, 0);
			final int index = i;
			coupon_home_ad_item.setMinimumWidth(item_width);
			coupon_home_ad_item.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					oper(index);
				}
			});
			header_ll.addView(coupon_home_ad_item);
		}
		getGoods(1,1);
	}

	private void oper(int index){
//		Log.i("hu","*111*****index="+index);
		AnimationSet _AnimationSet = new AnimationSet(true);
		TranslateAnimation _TranslateAnimation;
		mCurrentChecked = index;
		for(int i=0;i<PagerCount;i++){
			//Log.i("hu","****index:"+index+" --i:"+i+ " PagerCount:"+PagerCount);
			if (index == i) {
				//CURRENTPAGE = checkedId;
				_TranslateAnimation = new TranslateAnimation(
						mCurrentCheckedRadioLeft, dip2px(context, item_width * i), 0f, 0f);
				_AnimationSet.addAnimation(_TranslateAnimation);
				_AnimationSet.setFillAfter(true);
				_AnimationSet.setDuration(300);
				iv_horizontal.startAnimation(_AnimationSet);// 开始上面蓝色横条图片的动画切换
				mViewPager.setCurrentItem(i);// 让下方ViewPager跟随上面的HorizontalScrollView切换
			}
		}
		mCurrentCheckedRadioLeft = getCurrentCheckedRadioLeft(header_ll);// 更新当前蓝色横条距离左边的距离
		h_scrollview.smoothScrollTo((int) mCurrentCheckedRadioLeft
				- item_width, 0);
//		Log.i("hu","*111*****oper");
		//getGoods(index,1);
	}

	private void getGoods(int type, int page){
//		Log.i("hu","*******getGoods()  type="+type+" page="+page);
		if(mSafoneConnection == null)
			mSafoneConnection = new SampleConnection(this, 0);
		mSafoneConnection.connectService1(genGoods(type,page));
	}

	private Map<String, Object> genGoods(int type, int page) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cmd", "15");
		map.put("lx", String.valueOf(type));  //1：米，2：油，3：面
		map.put("page", String.valueOf(page));
		return map;
	}

	private View.OnClickListener mListener = new View.OnClickListener() {
		public void onClick(View paramView) {
			if(paramView.getId()==R.id.titleBackImageBtn){
				StoreActivity.this.finish();
			}

		}
	};

	/**
	 * 获得当前被选中的View距离左侧的距离
	 */
	private float getCurrentCheckedRadioLeft(ViewGroup mGroup) {
		// TODO Auto-generated method stub
		int count = mGroup.getChildCount();
		for (int i = 0; i < count; i++) {
			View childRB = (View) mGroup.getChildAt(i);

			if (i == mCurrentChecked) {
				return dip2px(context, item_width * i);
			}
		}
		return 0f;
	}

	private int dip2px(Context context, float dipValue) {
		//final float scale = context.getResources().getDisplayMetrics().density;
		final float scale = 1;
		return (int) (dipValue * scale + 0.5f);
	}

	private int isLogined(){
		int result = 0;
		try{
			result = mSettingPref.getInt("Status", 0);
		}catch (Exception e) {}
		return result;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
			case RESULT_OK:
				if(requestCode == 11){
//					Log.i("hu","***2***购物车返回");
					updateCartCount();
				}
				break;
			case 2:
				break;
		}
	}

	/**
	 * ViewPager的PageChangeListener(页面改变的监听器)
	 */
	private class MyPagerOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		/**
		 * 滑动ViewPager的时候,让上方的HorizontalScrollView自动切换
		 */
		@Override
		public void onPageSelected(int position) {
			//header_ll.getChildAt(position).performClick();
//			Log.i("hu","*********oper 2 onPageSelected");
			oper(position);
			getGoods(mCurrentChecked+1,1);
		}
	}

	@Override
	public void OnLoadData(int type, int page) {
		// TODO Auto-generated method stub
		getGoods(type,page);
	}
}
