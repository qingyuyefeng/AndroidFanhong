package com.fanhong.cn.fenxiao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanhong.cn.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by Administrator on 2017/8/8.
 */

@ContentView(R.layout.activity_fenxiao_information)
public class InformationActivity extends Activity{
    @ViewInject(R.id.back_bn)
    private ImageView backBtn;
    @ViewInject(R.id.tv_post_information)
    private TextView postInformation;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
    }
    @Event(value = {R.id.back_bn,R.id.tv_post_information})
    private void onClick(View v){
        switch (v.getId()){
            case R.id.back_bn:
                finish();
                break;
            case R.id.tv_post_information:
                startActivity(new Intent(this,PostSuccessActivity.class));
                break;
        }
    }
}
