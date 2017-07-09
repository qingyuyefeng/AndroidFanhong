package com.fanhong.cn.view;

import com.fanhong.cn.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

/**
 * All rights Reserved, Designed By GeofferySun 
 * @Title: 	SelectPicPopupWindow.java
 * @Package sun.geoffery.uploadpic
 * @Description:从底部弹出或滑出选择菜单或窗口
 * @author:	GeofferySun
 * @date:	2015年1月15日 上午1:21:01
 * @version	V1.0
 */
public class PayPopupWindow extends PopupWindow implements OnClickListener{

	private Button payBtn;
	private View mMenuView;
	private CheckBox checkbox_zfb,checkbox_wx;
	private EditText et_name,et_phone,et_address;
	private ImageView iv_close;
	private Context mcontext;
	private PayMoney itemsOnPay;

	@SuppressLint("InflateParams")
	public PayPopupWindow(Context context, PayMoney itemsOnPay) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mcontext = context;
		mMenuView = inflater.inflate(R.layout.layout_pay_atonce, null);
		payBtn = (Button) mMenuView.findViewById(R.id.btn_pay);
		payBtn.setOnClickListener(this);
		et_name = (EditText) mMenuView.findViewById(R.id.et_name);
		et_phone = (EditText) mMenuView.findViewById(R.id.et_phone);
		et_address = (EditText) mMenuView.findViewById(R.id.et_address);
		iv_close = (ImageView) mMenuView.findViewById(R.id.iv_close);
		iv_close.setOnClickListener(this);
		this.itemsOnPay = itemsOnPay;

		checkbox_zfb = (CheckBox) mMenuView.findViewById(R.id.checkbox_zfb);
		checkbox_wx = (CheckBox) mMenuView.findViewById(R.id.checkbox_wx);
		checkbox_zfb.setOnClickListener(this);
		checkbox_wx.setOnClickListener(this);
		// 设置按钮监听
	/*	cancelBtn.setOnClickListener(itemsOnClick);
		pickPhotoBtn.setOnClickListener(itemsOnClick);
		takePhotoBtn.setOnClickListener(itemsOnClick);*/

		// 设置SelectPicPopupWindow的View
		this.setContentView(mMenuView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.MATCH_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.PopupAnimation);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0x80000000);
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
	/*	mMenuView.setOnTouchListener(new OnTouchListener() {

			@Override
			@SuppressLint("ClickableViewAccessibility")
			public boolean onTouch(View v, MotionEvent event) {

				int height = mMenuView.findViewById(R.id.pop_layout).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});*/

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId())
		{
			case R.id.checkbox_zfb:
				checkbox_wx.setChecked(false);
				break;
			case R.id.checkbox_wx:
				checkbox_zfb.setChecked(false);
				break;
			case R.id.iv_close:
				this.dismiss();
				break;
			case R.id.btn_pay:
				pay();
				break;
		}
	}

	public void pay(){
		int payment = 0;
		if(checkbox_zfb.isChecked())
			payment = 1;
		if(checkbox_wx.isChecked())
			payment = 2;
		if(payment == 0){
			Toast.makeText(mcontext, mcontext.getResources().getString(R.string.choosepay), Toast.LENGTH_SHORT).show();
			return;
		}
		String name = et_name.getText().toString();
		String phone = et_phone.getText().toString();
		String address = et_address.getText().toString();
		itemsOnPay.OnPayMoney(name, phone, address, payment);
		this.dismiss();
	}
}
