package com.fanhong.cn;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sivin.Banner;
import com.sivin.BannerAdapter;

import org.xutils.common.Callback;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_comm_store_details)
public class CommStoreDetailsActivity extends Activity {
    //    @ViewInject(R.id.img_ms_detail_head)
//    ImageView img_head;
//    @ViewInject(R.id.tv_ms_detail_username)
//    TextView tv_uername;
//    @ViewInject(R.id.tv_ms_detail_send_time)
//    TextView tv_time;
    @ViewInject(R.id.tv_ms_detail_price)
    private TextView tv_price;
    @ViewInject(R.id.tv_ms_detail_title)
    private TextView tv_title;
        @ViewInject(R.id.img_ms_detail_goods)
    ImageView img_goods;
//    @ViewInject(R.id.banner_ms_detail_goods)
//    private Banner banner_goods;
    @ViewInject(R.id.lv_ms_detail_details)
    private TextView tv_details;
    @ViewInject(R.id.tv_ms_detail_senderaddr)
    private TextView tv_addr;
    @ViewInject(R.id.tv_ms_detail_phonenum)
    private TextView tv_phonenum;

    private Bundle bundle;
//    private List<String> images = new ArrayList<>();
//    private BannerAdapter bannerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        bundle = getIntent().getExtras();
        initDetails();
    }

    @Event(value = {R.id.img_back, R.id.btn_ms_detail_call})
    private void onclicks(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_ms_detail_call:
                showDialog(tv_phonenum.getText().toString());
                break;
        }
    }

    private void showDialog(final String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("将要拨打" + str);
        builder.setMessage("是否立即拨打？");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callNumber(str);
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

    private void callNumber(String num) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + num);
        intent.setData(data);
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
        startActivity(intent);
    }


    public void initDetails() {
        tv_title.setText(bundle.getString("title"));
        tv_details.setText(bundle.getString("detail"));
        tv_price.setText(bundle.getString("price"));
        tv_addr.setText(bundle.getString("user"));
        tv_phonenum.setText(bundle.getString("phone"));
        ImageOptions options = new ImageOptions.Builder().setLoadingDrawableId(R.drawable.img_default)
                        .setFailureDrawableId(R.drawable.img_default).setUseMemCache(true).build();
        x.image().bind(img_goods, bundle.getString("img"), options);
//        bannerAdapter=new BannerAdapter<String>(images) {
//            @Override
//            protected void bindTips(TextView tv, String s) {
//
//            }
//
//            @Override
//            public void bindImage(ImageView imageView, String s) {
//                x.image().bind(imageView, s,options);
//            }
//        };
//        banner_goods.setBannerAdapter(bannerAdapter);
//        images.add(bundle.getString("img"));
//        banner_goods.notifyDataHasChanged();
    }
//    String url="http://api.k780.com:88/?app=idcard.get";
//    Map<String,Object> map=new HashMap<>();
//        map.put("appkey", "10003");
//        map.put("sign", "b59bc3ef6191eb9f747dd4e83c99f2a4");
//        map.put("format", "json");
//        map.put("idcard", "110101199001011114");
//        XUtil.Post(url, map, new MyCallBack<PersonInfoBean>(){
//
//        @Override
//        public void onSuccess(PersonInfoBean result) {
//            super.onSuccess(result);
//            Log.e("result", result.toString());
//        }
//
//        @Override
//        public void onError(Throwable ex, boolean isOnCallback) {
//            super.onError(ex, isOnCallback);
//
//        }
//    });
}
