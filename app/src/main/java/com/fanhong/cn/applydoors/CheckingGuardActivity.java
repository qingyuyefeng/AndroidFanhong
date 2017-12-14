package com.fanhong.cn.applydoors;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.fanhong.cn.R;

/**
 * Created by Administrator on 2017/6/12.
 */

public class CheckingGuardActivity extends Activity{
    private Button backMainpage;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.applydoors_to_check);
        init();
    }
    private void init(){
        backMainpage = (Button) findViewById(R.id.back_to_mainpage);

        backMainpage.setOnClickListener(ocl);
    }
    View.OnClickListener ocl = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.back_to_mainpage:
                    finish();
                    break;
            }
        }
    };
}
