package com.fanhong.cn.party;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.fanhong.cn.R;
import com.fanhong.cn.party.models.LtItemModel;
import com.zhy.autolayout.AutoLinearLayout;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/3.
 */
@ContentView(R.layout.fragment_lt)
public class Fragmentlt extends Fragment {
    @ViewInject(R.id.lt_layout)
    private AutoLinearLayout ltlayout;
    @ViewInject(R.id.lt_recyclerview)
    private RecyclerView recyclerView;
    @ViewInject(R.id.lt_get_more)
    private TextView getMore;
    @ViewInject(R.id.lt_empty)
    private TextView ltEmpty;
    @ViewInject(R.id.lt_edit)
    private EditText ltEdit;
    @ViewInject(R.id.lt_submit)
    private TextView ltSubmit;

    private List<LtItemModel> list = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = x.view().inject(this,inflater,container);
        return view;
    }
    private void initViews(){

    }
    @Event({R.id.lt_get_more,R.id.lt_submit})
    private void onClick(View v){
        switch (v.getId()){
            case R.id.lt_get_more:
                break;
            case R.id.lt_submit:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(list.size()>0){
            handler.sendEmptyMessage(2);
        }else
            handler.sendEmptyMessage(1);
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    //没有数据时
                    ltlayout.setVisibility(View.GONE);
                    ltEmpty.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    ltlayout.setVisibility(View.VISIBLE);
                    ltEmpty.setVisibility(View.GONE);
                    break;
            }
        }
    };
}
