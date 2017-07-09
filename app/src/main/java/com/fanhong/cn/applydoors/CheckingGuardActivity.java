package com.fanhong.cn.applydoors;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.fanhong.cn.FragmentMainActivity;
import com.fanhong.cn.R;

/**
 * Created by Administrator on 2017/6/12.
 */

public class CheckingGuardActivity extends Activity{
    private ImageView backBtn;
    private Button backMainpage;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.applydoors_to_check);
        init();
    }
    private void init(){
        backBtn = (ImageView) findViewById(R.id.checking_backbtn);
        backMainpage = (Button) findViewById(R.id.back_to_mainpage);

        backBtn.setOnClickListener(ocl);
        backMainpage.setOnClickListener(ocl);
    }
    View.OnClickListener ocl = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.checking_backbtn:
                    finish();
                    break;
                case R.id.back_to_mainpage:
                    startActivity(new Intent(CheckingGuardActivity.this, FragmentMainActivity.class));
                    break;
            }
        }
    };
}
