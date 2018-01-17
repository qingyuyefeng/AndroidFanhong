package com.fanhong.cn.party;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fanhong.cn.App;
import com.fanhong.cn.ImageLoaderPicture;
import com.fanhong.cn.R;
import com.fanhong.cn.SampleConnection;
import com.fanhong.cn.party.activities.PartyDuesActivity;
import com.fanhong.cn.party.activities.PartyMemberInfoActivity;
import com.fanhong.cn.party.activities.PartyScoreActivity;
import com.fanhong.cn.util.JsonSyncUtils;
import com.fanhong.cn.util.MySharedPrefUtils;
import com.fanhong.cn.view.CircleImg;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.zhy.autolayout.AutoLinearLayout;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
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
    private AutoLinearLayout userMessage;
    @ViewInject(R.id.user_score)
    private AutoLinearLayout userScore;
    @ViewInject(R.id.tv_score)
    private TextView tvscore;
//    @ViewInject(R.id.user_notification)
//    private AutoLinearLayout userNotice;
    @ViewInject(R.id.view_devider)
    private View devider;
    @ViewInject(R.id.party_person_info)
    private AutoLinearLayout userInfo;
    @ViewInject(R.id.dues_message)
    private AutoLinearLayout userDues;
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
        getScore(MySharedPrefUtils.getUserId(getActivity()));
//        checkmember(MySharedPrefUtils.getPhone(getActivity()));
        return view;
    }
    @Event({R.id.user_message,/*R.id.user_score,R.id.user_notification,*/R.id.party_person_info,R.id.dues_message})
    private void onClick(View v){
        switch (v.getId()){
            case R.id.user_message: //个人信息
                startActivity(new Intent(getActivity(),PersonalMessageActivity.class));
                break;
//            case R.id.user_notification: //消息通知
//                break;
//            case R.id.user_score:  //积分
//                startActivity(new Intent(getActivity(), PartyScoreActivity.class));
//                break;
            case R.id.party_person_info:  //党员信息
                startActivity(new Intent(getActivity(), PartyMemberInfoActivity.class));
                break;
            case R.id.dues_message: //党费信息
                startActivity(new Intent(getActivity(), PartyDuesActivity.class));
                break;
        }
    }

    private void getScore(String uid){
        RequestParams params = new RequestParams(App.CMDURL);
        params.addBodyParameter("cmd","105");
        params.addBodyParameter("uid",uid);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if(JsonSyncUtils.getJsonValue(result,"cmd").equals("106")){
                    String score = JsonSyncUtils.getJsonValue(result,"fen");
                    handler.sendMessage(handler.obtainMessage(13,score));
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void checkmember(String phonenumber){
        RequestParams params = new RequestParams(App.CMDURL);
        params.addBodyParameter("cmd","135");
        params.addBodyParameter("tel",phonenumber);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if(JsonSyncUtils.getJsonValue(result,"cw").equals("0")){
                    //有权限
                    handler.sendEmptyMessage(11);
                }else {
                    //没权限
                    handler.sendEmptyMessage(12);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 11:
                    devider.setVisibility(View.VISIBLE);
                    userInfo.setVisibility(View.VISIBLE);
                    break;
                case 12:
                    devider.setVisibility(View.GONE);
                    userInfo.setVisibility(View.GONE);
                    break;
                case 13:
                    String s = (String) msg.obj;
                    tvscore.setText(s);
                    break;
            }
        }
    };
}
