package com.fanhong.cn.fenxiao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.fanhong.cn.FragmentMainActivity;
import com.fanhong.cn.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by Administrator on 2017/8/8.
 */

@ContentView(R.layout.activity_fenxiao_successpost)
public class PostSuccessActivity extends Activity{
    @ViewInject(R.id.tv_tips)
    TextView tv_tips;
    @ViewInject(R.id.tv_label)
    TextView tv_label;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        if (getIntent().getBooleanExtra("fromVerification",false)){
            tv_tips.setText("预约成功");
            tv_label.setVisibility(View.GONE);
        }
    }
    @Event(R.id.tv_back_homepage)
    private void onClick(View v){
        if(v.getId() == R.id.tv_back_homepage){
            Intent intent = new Intent();
            intent.setClass(this, FragmentMainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}
