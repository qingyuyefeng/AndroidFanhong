package com.fanhong.cn.community;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.fanhong.cn.FragmentMainActivity;
import com.fanhong.cn.R;
import com.fanhong.cn.view.BaseFragment;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;


/**
 * Created by Administrator on 2017/6/30.
 */
@ContentView(R.layout.fragment_community_im)
public class CommunityIndexFragment extends BaseFragment {
    @ViewInject(R.id.rg_title_community)
    RadioGroup rg_tab;
    @ViewInject(R.id.tv_back)
    ImageView tv_back;
    @ViewInject(R.id.tv_community_)
    TextView tv_communityName;

    FragmentManager fragmentManager;
    FragmentMainActivity activity;
    SharedPreferences pref;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = x.view().inject(this, inflater, container);

        fragmentManager = getFragmentManager();
        activity = getBaseActivity();
        pref = getActivity().getSharedPreferences("Setting", Context.MODE_PRIVATE);
        tv_back.setVisibility(View.GONE);
        try {
            String str = pref.getString("gardenName", "");
            if (str.length() > 0)
                tv_communityName.setText(str);
        } catch (Exception e) {
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = new CommunityNewsFragment();
        transaction.replace(R.id.framelayout_community, fragment);
        transaction.commitAllowingStateLoss();
        rg_tab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.title_rb2) {
                    startActivityForResult(new Intent(getBaseActivity(), CommunityChatRoomActivity.class), 30);
                    rg_tab.check(R.id.title_rb1);
                }
            }
        });
        return view;
    }

//    @Event(value = R.id.rg_title_community, type = RadioGroup.OnCheckedChangeListener.class)
//    private void onTabChecked(RadioGroup group, int checkedId) {
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        Fragment fragment = getInstanceByIndex(checkedId);
//        transaction.replace(R.id.framelayout_community, fragment);
//        transaction.commit();
//    }
//
//    private Fragment getInstanceByIndex(int checkedId) {
//        Fragment fragment = null;
//        switch (checkedId) {
//            case R.id.title_rb1:
//                fragment = new CommunityNewsFragment();
//                tv_back.setVisibility(View.INVISIBLE);
//                activity.hiddenBottom(false);
//                break;
//            case R.id.title_rb2:
//                fragment = new CommunityChatRoomActivity();
//                tv_back.setVisibility(View.VISIBLE);
//                activity.hiddenBottom(true);
//                break;
//        }
//        return fragment;
//    }

    public void setFragment(String communityName) {
        tv_communityName.setText(communityName);
    }

}
