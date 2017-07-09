package com.fanhong.cn.listviews;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fanhong.cn.R;
import com.fanhong.cn.database.Cartdb;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 设备列表类
 * @author zk
 *
 */
public class AddressListView  extends  android.widget.ListView{
	public MyAdapter listItemAdapter;
	Context m_context;
	List<Map<String, Object>> listItems;
	View footerView;
	private LayoutInflater mInflater;
	Cartdb _dbad;

	public AddressListView(Context context) {
		super(context);
		this.mInflater = LayoutInflater.from(context);
		m_context= context;
	}
	public AddressListView(Context context, AttributeSet attrs) {
		super(context,attrs);
		m_context= context;
	}
	public void Bulid(){
		_dbad = new Cartdb(m_context);
		listItems = null;
		listItemAdapter = null;
		listItems = new ArrayList<Map<String, Object>>();
		listItemAdapter = new MyAdapter(m_context,listItems);
		this.setAdapter(listItemAdapter);
	}

	public void addItem(String id,String person,String phone, String cell,String content)
	{
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("person", person);
		map.put("phone", phone);
		map.put("cell", cell);
		map.put("content", content);
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
			convertView = listContainer.inflate(R.layout.listviewaddress, null);
			listItemView.tv_person = (TextView)convertView.findViewById(R.id.tv_person);
			listItemView.tv_phone = (TextView)convertView.findViewById(R.id.tv_phone);
			listItemView.tv_cell = (TextView)convertView.findViewById(R.id.tv_cell);
			listItemView.tv_address = (TextView)convertView.findViewById(R.id.tv_address);

			convertView.setTag(listItemView);
			//	} else {
			//		listItemView = (ListViewItem) convertView.getTag();
			//	}

			listItemView.tv_person.setText((String) listItems.get(position)
					.get("person"));
			listItemView.tv_phone.setText((String) listItems.get(position)
					.get("phone"));
			listItemView.tv_cell.setText((String) listItems.get(position)
					.get("cell"));
			listItemView.tv_address.setText((String) listItems.get(position)
					.get("content"));


			return convertView;
		}

	}

	public class ListViewItem{
		public TextView tv_person;
		public TextView tv_phone;
		public TextView tv_cell;
		public TextView tv_address;
	}


}
