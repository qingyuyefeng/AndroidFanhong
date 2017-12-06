package com.fanhong.cn.party.activities;

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
 * Created by Administrator on 2017/11/24.
 */
@ContentView(R.layout.pmembers_info)
public class PartyMemberInfoActivity extends Activity {
    @ViewInject(R.id.btn_back)
    private ImageView back;
    @ViewInject(R.id.text_0)
    private TextView text0;
    @ViewInject(R.id.text_1)
    private TextView text1;
    @ViewInject(R.id.text_2)
    private TextView text2;
    @ViewInject(R.id.text_3)
    private TextView text3;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Event({R.id.btn_back,R.id.party_0,R.id.party_1,R.id.party_2,R.id.party_3})
    private void onClick(View v){
        Intent intent = new Intent(this,PartyMembersActivity.class);
        switch (v.getId()){
            case R.id.party_0:
                intent.putExtra("title",text0.getText().toString());
                intent.putExtra("type",0);
                startActivity(intent);
                break;
            case R.id.party_1:
                intent.putExtra("title",text1.getText().toString());
                intent.putExtra("type",1);
                startActivity(intent);
                break;
            case R.id.party_2:
                intent.putExtra("title",text2.getText().toString());
                intent.putExtra("type",2);
                startActivity(intent);
                break;
            case R.id.party_3:
                intent.putExtra("title",text3.getText().toString());
                intent.putExtra("type",3);
                startActivity(intent);
                break;
        }
    }
}
