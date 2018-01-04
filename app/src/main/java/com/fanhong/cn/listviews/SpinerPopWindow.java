package com.fanhong.cn.listviews;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.fanhong.cn.R;
import com.zhy.autolayout.utils.AutoUtils;


/**
 * create PopupWindow  just like spinner ListView
 *
 * @param <T>
 * @param <T>
 * @author Ansen
 * @create time 2016-7-3
 */
public class SpinerPopWindow<T> extends PopupWindow {
    private LayoutInflater inflater;
    private ListView mListView;
    private List<T> list;
    private MyAdapter mAdapter;
    private String per="";

    public SpinerPopWindow(Context context, List<T> list, OnItemClickListener clickListener,String per) {
        super(context);
        inflater = LayoutInflater.from(context);
        this.list = list;
        init(clickListener);
        this.per=per;
    }

    private void init(OnItemClickListener clickListener) {
        View view = inflater.inflate(R.layout.spiner_window_layout, null);
        setContentView(view);
        setWidth(LayoutParams.WRAP_CONTENT);
        setHeight(LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x00);
        setBackgroundDrawable(dw);
        mListView = (ListView) view.findViewById(R.id.lv_spinner_window);
        mListView.setAdapter(mAdapter = new MyAdapter());
        mListView.setOnItemClickListener(clickListener);
    }

    public void notifyDataChanged() {
        mAdapter.notifyDataSetChanged();
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.spiner_item_layout, null);
                holder.tvName = (TextView) convertView.findViewById(R.id.tv_item_text);
                AutoUtils.autoSize(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvName.setText(getItem(position).toString()+per);
            return convertView;
        }
    }

    private class ViewHolder {
        private TextView tvName;
    }
}
