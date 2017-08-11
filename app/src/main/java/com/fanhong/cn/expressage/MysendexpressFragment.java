package com.fanhong.cn.expressage;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fanhong.cn.R;
import com.zhy.autolayout.AutoLinearLayout;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by Administrator on 2017/8/10.
 */
@ContentView(R.layout.fragment_mysend_express)
public class MysendexpressFragment extends Fragment{
    @ViewInject(R.id.my_send_ex_recyc)
    private RecyclerView mRecyclerView;
    @ViewInject(R.id.if_no_express)
    private AutoLinearLayout mLinearLayout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return x.view().inject(this,inflater,container);
    }
}
