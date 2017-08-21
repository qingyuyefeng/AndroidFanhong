package com.fanhong.cn.repair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanhong.cn.FragmentMainActivity;
import com.fanhong.cn.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by Administrator on 2017/8/21.
 */
@ContentView(R.layout.activity_repair_success)
public class RepairSuccessActivity extends Activity {
    @ViewInject(R.id.tv_title)
    TextView title;
    @ViewInject(R.id.img_back)
    ImageView back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        title.setText("提交成功");
        back.setVisibility(View.INVISIBLE);
    }

    @Event(R.id.btn_back)
    private void onBack(View v) {
        Intent i = new Intent(this, FragmentMainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        onBack(findViewById(R.id.btn_back));
    }
}
