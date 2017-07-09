package com.fanhong.cn.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanhong.cn.R;
import com.fanhong.cn.models.HorizontalListviewModel;
import com.fanhong.cn.synctaskpicture.LoadImage;

import java.util.ArrayList;
import java.util.List;

public class HorizontalListViewAdapter extends BaseAdapter {
    private List<HorizontalListviewModel> modelList = new ArrayList<>();
    private Context mContext;
    private LayoutInflater mInflater;

    public HorizontalListViewAdapter(Context context, List<HorizontalListviewModel> modelList) {
        this.mContext = context;
        this.modelList = modelList;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return modelList.size();
    }

    @Override
    public Object getItem(int position) {
        return modelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.myhorizontallistview_item, null);
            holder.mImage = (ImageView) convertView.findViewById(R.id.myitempic);
            holder.mTitle = (TextView) convertView.findViewById(R.id.myitemtext);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        HorizontalListviewModel model = modelList.get(position);

        LoadImage.Load(holder.mImage,model.getImageUrl(),mContext);
        holder.mTitle.setText(model.getPicName());

        return convertView;
    }

    private static class ViewHolder {
        private TextView mTitle;
        private ImageView mImage;
    }

//    private Bitmap getPropThumnail(int id) {
//        Drawable d = mContext.getResources().getDrawable(id);
//        Bitmap b = SampleConnection.drawableToBitmap(d);
//
//        int w = mContext.getResources().getDimensionPixelOffset(R.dimen.thumnail_default_width);
//        int h = mContext.getResources().getDimensionPixelSize(R.dimen.thumnail_default_height);
//
//        Bitmap thumBitmap = ThumbnailUtils.extractThumbnail(b, -2, -2);
//        return thumBitmap;
//    }



}