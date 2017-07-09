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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


public class GridViewset  extends  GridView{
	
	public MyAdapter listItemAdapter; 
    Context m_context;
    List<Map<String, Object>> listItems;
    private LayoutInflater mInflater;    
    public GridViewset(Context context) {
		super(context);
		this.mInflater = LayoutInflater.from(context);
		m_context= context;
	}
    public GridViewset(Context context, AttributeSet attrs) {
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
    public void addItem(int Title,int nImgIndex)
    {
    	String name = m_context.getString(Title);
    	HashMap<String, Object> map = new HashMap<String, Object>();
    	map.put("title", name);	
    	map.put("image", nImgIndex); 
    	listItems.add(map);
    	listItemAdapter.notifyDataSetChanged();
    }  
 
    public void delItem(int nIndex)
    {
    	listItems.remove(nIndex);
    	
    }    
    public void delCommit(){
    	listItemAdapter.notifyDataSetChanged();
    }
    
    public void clear(){
    	listItems.clear();
    	listItemAdapter.notifyDataSetChanged();
    }
    
	public class MyAdapter extends BaseAdapter{
		private Context context;                        //运行上下文
		private List<Map<String, Object>> listItems;    //商品信息集合
		private LayoutInflater listContainer;           //视图容器  

		
		public MyAdapter(Context context, List<Map<String, Object>> listItems) {   
			this.context = context;            
			listContainer = LayoutInflater.from(context);   //创建视图容器并设置上下文   
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
  		
 
		public View getView(int position, View convertView, ViewGroup parent) {
			final int selectID = position; 
			ListViewItem  listItemView = null;  
			if (convertView == null) {  
				listItemView = new ListViewItem();
				convertView = listContainer.inflate(R.layout.operatelistview, null);
				convertView.setPadding(4, 4, 4, 4);
				listItemView.ItemTitle = (TextView)convertView.findViewById(R.id.ItemTitle);				  
				listItemView.ItemImage = (ImageView)convertView.findViewById(R.id.ItemImage);
				convertView.setTag(listItemView);  
			}
			else {   
				  listItemView = (ListViewItem)convertView.getTag();   
			}   
			
			listItemView.ItemImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
			listItemView.ItemImage.setPadding(4, 4, 4, 4);
			
			int strid = (Integer) listItems.get(position).get("title");
			listItemView.ItemTitle.setText((String)m_context.getString(strid));   
			listItemView.ItemImage.setBackgroundResource((Integer) listItems.get(position)
					.get("image"));   			

			
			
			
			return convertView;

		} 
	}   
	
	public class ListViewItem{
		public TextView ItemTitle;
		public ImageView ItemImage;
	}    
}
