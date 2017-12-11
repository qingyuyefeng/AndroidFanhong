package com.fanhong.cn.party;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fanhong.cn.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by Administrator on 2017/11/3.
 */
@ContentView(R.layout.fragment_cy)
public class Fragmentcy extends Fragment {
    @ViewInject(R.id.new_active)
    private ImageView active;
    @ViewInject(R.id.join_test)
    private ImageView jointest;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = x.view().inject(this,inflater,container);
        return view;
    }
    @Event({R.id.new_active,R.id.join_test})
    private void onClick(View v){
        switch (v.getId()){
            case R.id.new_active:
                break;
            case R.id.join_test:
                break;
        }
    }
}
