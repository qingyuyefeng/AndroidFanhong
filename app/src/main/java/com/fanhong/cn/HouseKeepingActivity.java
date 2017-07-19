package com.fanhong.cn;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import com.fanhong.cn.view.CommunityNewsFragment;
import com.fanhong.cn.view.HouseKeepingClassifyFragment;
import com.fanhong.cn.view.HouseKeepingRecommitFragment;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import static com.fanhong.cn.R.id.tv_back;

@ContentView(R.layout.activity_house_keeping)
public class HouseKeepingActivity extends AppCompatActivity {
    @ViewInject(R.id.rg_housekeeping_home)
            private RadioGroup rg_home;

    FragmentManager fragmentManager;
    FragmentMainActivity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        rg_home.check(R.id.rb_hk_recommend);
    }

    @Event(value = R.id.rg_housekeeping_home, type = RadioGroup.OnCheckedChangeListener.class)
    private void onTabChecked(RadioGroup group, int checkedId) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = getInstanceByIndex(checkedId);
        transaction.replace(R.id.layout_housekeeping_fragment, fragment);
        transaction.commitAllowingStateLoss();
    }
    @Event(R.id.tv_back)
    private void onBackClick(View v){
        finish();
    }

    private Fragment getInstanceByIndex(int checkedId) {
        Fragment fragment = null;
        switch (checkedId) {
            case R.id.rb_hk_recommend:
                fragment = new HouseKeepingRecommitFragment();
                break;
            case R.id.rb_hk_classify:
                fragment = new HouseKeepingClassifyFragment();
                break;
        }
        return fragment;
    }
}
