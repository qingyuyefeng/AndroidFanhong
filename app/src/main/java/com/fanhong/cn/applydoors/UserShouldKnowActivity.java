package com.fanhong.cn.applydoors;

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
 * Created by Administrator on 2017/9/4.
 */
@ContentView(R.layout.activity_userknow)
public class UserShouldKnowActivity extends Activity{
    @ViewInject(R.id.tv_title)
    private TextView title;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        title.setText("用户须知");
    }
    @Event({R.id.img_back,R.id.receive_xuzhi,R.id.refuse_xuzhi})
    private void onClick(View v){
        switch (v.getId()){
            case R.id.img_back:
               goBack();
                break;
            case R.id.receive_xuzhi:
               goBack();
                break;
            case R.id.refuse_xuzhi:
                Intent intent = new Intent();
                intent.putExtra("ifreceive",false);
                setResult(17,intent);
                finish();
                break;
        }
    }
    private void goBack(){
        Intent intent = new Intent();
        intent.putExtra("ifreceive",true);
        setResult(17,intent);
        finish();
    }
}
