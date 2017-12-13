package com.fanhong.cn.party.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fanhong.cn.App;
import com.fanhong.cn.R;
import com.fanhong.cn.util.FileUtil;
import com.fanhong.cn.util.GetImagePath;
import com.fanhong.cn.util.JsonSyncUtils;
import com.fanhong.cn.util.MySharedPrefUtils;
import com.fanhong.cn.view.SelectPicPopupWindow;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static android.net.Uri.fromFile;

/**
 * Created by Administrator on 2017/11/21.
 */
@ContentView(R.layout.activity_add_fx)
public class AddFxActivity extends Activity{
    @ViewInject(R.id.tv_title)
    private TextView title;
    @ViewInject(R.id.top_extra)
    private TextView extra;
    @ViewInject(R.id.add_fx_edit)
    private EditText addFxEdit;
    @ViewInject(R.id.add_pic1)
    private ImageView addPic1;
//    @ViewInject(R.id.add_pic2)
//    private ImageView addPic2;
//    @ViewInject(R.id.add_pic3)
//    private ImageView addPic3;

    private String str1,url1 /*str2,str3,url2,url3*/;

    private SelectPicPopupWindow pictureWindow; // 自定义的popupWindow
    private Context mContext;
    private App app;
    private File file,/*拍照的照片文件*/cropFile/*剪切的照片文件*/;

    private final static int TAKE_PHOTO = 55;
    private final static int CHOOSE_PHOTO = 66;
    private final static int PHOTO_CUTTING = 77;
//    private final static int FIRST = 111; //添加第一张图片
//    private final static int SECOND = 222;  //添加第二张图片
//    private final static int THIRD = 333;  //添加第三张图片

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        initViews();
    }

    private void initViews(){
        mContext = getApplicationContext();
        app = new App();
        title.setText(R.string.fx);
        extra.setVisibility(View.VISIBLE);
        extra.setText("发布");
        addFxEdit.setText(MySharedPrefUtils.getSharedPref(mContext).getString("fenxiang",""));
        addFxEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                MySharedPrefUtils.getSharedPref(mContext).edit().putString("fenxiang",s.toString()).commit();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Event({R.id.img_back,R.id.top_extra,R.id.add_pic1})
    private void onClick(View v){
        switch (v.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.top_extra:
                RequestParams params = new RequestParams(App.CMDURL);
                if(!TextUtils.isEmpty(url1)){
                    params.addBodyParameter("image",new File(url1));
                }
                params.addBodyParameter("cmd","95");
                params.addBodyParameter("uid",MySharedPrefUtils.getUserId(AddFxActivity.this));
                params.addBodyParameter("times",System.currentTimeMillis()+"");
                params.addBodyParameter("content",addFxEdit.getText().toString());
                x.http().post(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        if(JsonSyncUtils.getJsonValue(result,"cw").equals("0")){
                            Toast.makeText(AddFxActivity.this,"分享成功！",Toast.LENGTH_SHORT).show();
                            //  回调中清空
                            MySharedPrefUtils.getSharedPref(mContext).edit().putString("fenxiang","").commit();
                            AddFxActivity.this.finish();
                        }else {
                            Toast.makeText(AddFxActivity.this,"分享失败！",Toast.LENGTH_SHORT).show();
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
                break;
            case R.id.add_pic1:
//                app.setWhichImg(FIRST);
                hideSoftinputyer(addPic1);
                showWindow();
                break;
        }
    }
    //隐藏软键盘的方法
    private void hideSoftinputyer(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
    // 时间戳给上传的照片命名
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        //3位随机数
        int kk = new Random().nextInt(900) + 100;
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + "_" + kk + ".jpg";
    }

    //显示出自定义window
    private void showWindow() {
        pictureWindow = new SelectPicPopupWindow(mContext, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pictureWindow.dismiss();
                switch (v.getId()) {
                    // 拍照
                    case R.id.takePhotoBtn:
                        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        if (Build.VERSION.SDK_INT >= 23) {
                            int check = ContextCompat.checkSelfPermission(AddFxActivity.this, permissions[0]);
                            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                            if (check == PackageManager.PERMISSION_GRANTED) {
                                //调用相机
                                useCamera();
                            } else {
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
                            }
                        } else {
                            useCamera();
                        }
                        break;
                    // 选择相册
                    case R.id.pickPhotoBtn:
                        String[] permissions1 = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        if (Build.VERSION.SDK_INT >= 23) {
                            int check = ContextCompat.checkSelfPermission(AddFxActivity.this, permissions1[0]);
                            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                            if (check == PackageManager.PERMISSION_GRANTED) {
                                //调用相册选择
                                choosePhoto();
                            } else {
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                            }
                        } else {
                            choosePhoto();
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        pictureWindow.showAtLocation(findViewById(R.id.mainLayout),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            useCamera();
        } else if (requestCode == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            choosePhoto();
        } else {
            // 没有获取 到权限，从新请求，或者关闭app
            Toast.makeText(this, "需要存储权限", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 使用相机
     */
    private void useCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = new File(Environment.getExternalStorageDirectory() + "/" + getPhotoFileName());
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= 23) {
            uri = FileProvider.getUriForFile(this, "applicationId.fileprovider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        //添加权限
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);//将拍取的照片保存到指定URI
        startActivityForResult(intent, TAKE_PHOTO);

    }

    /**
     * 相册选择
     */
    private void choosePhoto() {
        Intent intent1 = new Intent(Intent.ACTION_PICK, null);
        intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent1, CHOOSE_PHOTO);
    }

    /**
     * 照片剪切
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        cropFile = new File(Environment.getExternalStorageDirectory() + "/crop" + getPhotoFileName());
        if (!cropFile.getParentFile().exists()) {
            cropFile.getParentFile().mkdirs();
            try {
                cropFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Uri outputUri = fromFile(cropFile);
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("noFaceDetection", false);//去除默认的人脸识别，否则和剪裁匡重叠
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                String url = GetImagePath.getPath(this, uri);//这个方法是处理4.4以上图片返回的Uri对象不同的处理方法
                intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
            } else {
                intent.setDataAndType(uri, "image/*");
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        }
        intent.putExtra("crop", "true");
        //宽高比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //裁剪照片的宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, PHOTO_CUTTING);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                Uri uri1 = Uri.fromFile(file);
                if (Build.VERSION.SDK_INT >= 23) {
                    uri1 = FileProvider.getUriForFile(this, "applicationId.fileprovider", file);
                }
                startPhotoZoom(uri1);
                break;
            case CHOOSE_PHOTO:
                if (data != null) {
                    startPhotoZoom(data.getData());
                }
                break;
            case PHOTO_CUTTING:
                setPicToView(data);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setPicToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras == null) {
            return;
        }
        Bitmap bitmap = extras.getParcelable("data");
//        switch (app.getWhichImg()){
//            case FIRST:
                addPic1.setImageBitmap(bitmap);
                str1 = getPhotoFileName();
                url1 = FileUtil.saveFile(mContext, str1, bitmap);
//                addPic2.setVisibility(View.VISIBLE);
//                break;
//            case SECOND:
//                addPic2.setImageBitmap(bitmap);
//                str2 = getPhotoFileName();
//                url2 = FileUtil.saveFile(mContext, str1, bitmap);
//                addPic3.setVisibility(View.VISIBLE);
//                break;
//            case THIRD:
//                addPic3.setImageBitmap(bitmap);
//                str3 = getPhotoFileName();
//                url3 = FileUtil.saveFile(mContext, str1, bitmap);
//                break;
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
