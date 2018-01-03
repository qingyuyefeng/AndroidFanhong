package com.fanhong.cn.applydoors;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fanhong.cn.R;
import com.fanhong.cn.models.Cellmodel;
import com.fanhong.cn.util.TopBarUtil;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by Administrator on 2017/12/27.
 */

public class CellListAdapter extends BaseAdapter {
    private List<Cellmodel> list;
    private Context context;
    private LayoutInflater inflater;
    KeyListAdapter keyListAdapter;

    ItemClick itemClick;

    public void setItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    public interface ItemClick{
        void open(String key,ImageView view);
        void none();
    }

    public CellListAdapter(Context context, List<Cellmodel> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (list.size() > 0) {
            return list.size();
        }
        return 0;
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
        ViewHolder viewHolder = null;
        if(convertView==null){
            convertView = inflater.inflate(R.layout.item_door_key1,null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Cellmodel cellmodel = list.get(position);
        viewHolder.cellName.setText(cellmodel.getCellName());
        keyListAdapter = new KeyListAdapter(context,cellmodel.getDetails());
        keyListAdapter.setOpenClick(new KeyListAdapter.OpenClick() {
            @Override
            public void opendoor(String key, ImageView view) {
                itemClick.open(key,view);
            }

            @Override
            public void nokey() {
                itemClick.none();
            }
        });
        viewHolder.listView .setAdapter(keyListAdapter);
        TopBarUtil.setListViewHeight(viewHolder.listView);
        final ViewHolder finalViewHolder = viewHolder;
        finalViewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(finalViewHolder.listView.getVisibility()==View.GONE){
                    finalViewHolder.listView.setVisibility(View.VISIBLE);
                    finalViewHolder.icoNext.setImageResource(R.drawable.ico_qj1);
                }else{
                    finalViewHolder.listView.setVisibility(View.GONE);
                    finalViewHolder.icoNext.setImageResource(R.drawable.ico_qj);
                }
            }
        });
        return convertView;
    }
    private class ViewHolder{
        @ViewInject(R.id.cell_item_layout)
        AutoLinearLayout layout;
        @ViewInject(R.id.cell_name)
        TextView cellName;
        @ViewInject(R.id.door_next_img)
        ImageView icoNext;
        @ViewInject(R.id.key_list_view)
        ListView listView;
        ViewHolder(View itemView){
            x.view().inject(this,itemView);
            AutoUtils.autoSize(itemView);
        }
    }
}
