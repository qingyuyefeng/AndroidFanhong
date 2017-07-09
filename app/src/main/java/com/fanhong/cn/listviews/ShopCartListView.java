package com.fanhong.cn.listviews;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fanhong.cn.DescriptionActivity;
import com.fanhong.cn.ImageLoaderPicture;
import com.fanhong.cn.R;
import com.fanhong.cn.database.Cartdb;
import com.fanhong.cn.view.CountMoney;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * 设备列表类
 * @author zk
 *
 */
public class ShopCartListView  extends  android.widget.ListView{
	public MyAdapter listItemAdapter;
	Context m_context;
	List<Map<String, Object>> listItems;
	View footerView;
	private LayoutInflater mInflater;
	Cartdb _dbad;
	private CountMoney countMoney;

	public ShopCartListView(Context context) {
		super(context);
		this.mInflater = LayoutInflater.from(context);
		m_context= context;
	}
	public ShopCartListView(Context context, AttributeSet attrs) {
		super(context,attrs);
		m_context= context;
	}
	public void Bulid(CountMoney countMoney){
		this.countMoney = countMoney;
		_dbad = new Cartdb(m_context);
		listItems = null;
		listItemAdapter = null;
		listItems = new ArrayList<Map<String, Object>>();
		listItemAdapter = new MyAdapter(m_context,listItems);
		this.setAdapter(listItemAdapter);
	}

	public void addItem(String goodsid,String title, String detail, String price,int amount,String url,int selected)
	{
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("id", goodsid);
		map.put("title", title);
		map.put("detail", detail);
		map.put("price", price);
		map.put("amount", String.valueOf(amount));
		map.put("url", url);
		map.put("selected", String.valueOf(selected));
		listItems.add(map);
		listItemAdapter.notifyDataSetChanged();
	}

	public void clear(){
		listItems.clear();
		listItemAdapter.notifyDataSetChanged();
	}

	public class MyAdapter extends BaseAdapter{
		private Context context;
		private List<Map<String, Object>> listItems;
		private LayoutInflater listContainer;
		private int  selectItem=-1;

		public MyAdapter(Context context, List<Map<String, Object>> listItems) {
			this.context = context;
			listContainer = LayoutInflater.from(context);
			this.listItems = listItems;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return listItems.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return listItems.get(position);
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public  void setSelectItem(int selectItem) {
			this.selectItem = selectItem;
		}

		public int getSelectItem(){
			return this.selectItem;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ListViewItem  listItemView = new ListViewItem();
			//if(convertView == null ) {
			convertView = listContainer.inflate(R.layout.listviewshopcart, null);
			listItemView.ItemTitle = (TextView)convertView.findViewById(R.id.ItemTitle);
			listItemView.ItemDetail = (TextView)convertView.findViewById(R.id.ItemDetail);
			listItemView.ItemPrice = (TextView)convertView.findViewById(R.id.ItemPrice);

			listItemView.mAmountView = (AmountView) convertView.findViewById(R.id.amountview);
			listItemView.btn_close = (Button)convertView.findViewById(R.id.btn_close);
			listItemView.ItemImage= (ImageView)convertView.findViewById(R.id.ItemImage);
			listItemView.checkbox= (CheckBox)convertView.findViewById(R.id.checkbox);
			listItemView.ll_layout = (LinearLayout)convertView.findViewById(R.id.ll_layout);
			convertView.setTag(listItemView);
			//	} else {
			//		listItemView = (ListViewItem) convertView.getTag();
			//	}

			listItemView.ItemTitle.setText((String) listItems.get(position)
					.get("title"));
			listItemView.ItemDetail.setText((String) listItems.get(position)
					.get("detail"));
			listItemView.ItemPrice.setText((String) "￥"+ listItems.get(position)
					.get("price"));

			final int key = position;
			String str = (String) listItems.get(position).get("selected");
			int num = Integer.parseInt(str);
			if(num == 1)
				listItemView.checkbox.setChecked(true);
			else
				listItemView.checkbox.setChecked(false);
			listItemView.checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
											 boolean isChecked) {
					Log.i("hu","**********position="+key+" isChecked="+isChecked);
					int num = 0;
					if(isChecked)
						num = 1;
					else
						num = 0;
					_dbad.open();
					_dbad.confirmSelected((String) listItems.get(key).get("id"),num);
					Map<String, Object> map = listItems.get(key);
					map.put("selected", String.valueOf(num));
					listItems.set(key, map);
					float tot = _dbad.getTotalPrice();
					countMoney.OnCountMoney(tot);
					_dbad.close();
				}
			});

			listItemView.mAmountView.setGoods_storage(50);
			str = (String) listItems.get(position).get("amount");
			listItemView.mAmountView.setText(str);
			listItemView.mAmountView.setOnAmountChangeListener(new AmountView.OnAmountChangeListener() {
				@Override
				public void onAmountChange(View view, int amount) {
					//  Toast.makeText(getApplicationContext(), "Amount=>  " + amount, Toast.LENGTH_SHORT).show();
					Log.i("hu","**********onAmountChange() amount="+amount);
					_dbad.open();
					_dbad.changeAmount((String) listItems.get(key).get("id"),amount);
					float tot = _dbad.getTotalPrice();
					countMoney.OnCountMoney(tot);
					_dbad.close();
				}
			});

			listItemView.btn_close.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Log.i("hu","**********btn_close");

					deletItem(key);
				}
			});

			//用ImageLoader加载图片
			ImageLoader.getInstance().displayImage((String) listItems.get(position).get("url"), listItemView.ItemImage
					,new ImageLoaderPicture(m_context).getOptions(),new SimpleImageLoadingListener());

			listItemView.ll_layout.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					Log.i("hu","**********ll_layout");
					Intent intent = new Intent();
					String id1 = (String) listItems.get(key).get("id");
					intent.putExtra("id", id1);
					intent.setClass(context, DescriptionActivity.class);
					context.startActivity(intent);
				}
			});

			listItemView.ll_layout.setOnLongClickListener(new OnLongClickListener(){
				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					deletItem(key);
					return false;
				}
			});
			return convertView;
		}

		public void deletItem(int num){
			final int key = num;
			AlertDialog alert = new AlertDialog.Builder(context)
					.setMessage("是否删除此项？")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {//设置确定按钮
								//处理确定按钮点击事件
								public void onClick(DialogInterface dialog, int which) {
									_dbad.open();
									_dbad.deleteItem((String) listItems.get(key).get("id"));
									//float tot = _dbad.getTotalPrice();
									//countMoney.OnCountMoney(tot);
									_dbad.close();
									listItems.remove(key);
									listItemAdapter.notifyDataSetChanged();
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {//设置取消按钮
								//取消按钮点击事件
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();//对话框关闭。
								}
							}).create();
			alert.show();
		}
	}

	public class ListViewItem{
		public CheckBox checkbox;
		public ImageView ItemImage;
		public TextView ItemTitle;
		public TextView ItemDetail;
		public TextView ItemPrice;
		public AmountView mAmountView;
		public Button btn_close;
		public LinearLayout ll_layout;
	}
}
