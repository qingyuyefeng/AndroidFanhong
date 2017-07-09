package com.fanhong.cn.listviews;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fanhong.cn.R;

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
public class ListViewnoticelist  extends  android.widget.ListView{
	public MyAdapter listItemAdapter;
	Context m_context;
	List<Map<String, Object>> listItems;
	View footerView;
	private LayoutInflater mInflater;

	public ListViewnoticelist(Context context) {
		super(context);
		this.mInflater = LayoutInflater.from(context);
		m_context= context;
	}
	public ListViewnoticelist(Context context, AttributeSet attrs) {
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
 /*   public void setFooterView(View v,boolean bfootVisibled){
    	footerView = v;
		if(this.getFooterViewsCount()>0)
		{
			this.removeFooterView(v);
		}
		if(bfootVisibled){		
			this.addFooterView(v);
		}
		this.setAdapter(listItemAdapter);	
    }*/

	public void addItem(String title, String detail)
	{
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("title", title);
		map.put("detail", detail);
		listItems.add(map);
		listItemAdapter.notifyDataSetChanged();
	}

   /* public String getChecked(){
    	String sreturn = "";
    	String ids = "-1";
    	if(listItems.size()>0){
    		HashMap<String, Object> map = new HashMap<String, Object>();
        	for(int i = 0;i<listItems.size();i++){
            	map = (HashMap<String, Object>)listItems.get(i);
            	String isGroup = map.get("isGroup").toString();
            	if(isGroup.equals("0")){
                	String b = map.get("sel").toString();
                	if(b.equals("true")){
                		String id = map.get("Id").toString();
                		ids = ids + "," + id;
                	}
            	}

        	}    		
    	}   
    	if(!ids.equals("-1")){
    		sreturn = ids;
    	}
    	return sreturn;
    }  */

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

		/*	private void checkedChange(int checkedID,boolean bchecked) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map = (HashMap<String, Object>)listItems.get(checkedID);
                map.put("sel", bchecked);
                listItems.set(checkedID, map);
            }

            private void clickChange(int nselectID){
                HashMap<String, Object> map = new HashMap<String, Object>();
                map = (HashMap<String, Object>)listItems.get(nselectID);
                String skind = map.get("skind").toString();
            }*/
		public View getView(int position, View convertView, ViewGroup parent) {
			ListViewItem  listItemView = new ListViewItem();
			if(convertView == null ) {
				convertView = listContainer.inflate(R.layout.detaillist, null);
				listItemView.ItemTitle = (TextView)convertView.findViewById(R.id.ItemTitle);
				listItemView.ItemStatus = (TextView)convertView.findViewById(R.id.ItemStatus);
				listItemView.ItemImage = (ImageView)convertView.findViewById(R.id.ItemImage);
				listItemView.iv_next = (ImageView)convertView.findViewById(R.id.iv_next);
				convertView.setTag(listItemView);
			} else {
				listItemView = (ListViewItem) convertView.getTag();
			}
			listItemView.ItemTitle.setText((String) listItems.get(position)
					.get("title"));

			String str = (String)listItems.get(position).get("detail");

			listItemView.ItemStatus.setText(str);


			return convertView;
		}

	}

	public class ListViewItem{
		public ImageView ItemImage;
		public TextView ItemTitle;
		public TextView ItemStatus;
		public ImageView iv_next;
	}


}
