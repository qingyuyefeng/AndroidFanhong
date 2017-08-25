package com.fanhong.cn.listviews;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fanhong.cn.ImageLoaderPicture;
import com.fanhong.cn.R;
import com.fanhong.cn.database.Cartdb;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import org.xutils.x;

/**
 * 设备列表类
 * @author zk
 *
 */
public class ConfirmOrderListView  extends  android.widget.ListView{
	public MyAdapter listItemAdapter;
	Context m_context;
	List<Map<String, Object>> listItems;
	View footerView;
	private LayoutInflater mInflater;
	Cartdb _dbad;

	public ConfirmOrderListView(Context context) {
		super(context);
		this.mInflater = LayoutInflater.from(context);
		m_context= context;
	}
	public ConfirmOrderListView(Context context, AttributeSet attrs) {
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

	public void addItem(String goodsid,String title, String price,int amount,String url)
	{
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("id", goodsid);
		map.put("title", title);
		map.put("price", price);
		map.put("amount", String.valueOf(amount));
		map.put("url", url);
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
			convertView = listContainer.inflate(R.layout.listviewconfirmorder, null);
			listItemView.ItemTitle = (TextView)convertView.findViewById(R.id.ItemTitle);
			listItemView.ItemPrice = (TextView)convertView.findViewById(R.id.ItemPrice);
			listItemView.ItemAmount = (TextView)convertView.findViewById(R.id.ItemAmount);
			listItemView.ItemImage= (ImageView)convertView.findViewById(R.id.ItemImage);
			convertView.setTag(listItemView);
			//} else {
			//		listItemView = (ListViewItem) convertView.getTag();
			//	}

			listItemView.ItemTitle.setText((String) listItems.get(position)
					.get("title"));
			listItemView.ItemPrice.setText((String) "￥"+ listItems.get(position)
					.get("price"));
			listItemView.ItemAmount.setText((String) "X"+ listItems.get(position)
					.get("amount"));


			//用ImageLoader加载图片
//			ImageLoader.getInstance().displayImage((String) listItems.get(position).get("url"), listItemView.ItemImage
//					,new ImageLoaderPicture(m_context).getOptions(),new SimpleImageLoadingListener());

			x.image().bind(listItemView.ItemImage,(String)listItems.get(position).get("url"));
			return convertView;
		}

	}

	public class ListViewItem{
		public CheckBox checkbox;
		public ImageView ItemImage;
		public TextView ItemTitle;
		public TextView ItemPrice;
		public TextView ItemAmount;
	}


}
