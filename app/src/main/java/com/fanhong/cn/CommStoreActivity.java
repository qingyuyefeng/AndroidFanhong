package com.fanhong.cn;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import com.fanhong.cn.view.CommStoreSellFragment;
import com.fanhong.cn.view.CommStoreShopFragment;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;


@ContentView(R.layout.activity_comm_store)
public class CommStoreActivity extends Activity {
    @ViewInject(R.id.rg_title_community_store)
            RadioGroup rg_tab;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        fragmentManager = getFragmentManager();
        rg_tab.check(R.id.title_rb1);
    }

    @Event(value = {R.id.img_back, R.id.img_search})
    private void onClicks(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.img_search:
                break;
        }
    }

    @Event(value = R.id.rg_title_community_store, type = RadioGroup.OnCheckedChangeListener.class)
    private void onTabChecked(RadioGroup group, int checkedId) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = getInstanceByIndex(checkedId);
        transaction.replace(R.id.framelayout_comm_store, fragment);
        transaction.commit();
    }

    private Fragment getInstanceByIndex(int checkedId) {
        Fragment fragment = null;
        switch (checkedId) {
            case R.id.title_rb1:
                fragment = new CommStoreShopFragment();
                break;
            case R.id.title_rb2:
                fragment = new CommStoreSellFragment();
                break;
        }
        return fragment;
    }
}
