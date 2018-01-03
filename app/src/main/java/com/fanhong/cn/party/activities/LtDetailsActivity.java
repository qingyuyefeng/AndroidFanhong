package com.fanhong.cn.party.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.fanhong.cn.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by Administrator on 2017/12/25.
 */
@ContentView(R.layout.lt_item_details)
public class LtDetailsActivity extends Activity{
    @ViewInject(R.id.tv_title)
    private TextView title;
    @ViewInject(R.id.lt_item_content)
    private TextView content;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        init();
    }
    private void init(){
        title.setText("内容详情");
        Intent intent = getIntent();
        String cont = intent.getStringExtra("content");
        content.setText(cont);
    }
    @Event(R.id.img_back)
    private void onClick(View v){
        finish();
    }
}
