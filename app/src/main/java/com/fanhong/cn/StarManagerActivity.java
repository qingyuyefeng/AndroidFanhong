package com.fanhong.cn;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fanhong.cn.util.JsonSyncUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by Administrator on 2017/12/14.
 */
@ContentView(R.layout.activity_star_manager)
public class StarManagerActivity extends Activity {
    @ViewInject(R.id.tv_title)
    TextView tv_title;
    @ViewInject(R.id.label_description)
    TextView labelDescription;
    @ViewInject(R.id.tv_description)
    TextView tvDescription;
    @ViewInject(R.id.img_description)
    ImageView imgDescription;
    @ViewInject(R.id.label_nearby)
    TextView labelNearby;
    @ViewInject(R.id.tv_nearby)
    TextView tvNearby;
    @ViewInject(R.id.label_price)
    TextView labelPrice;
    @ViewInject(R.id.label_call)
    TextView labelCall;
    private String managerId, managerName, managerTel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        managerName = getIntent().getStringExtra("name");
        managerId = getIntent().getStringExtra("id");
        initViews();
        initDatas();
    }

    private void initViews() {
        tv_title.setText("物业之星");
        imgDescription.setVisibility(View.GONE);
        //字体加粗
        labelDescription.getPaint().setFakeBoldText(true);
        labelNearby.getPaint().setFakeBoldText(true);
        labelPrice.getPaint().setFakeBoldText(true);
        labelCall.getPaint().setFakeBoldText(true);
    }

    private void initDatas() {
        RequestParams param = new RequestParams(App.CMDURL);
        param.addParameter("cmd", "81");
        param.addParameter("id", managerId);
        x.http().post(param, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (JsonSyncUtils.getJsonValue(result, "cw").equals("0")) {
                    managerTel = JsonSyncUtils.getJsonValue(result, "tel");
                    String description = JsonSyncUtils.getJsonValue(result, "describe");
                    String imgUrl = JsonSyncUtils.getJsonValue(result, "img");
                    String nearby = JsonSyncUtils.getJsonValue(result, "periphery");
                    String price = JsonSyncUtils.getJsonValue(result, "price");
                    Log.e("imgtest", imgUrl);
                    if (!imgUrl.equals("-1")) {
                        ImageOptions option=new ImageOptions.Builder().setLoadingDrawableId(R.drawable.img_default).setFailureDrawableId(R.drawable.img_default).setUseMemCache(true).build();
                        x.image().bind(imgDescription, imgUrl,option);
                        imgDescription.setVisibility(View.VISIBLE);
                    }
                    tvDescription.setText(Html.fromHtml(description));
                    tvNearby.setText(Html.fromHtml(nearby));
                    labelPrice.setText("物管费：" + price + "元/平方米");
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

    @Event({R.id.img_back, R.id.layout_btn_call})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.layout_btn_call:
                if (managerTel == null || managerTel.equals("-1")) {
                    new AlertDialog.Builder(this).setMessage("这个小区还没有录入电话哦！").setNegativeButton("我知道了", null).show();
                    break;
                }
                new AlertDialog.Builder(this).setTitle(managerName).setMessage("联系电话:" + managerTel)
                        .setPositiveButton("立即拨打", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //判断Android版本是否大于23
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    int checkCallPhonePermission = ContextCompat.checkSelfPermission(StarManagerActivity.this, Manifest.permission.CALL_PHONE);

                                    if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(StarManagerActivity.this, new String[]{Manifest.permission.CALL_PHONE},
                                                11);
                                        return;
                                    }
                                }
                                callManager();
                            }
                        }).setNegativeButton("取消", null).create().show();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 11)
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callManager();
            } else
                Toast.makeText(this, "需要通话权限", Toast.LENGTH_SHORT).show();
    }

    public void callManager() {
        Intent i = new Intent(Intent.ACTION_CALL);
        Uri uri = Uri.parse("tel:" + managerTel);
        i.setData(uri);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(i);
    }
}
