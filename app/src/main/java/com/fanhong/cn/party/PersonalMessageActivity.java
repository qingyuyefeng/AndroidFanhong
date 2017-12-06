package com.fanhong.cn.party;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanhong.cn.App;
import com.fanhong.cn.R;
import com.fanhong.cn.util.JsonSyncUtils;
import com.fanhong.cn.util.MySharedPrefUtils;
import com.zhy.autolayout.AutoRelativeLayout;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by Administrator on 2017/11/7.
 */
@ContentView(R.layout.party_wd_activity)
public class PersonalMessageActivity extends Activity {
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
    @Event({R.id.img_back})
    private void onClick(View v){
        finish();
    }
    private void initViews(){
        title.setText("个人信息");

//        name.setText(MySharedPrefUtils.getNick(this));
//        position.setText();
        phone.setText(MySharedPrefUtils.getPhone(this));
        RequestParams params = new RequestParams(App.CMDURL);
        params.addBodyParameter("cmd","109");
        params.addBodyParameter("uid",MySharedPrefUtils.getUserId(this));
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if(JsonSyncUtils.getJsonValue(result,"cmd").equals("110")){
                    final String nameStr = JsonSyncUtils.getJsonValue(result,"name");
                    final String posiStr = JsonSyncUtils.getJsonValue(result,"post");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            name.setText(nameStr);
                            position.setText(posiStr);
                        }
                    });
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
}
