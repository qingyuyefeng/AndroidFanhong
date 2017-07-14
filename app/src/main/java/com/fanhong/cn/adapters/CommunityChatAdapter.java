package com.fanhong.cn.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanhong.cn.App;
import com.fanhong.cn.R;
import com.fanhong.cn.bean.CommunityMessageBean;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * Created by Administrator on 2017/6/30.
 */

public class CommunityChatAdapter extends BaseAdapter {

    private Context context;
    private List<CommunityMessageBean> list;
    private LayoutInflater mInflater;
    private ImageOptions options;
    private ViewHolderLeft holderLeft;
    private ViewHolderRight holderRight;
    private CommunityMessageBean bean;

    public CommunityChatAdapter(Context context, List<CommunityMessageBean> list) {
        this.context = context;
        this.list = list;
        this.mInflater = LayoutInflater.from(context);
        this.options = new ImageOptions.Builder().setLoadingDrawableId(R.drawable.ilon_yh).setIgnoreGif(false).setFailureDrawableId(R.drawable.ilon_yh).setCircular(true).setUseMemCache(true).build();
    }

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
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        if (convertView == null) {
            switch (type) {
                case CommunityMessageBean.TYPE_LEFT:
                    convertView = mInflater.inflate(R.layout.item_chat_left, null);
                    holderLeft = new ViewHolderLeft();
                    x.view().inject(holderLeft, convertView);
                    AutoUtils.autoSize(convertView);
                    convertView.setTag(holderLeft);
                    break;
                case CommunityMessageBean.TYPE_RIGHT:
                    convertView = mInflater.inflate(R.layout.item_chat_right, null);
                    holderRight = new ViewHolderRight();
                    x.view().inject(holderRight, convertView);
                    AutoUtils.autoSize(convertView);
                    convertView.setTag(holderRight);
                    break;
                default:
                    break;
            }
        } else {
            switch (type) {
                case CommunityMessageBean.TYPE_LEFT:
                    holderLeft = (ViewHolderLeft) convertView.getTag();
                    break;
                case CommunityMessageBean.TYPE_RIGHT:
                    holderRight = (ViewHolderRight) convertView.getTag();
                    break;
                default:
                    break;
            }
        }
        bean = list.get(position);
        switch (type) {
            case CommunityMessageBean.TYPE_LEFT:
                holderLeft.tv_time.setVisibility(View.GONE);
                if (iswentmin(bean.getMsgTime(), position == 0 ? bean.getMsgTime() : list.get(position - 1).getMsgTime(),5)) {
                    App.old_msg_times.add(bean.getMsgTime());
                }
                if (App.old_msg_times.contains(bean.getMsgTime())) {
                    String sendTime = new SimpleDateFormat("MM月dd日 HH:mm").format(new Date(bean.getMsgTime()));
                    holderLeft.tv_time.setText(sendTime);
                    holderLeft.tv_time.setVisibility(View.VISIBLE);
                }
                holderLeft.tv_user.setText(bean.getUserName());
                holderLeft.tv_msg.setText(bean.getMessage());
                x.image().bind(holderLeft.img_head, bean.getHeadUrl(), options);
                break;
            case CommunityMessageBean.TYPE_RIGHT:
                holderLeft.tv_time.setVisibility(View.GONE);
                if (iswentmin(bean.getMsgTime(), position == 0 ? bean.getMsgTime() : list.get(position - 1).getMsgTime(),5)) {
                    App.old_msg_times.add(bean.getMsgTime());
                }
                if (App.old_msg_times.contains(bean.getMsgTime())) {
                    String sendTime = new SimpleDateFormat("MM月dd日 HH:mm").format(new Date(bean.getMsgTime()));
                    holderRight.tv_time.setText(sendTime);
                    holderLeft.tv_time.setVisibility(View.VISIBLE);
                }
                holderRight.tv_user.setText(bean.getUserName());
                holderRight.tv_msg.setText(bean.getMessage());
                x.image().bind(holderRight.img_head, bean.getHeadUrl(), options);
            default:
                break;
        }
        return convertView;
    }

    private boolean iswentmin(long msgtime, long lastmsgtime,int x) {
        Date sys = new Date(lastmsgtime);
        Date msg = new Date(msgtime);
        long went = sys.getTime() - msg.getTime();
//        Toast.makeText(context,String.valueOf(went),Toast.LENGTH_SHORT).show();
        if (went > 1000 * 60 * x)//如果超过五分钟返回true
            return true;
        return false;
    }

    class ViewHolderLeft {
        @ViewInject(R.id.tv_msg_time_left)
        TextView tv_time;
        @ViewInject(R.id.tv_chat_user_left)
        TextView tv_user;
        @ViewInject(R.id.tv_chat_msg_left)
        TextView tv_msg;
        @ViewInject(R.id.img_chat_head_left)
        ImageView img_head;
    }

    class ViewHolderRight {
        @ViewInject(R.id.tv_msg_time_right)
        TextView tv_time;
        @ViewInject(R.id.tv_chat_user_right)
        TextView tv_user;
        @ViewInject(R.id.tv_chat_msg_right)
        TextView tv_msg;
        @ViewInject(R.id.img_chat_head_right)
        ImageView img_head;
    }
}
