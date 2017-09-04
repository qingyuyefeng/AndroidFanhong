package com.fanhong.cn;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fanhong.cn.shippingaddress.AllAddressActivity;
import com.fanhong.cn.util.FileUtil;
import com.fanhong.cn.util.HttpUtil;
import com.fanhong.cn.util.JsonSyncUtils;
import com.fanhong.cn.view.CircleImg;
import com.fanhong.cn.view.SelectPicPopupWindow;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.x;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.net.Uri.fromFile;

public class AccountSettingsActivity extends SampleActivity implements OnClickListener {
    private TextView tv_nickname;
    private TextView tv_account;
    private LinearLayout ll_headportrait;
    private LinearLayout ll_nickname;
    private LinearLayout ll_editaddress;
    private LinearLayout ll_resetpsw;
    private SharedPreferences mSettingPref;
    private SampleConnection mSafoneConnection;
    private Context context;
    private ImageView titleBackImageBtn;

    private CircleImg avatarImg;// 头像图片
    private SelectPicPopupWindow menuWindow; // 自定义的头像编辑弹出框
    private static final int REQUESTCODE_PICK = 60;        // 相册选图标记
    private static final int REQUESTCODE_TAKE = 61;        // 相机拍照标记
    private static final int REQUESTCODE_CUTTING = 62;    // 图片裁切标记
    private static final String IMAGE_FILE_NAME = "avatarImage.jpg";// 头像文件名称
    private File file;
    private String urlpath;            // 图片本地路径
    // 上传服务器的路径【一般不硬编码到程序中】
    private String imgUrl = null;
    private String resultStr = "";    // 服务端返回结果集
    private static ProgressDialog pd;// 等待进度圈
    private String userName;
    private String userNameStr;
    private Boolean uploadissuccess = false;

    public synchronized void connectFail(int type) {
        //SampleConnection.USER = "";
        //SampleConnection.USER_STATE = 0;

        //Toast.makeText(this, getString(R.string.login_fail), Toast.LENGTH_SHORT).show();
        if (pd != null)
            pd.dismiss();
    }

    public synchronized void connectSuccess(JSONObject json, int type) {
        int cmd = -1;
        int result = -1;
        int state = 0;
        String str;
        String name = "";
        String phonenumber = "";
        String alias = "";
//        Log.i("xq", "ResetPswActivity.java json=" + json.toString());
        try {
            str = json.getString("cmd");
            cmd = Integer.parseInt(str);
            str = json.getString("cw");
            result = Integer.parseInt(str);

            try {
                name = json.getString("name");
            } catch (Exception e) {
            }
        } catch (Exception e) {
            connectFail(type);
            return;
        }
        if (cmd == 2 && result == 0) {

        } else if (cmd == 4) {

            if (result == 0) {
                mSettingPref.edit().putInt("Status", 0).commit();
                Intent intent = new Intent();
                this.setResult(2, intent);
                this.finish();
                Toast.makeText(this, getString(R.string.resetpassword_success), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.resetpassword_fail), Toast.LENGTH_SHORT).show();
            }
        } else if (cmd == 14) {
            if (result == 1) {
                pd.dismiss();
                Toast.makeText(AccountSettingsActivity.this, "头像上传成功！", Toast.LENGTH_LONG).show();
                getToken(mSettingPref.getString("UserId", ""));
            } else {
                pd.dismiss();
                Toast.makeText(AccountSettingsActivity.this, "头像上传失败！", Toast.LENGTH_LONG).show();
            }
        } else {
            connectFail(type);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountsettings);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        context = getApplicationContext();
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSafoneConnection != null) {
            mSafoneConnection.close();
        }
    }

    private void init() {
        tv_nickname = (TextView) findViewById(R.id.tv_nickname);
        tv_account = (TextView) findViewById(R.id.tv_account);
        avatarImg = (CircleImg) findViewById(R.id.avatarImg);

        ll_headportrait = (LinearLayout) findViewById(R.id.ll_headportrait);
        ll_headportrait.setOnClickListener(this);
        ll_nickname = (LinearLayout) findViewById(R.id.ll_nickname);
        ll_nickname.setOnClickListener(this);
        ll_editaddress = (LinearLayout) findViewById(R.id.ll_inputaddress);
        ll_editaddress.setOnClickListener(this);
        ll_resetpsw = (LinearLayout) findViewById(R.id.ll_resetpsw);
        ll_resetpsw.setOnClickListener(this);

        titleBackImageBtn = (ImageView) findViewById(R.id.titleBackImageBtn);
        titleBackImageBtn.setOnClickListener(this);

        mSettingPref = getSharedPreferences("Setting", Context.MODE_PRIVATE);
        if (mSettingPref.getInt("Status", 0) == 1) {
            String nick = "";
            String name = "";
            try {
                name = mSettingPref.getString("Name", "");
                //nick = SampleConnection.ALIAS;
            } catch (Exception e) {
            }
            try {
                nick = mSettingPref.getString("Nick", "");
            } catch (Exception e) {
            }
            try {
                String ul = mSettingPref.getString("Logo", "");
                if (ul != null && ul.length() > 0) {
                    //用ImageLoader加载图片
                    ImageLoader.getInstance().displayImage(ul, avatarImg, new ImageLoaderPicture(context).getOptions(), new SimpleImageLoadingListener());
                }
            } catch (Exception e) {
            }
            tv_nickname.setText(nick);
            tv_account.setText(name);
            userName = name.trim() + ".jpg";
            userNameStr = name;
            // new UploadTask().execute();
        }
    }

    private void getToken(String userId) {
        org.xutils.http.RequestParams params = new org.xutils.http.RequestParams(App.CMDURL);
        params.addParameter("cmd", "55");
        params.addParameter("id", userId);
        Log.i("getToken json=id:", userId);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                mSettingPref.edit().putString("token", JsonSyncUtils.getJsonValue(s, "token")).commit();
                SampleConnection.TOKEN = JsonSyncUtils.getJsonValue(s, "token");
                Log.i("getToken json=", "onSuccess:" + s);
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                Log.i("getToken json=", "onError:" + throwable.getMessage());
            }

            @Override
            public void onCancelled(CancelledException e) {
                Log.i("getToken json=", "onCancelled:" + e.getMessage());
            }

            @Override
            public void onFinished() {
                Log.i("getToken json=", "onFinished:");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                break;
            case 2:  //修改昵称返回
                tv_nickname.setText(data.getStringExtra("nick"));
                getToken(mSettingPref.getString("UserId", ""));
//			tv_nickname.setText(mSettingPref.getString("Nick",""));
                break;
            case 3:  //修改密码返回
                tv_nickname.setText("");
                tv_account.setText("");
                avatarImg.setImageResource(R.drawable.ico_tx);
                break;
        }

        switch (requestCode) {
            case REQUESTCODE_PICK:// 直接从相册获取
                try {
                    startPhotoZoom(data.getData());
                } catch (NullPointerException e) {
                    e.printStackTrace(); // 用户点击取消操作
                }
                break;
            case REQUESTCODE_TAKE: // 调用相机拍照
                Uri uri = fromFile(file);
                if (Build.VERSION.SDK_INT >= 24) {
                    uri = FileProvider.getUriForFile(this, "com.xqyh.customview.fileprovider", file);
                }
                startPhotoZoom(uri);
                break;
            case REQUESTCODE_CUTTING: //取得裁剪后的图片
                if (data != null) {
                    setPicToView(data);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.titleBackImageBtn:
                if (uploadissuccess) {
                    Intent intent = new Intent();
                    this.setResult(21, intent);
                    this.finish();
                } else
                    this.finish();
                break;
            case R.id.ll_headportrait:
                menuWindow = new SelectPicPopupWindow(context, itemsOnClick);
                menuWindow.showAtLocation(findViewById(R.id.mainLayout),
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.ll_nickname:
                Intent intent1 = new Intent(AccountSettingsActivity.this, ModifyNickNameActivity.class);
                startActivityForResult(intent1, 1);
                break;
            case R.id.ll_inputaddress:
                startActivity(new Intent(AccountSettingsActivity.this, AllAddressActivity.class));
                break;
            case R.id.ll_resetpsw:
//			if(mSettingPref.getInt("Status", 0)==0){
//				Toast.makeText(this, getString(R.string.pleaselogin), Toast.LENGTH_SHORT).show();
//				return;
//			}
                Intent intent2 = new Intent(AccountSettingsActivity.this, ModifyPswActivity.class);
                startActivityForResult(intent2, 1);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (uploadissuccess) {
                Intent intent = new Intent();
                this.setResult(21, intent);
                this.finish();
            } else
                this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    //为弹出窗口实现监听类
    private OnClickListener itemsOnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()) {
                // 拍照
                case R.id.takePhotoBtn:
//                    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
//                    if (Build.VERSION.SDK_INT >= 23) {
//                        int check = ContextCompat.checkSelfPermission(AccountSettingsActivity.this, permissions[0]);
//                        // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
//                        if (check == PackageManager.PERMISSION_GRANTED) {
//                            //调用相机
//                            useCamera();
//                        } else {
//                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//                        }
//                    } else {
//                        useCamera();
//                    }
                    String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    if (Build.VERSION.SDK_INT >= 24) {
                        int check = ContextCompat.checkSelfPermission(AccountSettingsActivity.this, permissions[0]);
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
                // 相册选择图片
                case R.id.pickPhotoBtn:
//                    Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
//                    // 如果朋友们要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、image/png"等的类型
//                    pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//                    startActivityForResult(pickIntent, REQUESTCODE_PICK);
                    String[] permissions1 = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    if (Build.VERSION.SDK_INT >= 24) {
                        int check = ContextCompat.checkSelfPermission(AccountSettingsActivity.this, permissions1[0]);
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
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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
        file = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= 24) {
//            ContentValues contentValues = new ContentValues(1);
//            contentValues.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
//            uri = getApplication().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            uri = FileProvider.getUriForFile(this, "com.xqyh.customview.fileprovider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        //添加权限
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);//将拍取的照片保存到指定URI
        startActivityForResult(intent, REQUESTCODE_TAKE);

    }

    private void choosePhoto() {
        Intent intent1 = new Intent(Intent.ACTION_PICK, null);
        intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent1, REQUESTCODE_PICK);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= 24) {
//            uri=FileProvider.getUriForFile(this,getPackageName()+".fileprovider",file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        intent.setDataAndType(uri, "image/*");
        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            // 取得SDCard图片路径做显示
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(null, photo);
            //裁剪后的图片的保存路径
            urlpath = FileUtil.saveFile(context, userName, photo);
            avatarImg.setImageDrawable(drawable);
            //imgUrl = SampleConnection.HEAD_PICTURE_URL+userName;
            imgUrl = SampleConnection.HEAD_PICTURE_URL;

            File file = new File(urlpath); //这里的path就是那个地址的全局变量
            asynchttpUpload(imgUrl, file);
            // 新线程后台上传服务端
            pd = ProgressDialog.show(this, null, "正在上传图片，请稍候...");
        }
    }

    public void asynchttpUpload(String path, File myFile) {
        Log.e("hu", "*****path=" + path + " myFile=" + myFile);
        RequestParams params = new RequestParams();
        try {
            params.put("touxiang", myFile);
            //AsyncHttpClient client = new AsyncHttpClient();
            HttpUtil.post(path, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, String content) {
                    super.onSuccess(statusCode, content);
                    //pd.dismiss();
//                    Log.e("hu", "**********statusCode=" + statusCode + " content=" + content);
                    dealResult(content);

                }
            });
        } catch (FileNotFoundException e) {
        }
    }

    private String pig;

    private void dealResult(String content) {
        try {
            JSONObject json = new JSONObject(content);
            String status = json.getString("status");
            pig = json.getString("msg");
//            Log.e("hu", "*******status=" + status);
            if (status.equals("true")) {
                //Toast.makeText(AccountSettingsActivity.this, "上传成功！", Toast.LENGTH_LONG).show();
                uploadissuccess = true;
                if (SampleConnection.LOGO_URL != null && SampleConnection.LOGO_URL.length() > 0) {
                    //清除图片缓存
                    ImageLoader.getInstance().clearMemoryCache();
                    ImageLoader.getInstance().clearDiscCache();
                }
                getTouxiang(pig);
            }
        } catch (Exception e) {

        }
    }

    private void getTouxiang(String logo) {
        Log.i("hu", "*******getTouxiang()");
        String userid = mSettingPref.getString("UserId", "");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("cmd", "13");
        map.put("uid", userid);
        map.put("logo", logo);

//        Log.i("xq", "修改头像map===>" + map.toString());
        if (mSafoneConnection == null)
            mSafoneConnection = new SampleConnection(AccountSettingsActivity.this, 0);
        mSafoneConnection.connectService1(map);
    }

    Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    pd.dismiss();
                    try {
                        // 返回数据示例，根据需求和后台数据灵活处理
                        // {"status":"1","statusMessage":"上传成功","imageUrl":"http://120.24.219.49/726287_temphead.jpg"}
                        JSONObject jsonObject = new JSONObject(resultStr);
                        Log.i("hu", "*****8***resultStr=" + resultStr);
                        // 服务端以字符串“1”作为操作成功标记
                        if (jsonObject.optString("status").equals("1")) {
                            BitmapFactory.Options option = new BitmapFactory.Options();
                            // 压缩图片:表示缩略图大小为原始图片大小的几分之一，1为原图，3为三分之一
                            option.inSampleSize = 1;

                            // 服务端返回的JsonObject对象中提取到图片的网络URL路径
                            String imageUrl = jsonObject.optString("imageUrl");
                            Toast.makeText(context, imageUrl, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, jsonObject.optString("statusMessage"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;

                default:
                    break;
            }
            return false;
        }
    });
}
