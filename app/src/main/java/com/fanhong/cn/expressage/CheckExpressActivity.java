package com.fanhong.cn.expressage;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fanhong.cn.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by Administrator on 2017/8/9.
 */

@ContentView(R.layout.activity_check_express)
public class CheckExpressActivity extends Activity{
    @ViewInject(R.id.tv_title)
    private TextView title;
    @ViewInject(R.id.input_express_number)
    private EditText edtExpressNumber;
    @ViewInject(R.id.my_send_express)
    private TextView tvsend;
    @ViewInject(R.id.line_send_express)
    private View linesend;
    @ViewInject(R.id.my_get_express)
    private TextView tvget;
    @ViewInject(R.id.line_get_express)
    private View lineget;

    private FragmentManager fragmentManager = getFragmentManager();
    private FragmentTransaction transaction;
    private MysendexpressFragment sendfragment;
    private MygetexpressFragment getfragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        title.setText("查快递");
    }
    @Event({R.id.img_back,R.id.img_search_express,R.id.my_send_express,R.id.my_get_express})
    private void onClick(View v){
        switch (v.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.img_search_express:
                break;
            case R.id.my_send_express:
                linesend.setVisibility(View.VISIBLE);
                lineget.setVisibility(View.INVISIBLE);
                break;
            case R.id.my_get_express:
                linesend.setVisibility(View.INVISIBLE);
                lineget.setVisibility(View.VISIBLE);
                break;
        }
    }
}
