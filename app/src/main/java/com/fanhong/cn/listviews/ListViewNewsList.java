package com.fanhong.cn.listviews;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fanhong.cn.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备列表类
 * @author zk
 *
 */
public class ListViewNewsList  extends ListView {
	public MyAdapter listItemAdapter;
	Context m_context;
	List<Map<String, Object>> listItems;
	View footerView;
	private LayoutInflater mInflater;

	public ListViewNewsList(Context context) {
		super(context);
		this.mInflater = LayoutInflater.from(context);
		m_context= context;
	}
	public ListViewNewsList(Context context, AttributeSet attrs) {
		super(context,attrs);
		m_context= context;
	}
	public void Bulid(){
		listItems = null;
		listItemAdapter = null;
		listItems = new ArrayList<Map<String, Object>>();
		listItemAdapter = new MyAdapter(m_context,listItems);
		this.setAdapter(listItemAdapter);
	}
	public void reBulid(){
		listItems = null;
		listItemAdapter = null;
		listItems = new ArrayList<Map<String, Object>>();
		listItemAdapter = new MyAdapter(m_context,listItems);
		this.setAdapter(listItemAdapter);
	}

	public void addItem(String title, int image)
	{
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("title", title);
		map.put("image", image);
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
			final int selectID = position;
			ListViewItem  listItemView = null;
			listItemView = new ListViewItem();

			convertView = listContainer.inflate(R.layout.listviewnewslist, null);
			listItemView.ItemTitle = (TextView)convertView.findViewById(R.id.ItemTitle);
			listItemView.ItemImage = (ImageView)convertView.findViewById(R.id.ItemImage);
			convertView.setTag(listItemView);

			listItemView.ItemTitle.setText((String) listItems.get(position)
					.get("title"));

			listItemView.ItemImage.setBackgroundResource( (Integer) listItems.get(position).get("image"));



			if(selectItem==position){
				//convertView.setBackgroundColor(Color.CYAN);
				//convertView.setBackgroundColor(Color.WHITE);
				//	listItemView.groupHide.setImageResource(R.drawable.next_on);
				//	listItemView.ll_doorcontrol.setVisibility(View.VISIBLE);
				//		listItemView.iv_next.setBackgroundResource(R.drawable.follow_off);
			}
			else{
				//		listItemView.ll_doorcontrol.setVisibility(View.GONE);
			}


			//convertView = listContainer.inflate(R.layout.listviewcarlist, null);   


			return convertView;
		}

	}

	public class ListViewItem{
		public ImageView ItemImage;
		public TextView ItemTitle;
	}


}
