package com.fanhong.cn.party;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fanhong.cn.ImageLoaderPicture;
import com.fanhong.cn.R;
import com.fanhong.cn.SampleConnection;
import com.fanhong.cn.util.MySharedPrefUtils;
import com.fanhong.cn.view.CircleImg;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.zhy.autolayout.AutoLinearLayout;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by Administrator on 2017/11/3.
 */
@ContentView(R.layout.fragment_wd)
public class Fragmentwd extends Fragment {
    @ViewInject(R.id.user_picture)
    private CircleImg userPicture;
    @ViewInject(R.id.user_name)
    private TextView userName;
    @ViewInject(R.id.user_phone)
    private TextView userPhone;
    @ViewInject(R.id.user_message)
    private AutoLinearLayout userMessage;  //个人信息
    @ViewInject(R.id.user_score)
    private AutoLinearLayout userScore;  //积分
    @ViewInject(R.id.user_notification)
    private AutoLinearLayout userNotice;  //消息通知
    @ViewInject(R.id.party_person_info)
    private AutoLinearLayout userInfo;  //党员信息
    @ViewInject(R.id.dues_message)
    private AutoLinearLayout userDues;  //党费信息
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = x.view().inject(this,inflater,container);
        if(!TextUtils.isEmpty(SampleConnection.LOGO_URL)){
            String ul = SampleConnection.LOGO_URL;
            //用ImageLoader加载图片
            ImageLoader.getInstance().displayImage(ul, userPicture,new ImageLoaderPicture(this.getActivity()).getOptions(),new SimpleImageLoadingListener());
        }else {
            userPicture.setImageResource(R.drawable.default_photo);
        }
        userName.setText(MySharedPrefUtils.getNick(getActivity()));
        userPhone.setText(MySharedPrefUtils.getPhone(getActivity()));
        return view;
    }
    @Event({R.id.user_message,R.id.user_score})
    private void onClick(View v){
        switch (v.getId()){
            case R.id.user_message:
                startActivity(new Intent(getActivity(),PersonalMessageActivity.class));
                break;
        }
    }
}
