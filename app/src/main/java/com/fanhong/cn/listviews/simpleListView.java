package com.fanhong.cn.listviews;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.fanhong.cn.AssessActivity;
import com.fanhong.cn.R;
import com.fanhong.cn.database.Cartdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备列表类
 * @author zk
 *
 */
public class simpleListView  extends  android.widget.ListView{
	public MyAdapter listItemAdapter;
	Context m_context;
	Activity context2;
	public List<Map<String, Object>> listItems;
	View footerView;
	private LayoutInflater mInflater;
	Cartdb _dbad;
	private String orderid;  //订单号

	public simpleListView(Context context) {
		super(context);
		this.mInflater = LayoutInflater.from(context);
		m_context= context;
	}
	public simpleListView(Context context, AttributeSet attrs) {
		super(context,attrs);
		m_context= context;
	}
	public void Bulid(String order,Activity context){
		this.orderid = order;
		this.context2 = context;
		_dbad = new Cartdb(m_context);
		listItems = null;
		listItemAdapter = null;
		listItems = new ArrayList<Map<String, Object>>();
		listItemAdapter = new MyAdapter(m_context,listItems);
		this.setAdapter(listItemAdapter);
	}

	public void addItem(String title, String amount, String iid, String isassess)
	{
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("title", title);
		map.put("amount", amount);
		map.put("iid", iid);
		map.put("isassess", isassess);
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
			convertView = listContainer.inflate(R.layout.listviewsimple, null);
			listItemView.ItemTitle = (TextView)convertView.findViewById(R.id.ItemTitle);
			listItemView.ItemAmount = (TextView)convertView.findViewById(R.id.ItemAmount);
			listItemView.btn_assess = (Button)convertView.findViewById(R.id.btn_assess);
			convertView.setTag(listItemView);
			//} else {
			//		listItemView = (ListViewItem) convertView.getTag();
			//	}

			listItemView.ItemTitle.setText((String) listItems.get(position)
					.get("title"));
			listItemView.ItemAmount.setText((String) "x"+ listItems.get(position)
					.get("amount"));
			final int key = position;
			String sign = (String) listItems.get(position).get("isassess");
			if(sign.trim().equals("1")){
				listItemView.btn_assess.setVisibility(View.VISIBLE);
			}else{
				listItemView.btn_assess.setVisibility(View.GONE);
			}
			listItemView.btn_assess.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					String id1 = (String) listItems.get(key).get("iid");
					String name = (String) listItems.get(key).get("title");
					intent.putExtra("iid", id1);
					intent.putExtra("good", name);
					intent.putExtra("orderid", orderid);
					intent.setClass(context, AssessActivity.class);
					context2.startActivityForResult(intent,1);
				}
			});

			return convertView;
		}

	}

	public class ListViewItem{
		public TextView ItemTitle;
		public TextView ItemAmount;
		public Button btn_assess;
	}


}
