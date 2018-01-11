package com.fanhong.cn.party;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.fanhong.cn.App;
import com.fanhong.cn.R;
import com.fanhong.cn.util.JsonSyncUtils;
import com.fanhong.cn.util.MySharedPrefUtils;

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
    @ViewInject(R.id.party_user_sex)
    private TextView sex;
    @ViewInject(R.id.party_user_nationality)
    private TextView nationality;
    @ViewInject(R.id.party_user_age)
    private TextView age;
    @ViewInject(R.id.party_user_birthday)
    private TextView birthday;
    @ViewInject(R.id.party_user_idcardnumber)
    private TextView idcardnumber;
    @ViewInject(R.id.party_user_phonenumber)
    private TextView phone;
    @ViewInject(R.id.party_user_position)
    private TextView position;
    @ViewInject(R.id.party_user_education)
    private TextView education;
    @ViewInject(R.id.party_user_address)
    private TextView address;
    @ViewInject(R.id.party_user_jointime)
    private TextView jointime;
    @ViewInject(R.id.party_user_positivetime)
    private TextView positivetime;
    @ViewInject(R.id.party_user_whichbranch)
    private TextView whichbranch;

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
//        phone.setText(MySharedPrefUtils.getPhone(this));
        RequestParams params = new RequestParams(App.CMDURL);
        params.addBodyParameter("cmd","109");
        params.addBodyParameter("uid",MySharedPrefUtils.getUserId(this));
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if(JsonSyncUtils.getJsonValue(result,"cmd").equals("110")){
                    String data = JsonSyncUtils.getJsonValue(result,"data");
                    final String nameStr = JsonSyncUtils.getJsonValue(data,"name");
                    final String sexStr = JsonSyncUtils.getJsonValue(data,"sex");
                    final String nationStr = JsonSyncUtils.getJsonValue(data,"nation");
                    final String ageStr = JsonSyncUtils.getJsonValue(data,"age");
                    final String birthdayStr = JsonSyncUtils.getJsonValue(data,"birthday");
                    final String idcardStr = JsonSyncUtils.getJsonValue(data,"idcard");
                    final String educationStr = JsonSyncUtils.getJsonValue(data,"education");
                    final String jointimeStr = JsonSyncUtils.getJsonValue(data,"jointime");
                    final String positivetimeStr = JsonSyncUtils.getJsonValue(data,"turnovertime");
                    final String addressStr = JsonSyncUtils.getJsonValue(data,"address");
                    final String phoneStr = JsonSyncUtils.getJsonValue(data,"tel");
                    final String positionStr = JsonSyncUtils.getJsonValue(data,"post");
                    final String whichbranchStr = JsonSyncUtils.getJsonValue(data,"zhibu");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            name.setText(nameStr);
                            sex.setText(sexStr);
                            nationality.setText(nationStr);
                            age.setText(ageStr);
                            birthday.setText(birthdayStr);
                            idcardnumber.setText(idcardStr);
                            phone.setText(phoneStr);
                            position.setText(positionStr);
                            education.setText(educationStr);
                            address.setText(addressStr);
                            jointime.setText(jointimeStr);
                            positivetime.setText(positivetimeStr);
                            whichbranch.setText(whichbranchStr);
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
