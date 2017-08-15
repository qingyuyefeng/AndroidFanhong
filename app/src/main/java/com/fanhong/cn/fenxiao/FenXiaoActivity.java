package com.fanhong.cn.fenxiao;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.fanhong.cn.App;
import com.fanhong.cn.LoginActivity;
import com.fanhong.cn.R;
import com.fanhong.cn.util.JsonSyncUtils;
import com.fanhong.cn.view.HomeView1;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by Administrator on 2017/8/8.
 */
@ContentView(R.layout.activity_fenxiao)
public class FenXiaoActivity extends Activity {
    @ViewInject(R.id.scroll_fenxiao)
    private ScrollView scrollView;
    @ViewInject(R.id.iv_fenxiao1)
    private ImageView distribution1;
    @ViewInject(R.id.iv_fenxiao2)
    private ImageView distribution2;
    @ViewInject(R.id.tv_lijicanyu)
    private TextView joinIn;

    private SharedPreferences mSharedPref;
    private String uid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        mSharedPref = getSharedPreferences("Setting", Context.MODE_PRIVATE);
        uid = mSharedPref.getString("UserId","");

        setImage(distribution1, 720, 1139);
        setImage(distribution2, 540, 858);
    }

    @Event(value = {R.id.back_button, R.id.iv_fenxiao1 ,R.id.iv_fenxiao2, R.id.tv_lijicanyu})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_button:
                finish();
                break;
            case R.id.iv_fenxiao1:
                scrollView.smoothScrollTo(0, distribution2.getTop());
                break;
            case R.id.iv_fenxiao2:
                scrollView.smoothScrollTo(0, joinIn.getTop());
                break;
            case R.id.tv_lijicanyu:
                if(isLogined() == 1){
                    checkIfjoined(uid);
                }else {
                    createDialog();
                }
                break;
        }
    }
    private int isLogined() {
        int result = 0;
        try {
            result = mSharedPref.getInt("Status", 0);
        } catch (Exception e) {
        }
        return result;
    }
    private void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("你还没有登录哦");
        builder.setMessage("是否立即登录？");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(FenXiaoActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
//        builder.setCancelable(true);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode){
            case 21://登录成功返回
//                Toast.makeText(this,"登录成功！",Toast.LENGTH_SHORT).show();
                break;
            case 22://未登录直接返回
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void checkIfjoined(String str) {
        RequestParams params = new RequestParams(App.CMDURL);
        params.addBodyParameter("cmd", "69");
        params.addBodyParameter("uid", str);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                String data = JsonSyncUtils.getJsonValue(s, "data");
//                Log.i("xq","data==>"+data);
                if (data.equals("0")) {
                    Message msg = new Message();
                    msg.what = 0;
                    handler.sendMessage(msg);
                } else if (data.equals("1")) {
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                } else {
                    Message msg = new Message();
                    msg.what = 2;
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    startActivity(new Intent(FenXiaoActivity.this, InformationActivity.class));
                    break;
                case 1:
                    Toast.makeText(FenXiaoActivity.this, "你已注册过此系统", Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        }
    });

    private void setImage(ImageView iv, int wd, int ht) {
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int height = ht * screenWidth / wd;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth, height);
        params.setMargins(0, 0, 0, 0);
        iv.setLayoutParams(params);
        iv.setPadding(0, 0, 0, 0);
    }
}
