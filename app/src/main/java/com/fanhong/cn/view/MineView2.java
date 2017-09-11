package com.fanhong.cn.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fanhong.cn.AboutActivity;
import com.fanhong.cn.AccountSettingsActivity;
import com.fanhong.cn.GeneralSettingsActivity;
import com.fanhong.cn.ImageLoaderPicture;
import com.fanhong.cn.LoginActivity;
import com.fanhong.cn.MyTradeNoActivity;
import com.fanhong.cn.NoticeActivity;
import com.fanhong.cn.R;
import com.fanhong.cn.SampleConnection;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.zhy.autolayout.AutoLinearLayout;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by Administrator on 2017/9/11.
 */
@ContentView(R.layout.fragment_mine1)
public class MineView2 extends BaseFragment {
    @ViewInject(R.id.mine_photo)
    private CircleImg userPhoto;
    @ViewInject(R.id.user_name)
    private TextView userName;
    @ViewInject(R.id.account_setting)
    private AutoLinearLayout acSetup;
    @ViewInject(R.id.news_notice)
    private AutoLinearLayout newsNotice;
    @ViewInject(R.id.my_order)
    private AutoLinearLayout myOrder;
    @ViewInject(R.id.customer_hotline)
    private AutoLinearLayout callHotline;
    @ViewInject(R.id.tv_hotline)
    private TextView hotlineNumber;
    @ViewInject(R.id.general_setup)
    private AutoLinearLayout generalSetup;
    @ViewInject(R.id.about_us)
    private AutoLinearLayout aboutUs;

    private SharedPreferences mSettingPref;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = x.view().inject(this, inflater, container);
        init();
        return view;
    }

    private void init() {
        mSettingPref = getActivity().getSharedPreferences("Setting", Context.MODE_PRIVATE);
    }

    @Event({R.id.mine_photo, R.id.user_name, R.id.account_setting, R.id.news_notice,
            R.id.my_order, R.id.customer_hotline, R.id.general_setup, R.id.about_us})
    private void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.mine_photo:
                if (isLogined() == 0) {
                    intent.setClass(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, 0);
                }
                break;
            case R.id.user_name:
                break;
            case R.id.account_setting:
                intent.setClass(getActivity(), AccountSettingsActivity.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.news_notice:
                intent.setClass(getActivity(), NoticeActivity.class);
                startActivity(intent);
                break;
            case R.id.my_order:
                intent.setClass(getActivity(), MyTradeNoActivity.class);
                startActivity(intent);
                break;
            case R.id.customer_hotline:
                //判断Android版本是否大于23
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int checkCallPhonePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);

                    if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE},
                                11);
                        return;
                    }
                }
                String call = hotlineNumber.getText().toString();
                showDialog(call);
                break;
            case R.id.general_setup:
                intent.setClass(getActivity(), GeneralSettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.about_us:
                intent.setClass(getActivity(), AboutActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
    public synchronized void setFragment(int type,int k) {
        switch(type)
        {
            case 1:
                getUser(k);
                break;
        }
    }
    @Override
    public void onResume() {
        getUser(isLogined());
        super.onResume();
    }

    public void getUser(int status){
        if(status == 1){
            String str1 = "";
            try{
                str1 = mSettingPref.getString("Nick", "");
            }catch (Exception e) {}
            if(str1 == null || str1.length()==0){
                try{
                    str1 = mSettingPref.getString("Name", "");
                }catch (Exception e) {}
            }
            userName.setText(str1);

            if(SampleConnection.LOGO_URL != null && SampleConnection.LOGO_URL.length() > 0){
                String ul = SampleConnection.LOGO_URL;
                //用ImageLoader加载图片
                ImageLoader.getInstance().displayImage(ul, userPhoto,new ImageLoaderPicture(this.getActivity()).getOptions(),new SimpleImageLoadingListener());
            }
        }else{
            userName.setText(getString(R.string.keylogin));
            userPhoto.setImageResource(R.drawable.mine_photo);
        }
    }

    private int isLogined() {
        int result = 0;
        try {
            result = mSettingPref.getInt("Status", 0);
        } catch (Exception e) {
        }
        return result;
    }

    private void showDialog(final String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("将要拨打" + str);
        builder.setMessage("是否立即拨打？");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callNumber(str);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void callNumber(String num) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + num);
        intent.setData(data);
        startActivity(intent);
    }
}
