package com.fanhong.cn.repair;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fanhong.cn.App;
import com.fanhong.cn.R;
import com.fanhong.cn.util.FileUtil;
import com.fanhong.cn.util.JsonSyncUtils;
import com.fanhong.cn.util.StringUtils;
import com.zhy.autolayout.AutoLinearLayout;

import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.http.RequestParams;
import org.xutils.http.body.MultipartBody;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;

/**
 * Created by Administrator on 2017/8/21.
 */
@ContentView(R.layout.activity_repair)
public class RepairActivity extends Activity {
    @ViewInject(R.id.tv_title)
    private TextView tv_title;
    @ViewInject(R.id.edt_input_name)
    private EditText edtName;
    @ViewInject(R.id.edt_input_phone)
    private EditText edtPhone;
    //    @ViewInject(R.id.ctv_province)
    //    CheckedTextView ctvProvince;
    //    @ViewInject(R.id.ctv_city)
    //    CheckedTextView ctvCity;
    //    @ViewInject(R.id.ctv_area)
    //    CheckedTextView ctvArea;
    @ViewInject(R.id.edt_input_address)
    private EditText edtAddr;
    @ViewInject(R.id.edt_input_details)
    private EditText edtDetails;
    @ViewInject(R.id.layout_chosen)
    private AutoLinearLayout layout_img;
    @ViewInject(R.id.img_1)
    private ImageView img_1;
    @ViewInject(R.id.img_2)
    private ImageView img_2;
    @ViewInject(R.id.img_3)
    private ImageView img_3;
    @ViewInject(R.id.img_Add)
    private ImageView img_add;

    private boolean isinput = true;
    private Bundle bundle;
    private ArrayList<String> picPaths = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        tv_title.setText("上门维修");
        edtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isinput) {
                    isinput = false;
                    String text = s.toString().trim().replace("-", "");
                    if (text.length() > 0) {
                        if (text.charAt(0) == '1') {//以‘1’开头的说明是电话号码，否则认为是座机号码
                            text = StringUtils.addChar(3, text, '-');
                            edtPhone.setText(text);
                        } else {
                            text = StringUtils.addChar(text, '-');
                            edtPhone.setText(text);
                        }
                    }
                    edtPhone.setSelection(text.length());
                } else isinput = true;
            }
        });
    }

    @Event({R.id.img_back, R.id.ctv_province, R.id.ctv_city, R.id.ctv_area, R.id.btn_confirm, R.id.img_Add})
    private void onCLicks(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.ctv_province:
                break;
            case R.id.ctv_city:
                break;
            case R.id.ctv_area:
                break;
            case R.id.btn_confirm:
                if (getForms()) {
                    RequestParams params = new RequestParams(App.CMDURL);
                    List<KeyValue> body = new ArrayList<>();
                    body.add(new KeyValue("cmd", "1014"));
                    body.add(new KeyValue("name", bundle.getString("name")));
                    body.add(new KeyValue("phone", bundle.getString("phone")));
                    body.add(new KeyValue("dizhi", bundle.getString("addr")));
                    body.add(new KeyValue("concent", bundle.getString("details")));
                    for (int i = 0; i < picPaths.size(); i++) {
                        File file = new File(picPaths.get(i));
                        body.add(new KeyValue("file" + (i + 1), file));
                        body.add(new KeyValue("tupian" + (i + 1), file.getName()));
                    }
                    params.setRequestBody(new MultipartBody(body, "UTF-8"));
                    params.setMultipart(true);
                    x.http().post(params, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String s) {
//                            Log.e("TestLog", s);
                            if (JsonSyncUtils.getJsonValue(s, "state").equals("200")) {
                                Intent intent = new Intent(RepairActivity.this, RepairSuccessActivity.class);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onError(Throwable throwable, boolean b) {
                            Log.e("TestLog","err");
                        }

                        @Override
                        public void onCancelled(CancelledException e) {
                            Log.e("TestLog","cancel");
                        }

                        @Override
                        public void onFinished() {
                        }
                    });
                } else
                    Toast.makeText(this, "您的输入有误，请检查！", Toast.LENGTH_SHORT).show();
                break;
            case R.id.img_Add:
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    int checkCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
                    int checkSD = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (checkCamera + checkSD != 0) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102);
                        return;
                    }
                }
                openPicture();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 102) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                openPicture();
        }
    }

    private void openPicture() {
        FunctionConfig cfg = new FunctionConfig.Builder()
                .setMutiSelectMaxSize(3/* - chosenSize*/)
                .setSelected(picPaths)
                .setEnableCamera(true)
                .build();
        GalleryFinal.openGalleryMuti(103, cfg, new GalleryFinal.OnHanlderResultCallback() {
            @Override
            public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
                if (reqeustCode == 103) {
                    picPaths.clear();
                    if (resultList.size() > 0) {
                        layout_img.setVisibility(View.VISIBLE);
                        for (PhotoInfo i : resultList) {
                            picPaths.add(i.getPhotoPath());
                            if (resultList.indexOf(i) == 0) {
                                img_1.setImageURI(Uri.fromFile(new File(i.getPhotoPath())));
                                img_2.setVisibility(View.GONE);
                                img_3.setVisibility(View.GONE);
                            } else if (resultList.indexOf(i) == 1) {
                                img_2.setImageURI(Uri.fromFile(new File(i.getPhotoPath())));
                                img_2.setVisibility(View.VISIBLE);
                            } else if (resultList.indexOf(i) == 2) {
                                img_3.setImageURI(Uri.fromFile(new File(i.getPhotoPath())));
                                img_3.setVisibility(View.VISIBLE);
                            }
                        }
                    } else layout_img.setVisibility(View.GONE);
                }
            }

            @Override
            public void onHanlderFailure(int requestCode, String errorMsg) {

            }
        });
    }

    private boolean getForms() {
        String name = edtName.getText().toString();
        if (StringUtils.isEmpty(name)) {
            Toast.makeText(this, "请输入联系人姓名！", Toast.LENGTH_SHORT).show();
            return false;
        }
        String phone = edtPhone.getText().toString().replace("-", "");
        if (!StringUtils.validPhoneNum("2", phone)) {
            Toast.makeText(this, "请输入正确的电话号码！", Toast.LENGTH_SHORT).show();
            return false;
        }
        String addr = edtAddr.getText().toString();
        if (StringUtils.isEmpty(addr)) {
            Toast.makeText(this, "请输入详细地址！", Toast.LENGTH_SHORT).show();
            return false;
        }
        String details = edtDetails.getText().toString();
        if (StringUtils.isEmpty(details)) {
            Toast.makeText(this, "请简要描述您所报修的损坏情况！", Toast.LENGTH_SHORT).show();
            return false;
        }
        bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("phone", phone);
        bundle.putString("addr", addr);
        bundle.putString("details", details);
        return true;
    }
}
