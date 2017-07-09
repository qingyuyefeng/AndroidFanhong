package com.fanhong.cn.listviews;


import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fanhong.cn.CommentLargeImageActivity;
import com.fanhong.cn.ImageLoaderPicture;
import com.fanhong.cn.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fanhong.cn.AssessActivity.KEY_CURRENT_INDEX;
import static com.fanhong.cn.AssessActivity.KEY_IMAGE_LIST;

/**
 * 设备列表类
 * @author zk
 *
 */
public class ChangeListView  extends ListView {
	public MyAdapter listItemAdapter;
	Context m_context;
	List<Map<String, Object>> listDiscuss;
	View footerView;
	// private LayoutInflater mInflater;

	public ChangeListView(Context context) {
		super(context);
		//this.mInflater = LayoutInflater.from(context);
		m_context= context;
	}
	public ChangeListView(Context context, AttributeSet attrs) {
		super(context,attrs);
		m_context= context;
	}
	public void Bulid(){
		listDiscuss = null;
		listItemAdapter = null;
		listDiscuss = new ArrayList<Map<String, Object>>();
		listItemAdapter = new MyAdapter(m_context,listDiscuss);
		this.setAdapter(listItemAdapter);
	}

	public void RebulidDiscuss(){
		listDiscuss = null;
		listItemAdapter = null;
		listDiscuss = new ArrayList<Map<String, Object>>();
		listItemAdapter = new MyAdapter(m_context,listDiscuss);
		this.setAdapter(listItemAdapter);
	}

	public void addItem2(int image, String name,String date, String str,String tupian)
	{
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("image", image);
		map.put("name", name);
		map.put("date", date);
		map.put("str", str);
		map.put("tupian", tupian);
		listDiscuss.add(map);
		listItemAdapter.notifyDataSetChanged();
	}

	public void clear(){
		listDiscuss.clear();
		listItemAdapter.notifyDataSetChanged();
	}

	public class MyAdapter extends BaseAdapter{
		private Context context;
		private List<Map<String, Object>> listItems2;
		private LayoutInflater listContainer;
		private int  selectItem=-1;

		public MyAdapter(Context context, List<Map<String, Object>> listItems2) {
			this.context = context;
			listContainer = LayoutInflater.from(context);
			this.listItems2 = listItems2;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return listItems2.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return listItems2.get(position);
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
			// final int selectID = position;
			//ListViewItem  listItemView = null;
			//listItemView = new ListViewItem();
			Log.e("hu","ChangeListView getView********postition="+position+" ,"+((convertView != null)?1:0));
			{
				convertView = listContainer.inflate(R.layout.list_discuss, null);
				ImageView iv = (ImageView)convertView.findViewById(R.id.ItemImage);
				//convertView.setTag(iv);
				iv.setBackgroundResource( (Integer) listItems2.get(position).get("image"));
				TextView tv_name = (TextView)convertView.findViewById(R.id.tv_name);
				String str = (String) listItems2.get(position).get("name");
				String str1;
				if(str.length() == 11){
					str1 = str.substring(0,3)+"****"+str.substring(7);
				}else
					str1 = str;
				tv_name.setText(str1);

				TextView tv_date = (TextView)convertView.findViewById(R.id.tv_date);
				str = (String) listItems2.get(position).get("date");
				str = str.substring(0,10);
				tv_date.setText(str);
				TextView tv_str = (TextView)convertView.findViewById(R.id.tv_str);
				tv_str.setText((String) listItems2.get(position).get("str"));

				ImageView iv_1 = (ImageView)convertView.findViewById(R.id.iv_1);
				ImageView iv_2 = (ImageView)convertView.findViewById(R.id.iv_2);
				ImageView iv_3 = (ImageView)convertView.findViewById(R.id.iv_3);
				str = (String) listItems2.get(position).get("tupian");
				String[] buf = str.split(",");
				List<String> paths = new ArrayList<String>();
				int len = buf.length;
				if(str.length() == 0)
					len =0;
				for(int i = 0;i<len;i++){
					Log.i("hu","*******i="+i+" buf[i]="+buf[i]);
					//buf[i] = buf[i].substring(buf[i].indexOf('"')+1,buf[i].lastIndexOf('"'));
					//Log.i("hu","*******i="+i+" buf[i]="+buf[i]);
					//buf[i] = "http://m.wuyebest.com/public/goods/58ddaa999a1a3.jpg";
					paths.add(i,buf[i]);
				}
				final List<String> paths1 = paths;

				switch (len){
					case 0:
						iv_1.setVisibility(View.GONE);
						iv_2.setVisibility(View.GONE);
						iv_3.setVisibility(View.GONE);
						break;
					case 1:
						iv_2.setVisibility(View.GONE);
						iv_3.setVisibility(View.GONE);
						break;
					case 2:
						iv_3.setVisibility(View.GONE);
						break;
					default:
						break;
				}

				if(len>0) {
					//用ImageLoader加载图片
					ImageLoader.getInstance().displayImage(buf[0], iv_1, new ImageLoaderPicture(m_context).getOptions(), new SimpleImageLoadingListener());
				}
				if(len>1) {
					//用ImageLoader加载图片
					ImageLoader.getInstance().displayImage(buf[1], iv_2, new ImageLoaderPicture(m_context).getOptions(), new SimpleImageLoadingListener());
				}
				if(len>2) {
					//用ImageLoader加载图片
					ImageLoader.getInstance().displayImage(buf[2], iv_3, new ImageLoaderPicture(m_context).getOptions(), new SimpleImageLoadingListener());
				}
				iv_1.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						//点击HorizontalScrollView里的晒单图进入图片详情页
						Intent intent = new Intent(m_context, CommentLargeImageActivity.class);
						intent.putExtra(KEY_CURRENT_INDEX, 0);
						intent.putExtra("cutremove", 1);
						intent.putStringArrayListExtra(KEY_IMAGE_LIST, (ArrayList<String>) paths1);
						m_context.startActivity(intent);
					}
				});
				iv_2.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						//点击HorizontalScrollView里的晒单图进入图片详情页
						Intent intent = new Intent(m_context, CommentLargeImageActivity.class);
						intent.putExtra(KEY_CURRENT_INDEX, 1);
						intent.putExtra("cutremove", 1);
						intent.putStringArrayListExtra(KEY_IMAGE_LIST, (ArrayList<String>) paths1);
						m_context.startActivity(intent);
					}
				});
				iv_3.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						//点击HorizontalScrollView里的晒单图进入图片详情页
						Intent intent = new Intent(m_context, CommentLargeImageActivity.class);
						intent.putExtra(KEY_CURRENT_INDEX, 2);
						intent.putExtra("cutremove", 1);
						intent.putStringArrayListExtra(KEY_IMAGE_LIST, (ArrayList<String>) paths1);
						m_context.startActivity(intent);
					}
				});
			}

			return convertView;
		}
	}

	public class ListViewItem{
		public ImageView ItemImage;
		public TextView tv_name;
		public TextView tv_date;
		public TextView tv_str;
	}


}
