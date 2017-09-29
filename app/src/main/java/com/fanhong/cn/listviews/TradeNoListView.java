package com.fanhong.cn.listviews;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fanhong.cn.PullToLoadPageListView;
import com.fanhong.cn.R;

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
public class TradeNoListView  extends  PullToLoadPageListView{
    public MyAdapter listItemAdapter;
    Context m_context;
    public List<Map<String, Object>> listItems;

    public TradeNoListView(Context context, AttributeSet attrs) {
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

    public void addItem(String orderid,String id,String ordertime, String goods, String price)
    {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("orderid", orderid);
        map.put("id", id);
        map.put("ordertime", ordertime);
        map.put("goods", goods);
        map.put("price", price);
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
                convertView = listContainer.inflate(R.layout.myorder_list_item, null);
                listItemView.tv_id = (TextView)convertView.findViewById(R.id.tv_id);
                listItemView.tv_ordertime = (TextView)convertView.findViewById(R.id.tv_ordertime);
                listItemView.tv_goods = (TextView)convertView.findViewById(R.id.tv_goods);
                listItemView.tv_price = (TextView)convertView.findViewById(R.id.tv_price);
                convertView.setTag(listItemView);
            } else {
                listItemView = (ListViewItem) convertView.getTag();
            }

            listItemView.tv_id.setText((String) listItems.get(position)
                    .get("id"));
            listItemView.tv_ordertime.setText((String) listItems.get(position)
                    .get("ordertime"));
            listItemView.tv_goods.setText((String) listItems.get(position)
                    .get("goods"));
            listItemView.tv_price.setText((String) listItems.get(position)
                    .get("price"));

            final int key = position;



            return convertView;
        }
    }

    public class ListViewItem{
        public TextView tv_id;
        public TextView tv_ordertime;
        public TextView tv_goods;
        public TextView tv_price;
    }
}
