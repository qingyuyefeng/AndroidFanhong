package com.fanhong.cn.applydoors;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fanhong.cn.AmapChooseGardenActivity;
import com.fanhong.cn.App;
import com.fanhong.cn.R;
import com.fanhong.cn.util.MySharedPrefUtils;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

@ContentView(R.layout.activity_doorfix)
public class DoorfixActivity extends AppCompatActivity {
    @ViewInject(R.id.tv_title)
    private TextView title;
    @ViewInject(R.id.fix_get_cells)
    private TextView chooseCell;
    @ViewInject(R.id.fix_details)
    private EditText details;
    @ViewInject(R.id.fix_add_img)
    private ImageView addImage;

    private String cellId;
    private String imgurl1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        init();
    }

    private void init() {
        title.setText(R.string.service_door);
    }

    @Event({R.id.img_back,R.id.fix_get_cells,R.id.fix_add_img,R.id.fix_submit_btn})
    private void click(View v){
        switch (v.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.fix_get_cells:
                startActivityForResult(new Intent(this, AmapChooseGardenActivity.class),300);
                break;
            case R.id.fix_add_img:
                break;
            case R.id.fix_submit_btn:
                if(TextUtils.isEmpty(cellId)){
                    Toast.makeText(this,"请选择小区",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(details.getText().toString())){
                    Toast.makeText(this,"请描述楼栋和单元门位置等情况",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(imgurl1)){
                    Toast.makeText(this,"请至少上传一张照片作为参考",Toast.LENGTH_SHORT).show();
                    return;
                }
                submit();
                break;
        }
    }

    private void submit(){
        RequestParams params = new RequestParams(App.CMDURL);
    }
}
