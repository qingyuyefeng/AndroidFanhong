package com.fanhong.cn.expressage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.fanhong.cn.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by Administrator on 2017/8/9.
 */

@ContentView(R.layout.activity_express)
public class ExpressHomeActivity extends Activity{
    @ViewInject(R.id.tv_title)
    private TextView titleTop;
    @ViewInject(R.id.agree_sheet_protocol)
    private CheckBox agreeBox;
    @ViewInject(R.id.sheet_protocol)
    private TextView sheetText;

    private boolean ifAgree = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        titleTop.setText(R.string.daifa_express);
        sheetText.setText("《代发快递须知》");
        agreeBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    ifAgree = true;
                }else {
                    ifAgree = false;
                }
            }
        });
    }
    @Event({R.id.img_back,R.id.send_expressage,R.id.check_expressage,R.id.expressage_order,R.id.net_phone,R.id.sheet_protocol})
    private void onClick(View v){
        switch (v.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.send_expressage:
                startActivity(new Intent(this,SendExpressActivity.class));
                break;
            case R.id.check_expressage:
                startActivity(new Intent(this,CheckExpressActivity.class));
                break;
            case R.id.expressage_order:
                startActivity(new Intent(this,ExpressOrderActivity.class));
                break;
            case R.id.net_phone:
                startActivity(new Intent(this,NetphoneActivity.class));
                break;
            case R.id.sheet_protocol:
                createDialog();
                break;
        }
    }
    private void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.layout_dialog_sheet,null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Button agreeSheet = (Button) view.findViewById(R.id.read_and_agree);
        TextView textView = (TextView) view.findViewById(R.id.tv_content);
        textView.setText(R.string.daifaxuzhi);
        agreeSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                agreeBox.setChecked(true);
            }
        });
    }
}
