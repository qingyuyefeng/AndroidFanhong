package com.fanhong.cn.view;

import com.fanhong.cn.FragmentMainActivity;

import android.app.Fragment;

public class BaseFragment extends Fragment {

    //获取装载整个ViewPager容器的对象
    protected FragmentMainActivity getBaseActivity() {
        return (FragmentMainActivity) getActivity();
    }
    
    public synchronized void setFragment(int type,String str) {}
    
    public synchronized void connectResult(int cmd,String str) {}
}
