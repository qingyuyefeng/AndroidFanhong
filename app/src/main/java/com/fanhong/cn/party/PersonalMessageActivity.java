package com.fanhong.cn.party;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.fanhong.cn.R;
import com.fanhong.cn.util.MySharedPrefUtils;
import com.zhy.autolayout.AutoRelativeLayout;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by Administrator on 2017/11/7.
 */
@ContentView(R.layout.party_wd_activity)
public class PersonalMessageActivity extends Activity {
    @ViewInject(R.id.layout_title_)
    private AutoRelativeLayout titleLayout;
    @ViewInject(R.id.tv_title)
    private TextView title;
    @ViewInject(R.id.party_user_name)
    private TextView name;
    @ViewInject(R.id.party_user_position)
    private TextView position;
    @ViewInject(R.id.party_user_phone)
    private TextView phone;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        initViews();
    }
    private void initViews(){
        titleLayout.setBackgroundColor(getResources().getColor(R.color.white));
        title.setText("个人信息");
        name.setText(MySharedPrefUtils.getNick(this));
//        position.setText();
        phone.setText(MySharedPrefUtils.getPhone(this));
    }
}
