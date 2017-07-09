package com.fanhong.cn.listviews;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fanhong.cn.ImageLoaderPicture;
import com.fanhong.cn.PullToLoadPageListView;
import com.fanhong.cn.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 设备列表类
 * @author zk
 *
 */
public class ListViewgoodslist  extends  PullToLoadPageListView{
	public MyAdapter listItemAdapter;
	Context m_context;
	List<Map<String, Object>> listItems;
	View footerView;
	private LayoutInflater mInflater;

	/*   public ListViewgoodslist(Context context) {
           super(context);
           this.mInflater = LayoutInflater.from(context);
           m_context= context;
       }*/
	public ListViewgoodslist(Context context, AttributeSet attrs) {
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

	public void addItem(String title, String detail, String price, String url, String id1)
	{
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("title", title);
		map.put("detail", detail);
		map.put("price", price);
		map.put("url", url);
		map.put("id", id1);
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
			if(convertView == null ) {
				convertView = listContainer.inflate(R.layout.listviewgoodslist, null);
				listItemView.ItemTitle = (TextView)convertView.findViewById(R.id.ItemTitle);
				listItemView.ItemDetail = (TextView)convertView.findViewById(R.id.ItemDetail);
				listItemView.ItemPrice = (TextView)convertView.findViewById(R.id.ItemPrice);
				listItemView.ItemImage = (ImageView)convertView.findViewById(R.id.ItemImage);
				convertView.setTag(listItemView);
			} else {
				listItemView = (ListViewItem) convertView.getTag();
			}
			listItemView.ItemTitle.setText((String) listItems.get(position)
					.get("title"));
			listItemView.ItemDetail.setText((String) listItems.get(position)
					.get("detail"));
			listItemView.ItemPrice.setText((String) listItems.get(position)
					.get("price"));

			//用Volley加载图片
			//VolleyLoadPicture vlp = new VolleyLoadPicture(m_context, listItemView.ItemImage);
			//vlp.getmImageLoader().get((String) listItems.get(position)
			//		.get("url"), vlp.getOne_listener());
			//	vlp = null;


			//用ImageLoader加载图片
			ImageLoader.getInstance().displayImage((String) listItems.get(position)
					.get("url"), listItemView.ItemImage,new ImageLoaderPicture(m_context).getOptions(),new SimpleImageLoadingListener());


			return convertView;
		}

	}

	public class ListViewItem{
		public ImageView ItemImage;
		public TextView ItemTitle;
		public TextView ItemDetail;
		public TextView ItemPrice;
	}


}
