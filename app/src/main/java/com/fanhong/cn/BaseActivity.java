package com.fanhong.cn;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public abstract class BaseActivity extends Activity implements View.OnClickListener {
    protected Bundle savedInstanceState;
    public Context context;

    protected boolean isNoTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        context = this;

        setContentView();
        initData();
        initTitle();
        initView();
        initListener();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    protected void setTitle(String text) {
        if (!isNoTitle) {
            TextView tv_title = (TextView) findViewById(R.id.tv_title);
            tv_title.setText(text);
        }
    }

    private void initTitle() {

    }

    public abstract void setContentView();

    public abstract void initView();

    public abstract void initListener();

    public abstract void initData();
}

