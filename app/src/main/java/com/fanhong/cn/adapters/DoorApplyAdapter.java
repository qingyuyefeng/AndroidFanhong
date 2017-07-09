package com.fanhong.cn.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fanhong.cn.R;
import com.fanhong.cn.models.DoorcheckModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/22.
 */

public class DoorApplyAdapter extends BaseAdapter{
    private Context context;
    private List<DoorcheckModel> list = new ArrayList<>();
    private LayoutInflater inflater;

    private final int TYPE1 = 0;
    private final int TYPE2 = 1;

    private OpenDoor openDoor;

    public void setOpenDoor(OpenDoor openDoor) {
        this.openDoor = openDoor;
    }

    public interface OpenDoor{
        void openBtnClick(String str1,String str2,String str3);
    }

    public DoorApplyAdapter(Context context,List<DoorcheckModel> list){
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        if(list == null || list.size() == 0){
            return 0;
        }
        return list.size();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
        if(list == null || list.size() == 0){
            return null;
        }
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if(list.get(position).getStatus() == 0){
           return TYPE1;
        }else {
           return TYPE2;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder1 viewHolder1 = null;
        ViewHolder2 viewHolder2 = null;

        int type = getItemViewType(position);
        if(convertView == null){
            switch (type){
                case TYPE1:
                    convertView = inflater.inflate(R.layout.doorlist_checked,null);
                    viewHolder1 = new ViewHolder1(convertView);
                    final String str1 = list.get(position).getCellName();
                    final String str2 = list.get(position).getLouNumber();
                    final String str3 = list.get(position).getMiyue();
                    final ViewHolder1 viewHolder10 = viewHolder1;
                    viewHolder10.layoutItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //自定义接口中的方法
                            openDoor.openBtnClick(str1,str2,str3);
                        }
                    });
                    viewHolder1.cellname.setText(str1+str2);
                    convertView.setTag(viewHolder1);
                    break;
                case TYPE2:
                    convertView = inflater.inflate(R.layout.doorlist_checking,null);
                    viewHolder2 = new ViewHolder2(convertView);
                    final ViewHolder2 viewHolder20 = viewHolder2;
                    viewHolder20.layoutItem1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(viewHolder20.layoutNext1.getVisibility() == View.GONE){
                                viewHolder20.layoutNext1.setVisibility(View.VISIBLE);
                                viewHolder20.ivArrow1.setImageResource(R.drawable.arrow_down);
                            }else {
                                viewHolder20.layoutNext1.setVisibility(View.GONE);
                                viewHolder20.ivArrow1.setImageResource(R.drawable.next_off);
                            }
                        }
                    });
                    viewHolder2.cellname1.setText(list.get(position).getCellName());
                    convertView.setTag(viewHolder2);
                    break;
                default:
                    break;
            }
        }else {
            switch (type){
                case TYPE1:
                    viewHolder1 = (ViewHolder1) convertView.getTag();
                    final String str1 = list.get(position).getCellName();
                    final String str2 = list.get(position).getLouNumber();
                    final String str3 = list.get(position).getMiyue();
                    final ViewHolder1 viewHolder10 = viewHolder1;
                    viewHolder10.layoutItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //自定义接口中的方法
                            openDoor.openBtnClick(str1,str2,str3);
                        }
                    });
                    viewHolder1.cellname.setText(str1+str2);
                    break;
                case TYPE2:
                    viewHolder2 = (ViewHolder2) convertView.getTag();
                    final ViewHolder2 viewHolder20 = viewHolder2;
                    viewHolder20.layoutItem1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(viewHolder20.layoutNext1.getVisibility() == View.GONE){
                                viewHolder20.layoutNext1.setVisibility(View.VISIBLE);
                                viewHolder20.ivArrow1.setImageResource(R.drawable.arrow_down);
                            }else {
                                viewHolder20.layoutNext1.setVisibility(View.GONE);
                                viewHolder20.ivArrow1.setImageResource(R.drawable.next_off);
                            }
                        }
                    });
                    viewHolder2.cellname1.setText(list.get(position).getCellName());
                    break;
                default:
                    break;
            }
        }
        return convertView;
    }

    private class ViewHolder1{
        LinearLayout layoutItem;
        TextView cellname;
//        ImageView ivArrow;
//        Button openBtn;
        ViewHolder1(View convertView){
            layoutItem = (LinearLayout) convertView.findViewById(R.id.ll_doorchecked_item);
            cellname = (TextView) convertView.findViewById(R.id.doorchecked_cellname);
//            ivArrow = (ImageView) convertView.findViewById(R.id.doorchecked_next);
//            layoutNext = (LinearLayout) convertView.findViewById(R.id.ll_doorched_control);
//            openBtn = (Button) convertView.findViewById(R.id.btn_opendoor);
        }
    }
    private class ViewHolder2{
        LinearLayout layoutItem1,layoutNext1;
        TextView cellname1;
        ImageView ivArrow1;
        ViewHolder2(View convertView){
            layoutItem1 = (LinearLayout) convertView.findViewById(R.id.ll_doorchecking_item);
            cellname1 = (TextView) convertView.findViewById(R.id.doorchecking_cellname);
            ivArrow1 = (ImageView) convertView.findViewById(R.id.doorchecking_next);
            layoutNext1 = (LinearLayout) convertView.findViewById(R.id.ll_doorchecking_control);
        }
    }
}
