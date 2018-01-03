package com.fanhong.cn.applydoors;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanhong.cn.R;
import com.fanhong.cn.models.Keymodel;
import com.fanhong.cn.util.JsonSyncUtils;
import com.zhy.autolayout.utils.AutoUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/27.
 */

public class KeyListAdapter extends BaseAdapter {
    private final String[] STRINGS = {"审核中", "可用", "审核未通过"};
    private final int[] RESOURCE1 = {R.color.red, R.color.skyblue, R.color.red};
    private final int[] RESOURCE = {R.drawable.yaoshizzsh, R.drawable.yaoshi, R.drawable.yaoshizzsh};

    private Context context;
    private List<Keymodel> list = new ArrayList<>();
    private LayoutInflater inflater;

    OpenClick openClick;

    public void setOpenClick(OpenClick openClick) {
        this.openClick = openClick;
    }

    public interface OpenClick {
        void opendoor(String key, ImageView view);

        void nokey();
    }

    public KeyListAdapter(Context context,String details) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        try {
            JSONArray jsonArray = new JSONObject(details).getJSONArray("list");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject object = jsonArray.getJSONObject(i);
                Keymodel keymodel = new Keymodel();
                keymodel.setLoudongName(object.getString("bname"));
                keymodel.setStatus(object.getInt("sh"));
                keymodel.setKey(object.getString("key"));
                list.add(keymodel);
            }
            notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_door_key2, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Keymodel model = list.get(position);
        viewHolder.louName.setText(model.getLoudongName());
        final String key = model.getKey();
        final int status = model.getStatus();
        viewHolder.keyData.setText(STRINGS[status]);
        viewHolder.keyData.setTextColor(context.getResources().getColor(RESOURCE1[status]));
        viewHolder.icon.setImageResource(RESOURCE[status]);
        final ViewHolder viewHolder1 = viewHolder;
        viewHolder1.power.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("xq","点击的item的status==>"+status);
                if (status == 0) {
                    openClick.nokey();
                }
                if (status == 1) {
                    openClick.opendoor(key, viewHolder1.power);
                }
            }
        });
        return convertView;
    }

    class ViewHolder {
        @ViewInject(R.id.icon_key)
        ImageView icon;
        @ViewInject(R.id.loudong_name)
        TextView louName;
        @ViewInject(R.id.key_status)
        TextView keyData;
        @ViewInject(R.id.power_img)
        ImageView power;

        ViewHolder(View view) {
            x.view().inject(this, view);
            AutoUtils.autoSize(view);
        }
    }
}
