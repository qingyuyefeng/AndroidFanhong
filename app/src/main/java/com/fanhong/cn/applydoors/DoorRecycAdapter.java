package com.fanhong.cn.applydoors;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fanhong.cn.R;
import com.fanhong.cn.models.DoorcheckModel;

import java.util.List;

/**
 * Created by Administrator on 2017/9/7.
 */

public class DoorRecycAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<DoorcheckModel> list;
    private LayoutInflater inflater;


    public DoorRecycAdapter(Context context,List<DoorcheckModel> list){
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
       if(list.get(position).getStatus() == 0){
           return 0; //审核通过了的
       }else {
           return 1; //审核中的
       }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View view = null;
        switch (viewType){
            case 0:
                view = inflater.inflate(R.layout.doorlist_checked,parent,false);
                viewHolder = new ViewHolder1(view);
                break;
            case 1:
                view = inflater.inflate(R.layout.doorlist_checking,parent,false);
                viewHolder = new ViewHolder2(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)){
            case 0:
                ViewHolder1 viewHolder1 = (ViewHolder1) holder;
                final String str1 = list.get(position).getCellName();
                final String str2 = list.get(position).getLouNumber();
                final String str3 = list.get(position).getMiyue();
                viewHolder1.layoutItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //自定义接口中的方法
//                        openDoor.openBtnClick(str1,str2,str3);
                    }
                });
                viewHolder1.cellname.setText(str1+str2);
                break;
            case 1:
                ViewHolder2 viewHolder2 = (ViewHolder2) holder;
                viewHolder2.cellname1.setText(list.get(position).getCellName());
                break;
        }
    }

    @Override
    public int getItemCount() {
        if(list!=null){
            return list.size();
        }
        return 0;
    }

    public class ViewHolder1 extends RecyclerView.ViewHolder{
        LinearLayout layoutItem;
        TextView cellname;
        public ViewHolder1(View itemView) {
            super(itemView);
            layoutItem = (LinearLayout) itemView.findViewById(R.id.ll_doorchecked_item);
            cellname = (TextView) itemView.findViewById(R.id.doorchecked_cellname);
        }
    }
    public class ViewHolder2 extends RecyclerView.ViewHolder{
        LinearLayout layoutItem1,layoutNext1;
        TextView cellname1;
        ImageView ivArrow1;
        public ViewHolder2(View itemView) {
            super(itemView);
            layoutItem1 = (LinearLayout) itemView.findViewById(R.id.ll_doorchecking_item);
            cellname1 = (TextView) itemView.findViewById(R.id.doorchecking_cellname);
            ivArrow1 = (ImageView) itemView.findViewById(R.id.doorchecking_next);
            layoutNext1 = (LinearLayout) itemView.findViewById(R.id.ll_doorchecking_control);
        }
    }
}
