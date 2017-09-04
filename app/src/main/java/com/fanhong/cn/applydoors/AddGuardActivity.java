package com.fanhong.cn.applydoors;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fanhong.cn.AccountSettingsActivity;
import com.fanhong.cn.App;
import com.fanhong.cn.GardenSelecterActivity;
import com.fanhong.cn.R;
import com.fanhong.cn.SampleActivity;
import com.fanhong.cn.SampleConnection;
import com.fanhong.cn.util.FileUtil;
import com.fanhong.cn.util.HttpUtil;
import com.fanhong.cn.view.SelectPicPopupWindow;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Administrator on 2017/6/9.
 */

@ContentView(R.layout.activity_applydoors_fangdong)
public class AddGuardActivity extends SampleActivity {
    final String ImageUrl = "http://m.wuyebest.com/index.php/App/index/user_xx";
    @ViewInject(R.id.tv_title)
    private TextView title;
    @ViewInject(R.id.agree_protocol)
    private CheckBox agree;
    @ViewInject(R.id.guard_protocol)
    private TextView userKnow;

    private App app;
    private SampleConnection mSample;
    private ImageView backBtn, sfzBack, photoFront, housePropertyCard;
    private TextView chooseCell, chooseLou;
    private SelectPicPopupWindow pictureWindow; // 自定义的popupWindow
    private Button submit;
    private SharedPreferences mSharedPref;
    private File file;
    private Uri picUri;
    private String urlsfzbm, urlfdfr, urlhpc; //3张照片的路径
    private Context context;
    private long time1, time2;

    private final int TAKE_PHOTO = 77;//拍照的回调
    private final int CHOOSE_PHOTO = 88;//选择照片的回调
    private final int PHOTO_CUTTING = 99;//剪切回调

    private final int SFZBM = 222;//身份证照片的回调
    private final int FDPHOTO = 333;//人脸照片的回调
    private final int FDFCZ = 444;//房产证照片的回调
    private final int TJSC = 777; //上传提交

    private String cellId; //小区的id
    private String lastCellId;
    private String louId;//楼栋的id
    private int checked = -1;

    @Override
    public synchronized void connectFail(int type) {
        switch (type) {
            case TJSC:
                Toast.makeText(AddGuardActivity.this, "上传失败，数据异常错误", Toast.LENGTH_SHORT).show();
                submit.setText("提交");
                submit.setEnabled(true);
                break;
        }
    }

    @Override
    public synchronized void connectSuccess(JSONObject json, int type) {
        switch (type) {
            case TJSC:
                try {
                    String str = json.getString("cw");
                    //上传成功的回调
                    Log.i("xq", "返回的json====>" + json.toString());
                    int cw = Integer.parseInt(str);
                    if (cw == 0) {
                        Intent intent = new Intent(AddGuardActivity.this, CheckingGuardActivity.class);
                        startActivity(intent);
                    } else {
                        connectFail(TJSC);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        title.setText(R.string.postmessages);
        init();
        agree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checked = 1;
                } else {
                    checked = 0;
                }
            }
        });
        userKnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(AddGuardActivity.this, UserShouldKnowActivity.class), 17);
            }
        });
    }

    private void init() {
        mSharedPref = getSharedPreferences("Setting", Context.MODE_PRIVATE);
        app = new App();
        context = getApplicationContext();
        cellId = mSharedPref.getString("gardenId", "");
        lastCellId = cellId;

        backBtn = (ImageView) findViewById(R.id.img_back);

        chooseCell = (TextView) findViewById(R.id.fangdong_inputcell);
        //默认的小区
        String str = mSharedPref.getString("gardenName", "");
        if (!TextUtils.isEmpty(str))
            chooseCell.setText(str);
        else
            chooseCell.setText(getString(R.string.choosecell));
        chooseLou = (TextView) findViewById(R.id.fangdong_iuputhouse);


        sfzBack = (ImageView) findViewById(R.id.fangdong_sfzbm);
        photoFront = (ImageView) findViewById(R.id.fangdong_photo);
        housePropertyCard = (ImageView) findViewById(R.id.house_property_card);
        submit = (Button) findViewById(R.id.fangdong_submit_applydoors);


        backBtn.setOnClickListener(onClickListener);
        chooseCell.setOnClickListener(onClickListener);
        chooseLou.setOnClickListener(onClickListener);
        sfzBack.setOnClickListener(onClickListener);
        photoFront.setOnClickListener(onClickListener);
        housePropertyCard.setOnClickListener(onClickListener);
        submit.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            switch (v.getId()) {
                case R.id.img_back:
                    time2 = System.currentTimeMillis();
                    if (time2 - time1 > 2000) {
                        Toast.makeText(AddGuardActivity.this, "即将返回，请确保数据已上传", Toast.LENGTH_SHORT).show();
                        time1 = time2;
                    } else
                        finish();
                    break;
                case R.id.fangdong_inputcell:
                    intent.setClass(AddGuardActivity.this, GardenSelecterActivity.class);
                    startActivityForResult(intent, 100);
                    break;
                case R.id.fangdong_iuputhouse:
                    intent.setClass(AddGuardActivity.this, ChooseLouActivity.class);
                    intent.putExtra("cellId", cellId);
                    startActivityForResult(intent, 101);
                    break;
                case R.id.fangdong_sfzbm:
                    app.setWhichImg(SFZBM);
                    showWindow();
                    break;
                case R.id.fangdong_photo:
                    app.setWhichImg(FDPHOTO);
                    showWindow();
                    break;
                case R.id.house_property_card:
                    app.setWhichImg(FDFCZ);
                    showWindow();
                    break;
                case R.id.fangdong_submit_applydoors:
                    postSfzzm();
                    break;
            }
        }
    };

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
        pictureWindow = new SelectPicPopupWindow(context, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pictureWindow.dismiss();
                switch (v.getId()) {
                    // 拍照
                    case R.id.takePhotoBtn:
//                        Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        ContentValues values = new ContentValues();
//                        values.put(MediaStore.Images.Media.TITLE, getPhotoFileName());
//                        picUri = getContentResolver().insert(
//                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//                        takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
//                        startActivityForResult(takeIntent, TAKE_PHOTO);
                        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        if (Build.VERSION.SDK_INT >= 24) {
                            int check = ContextCompat.checkSelfPermission(AddGuardActivity.this, permissions[0]);
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
//                        Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
//                        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//                        startActivityForResult(pickIntent, CHOOSE_PHOTO);
                        String[] permissions1 = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        if (Build.VERSION.SDK_INT >= 24) {
                            int check = ContextCompat.checkSelfPermission(AddGuardActivity.this, permissions1[0]);
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
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(this, "com.xqyh.customview.fileprovider", file);
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
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
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
        switch (resultCode) {
            case 51:
                cellId = data.getStringExtra("gardenId");
                Log.i("xq", "重新选择后cellId=======>" + cellId);
                String str = data.getStringExtra("gardenName");
                if (!TextUtils.isEmpty(str)) {
                    chooseCell.setText(str);
                }
                if (!cellId.equals(lastCellId)) {
                    louId = "";
                    chooseLou.setText(R.string.chooselou);
                    lastCellId = cellId;
                }
                break;
            case 52:
                //选择楼栋
                String s1 = data.getStringExtra("loudongName");
                louId = data.getStringExtra("loudongId");
                Log.i("xq", "选择的楼栋====>" + s1 + "<" + louId);
                if (!TextUtils.isEmpty(s1))
                    chooseLou.setText(s1);
                break;
            case 17://阅读同意用户须知
                boolean b = data.getBooleanExtra("ifreceive", false);
                agree.setChecked(b);
                break;
            case RESULT_OK:
                switch (requestCode) {
                    case TAKE_PHOTO:
//                        if (data != null && data.getData() != null) {
//                            startPhotoZoom(data.getData());
//                        } else
//                            startPhotoZoom(picUri);
                        Uri uri1 = Uri.fromFile(file);
                        if (Build.VERSION.SDK_INT >= 24) {
                            uri1 = FileProvider.getUriForFile(this, "com.xqyh.customview.fileprovider", file);
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
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setPicToView(Intent mydata) {
        Bundle extras = mydata.getExtras();
        if (extras == null) {
            return;
        }
        Bitmap bitmap = extras.getParcelable("data");
        int k = app.getWhichImg();
        switch (k) {
            case SFZBM:
                sfzBack.setImageBitmap(bitmap);
                str1 = getPhotoFileName();
                urlsfzbm = FileUtil.saveFile(context, str1, bitmap);
                break;
            case FDPHOTO:
                photoFront.setImageBitmap(bitmap);
                str2 = getPhotoFileName();
                urlfdfr = FileUtil.saveFile(context, str2, bitmap);
                break;
            case FDFCZ:
                housePropertyCard.setImageBitmap(bitmap);
                str3 = getPhotoFileName();
                urlhpc = FileUtil.saveFile(context, str3, bitmap);
                break;
        }
    }

    private String str1, str2, str3;//3张照片的名字

    //先传照片
    private void postSfzzm() {
        if (TextUtils.isEmpty(louId)) {
            Toast.makeText(this, "请选择小区楼栋号", Toast.LENGTH_SHORT).show();
        } else if (urlsfzbm == null) {
            Toast.makeText(this, "请传入身份证人脸面照片", Toast.LENGTH_SHORT).show();
        } else if (urlfdfr == null) {
            Toast.makeText(this, "请传入人脸正面照片", Toast.LENGTH_SHORT).show();
        } else if (urlhpc == null) {
            Toast.makeText(this, "请传入房产证/购(租)房合同照片", Toast.LENGTH_SHORT).show();
        } else if (checked != 1) {
            Toast.makeText(this, "请阅读并同意用户须知", Toast.LENGTH_SHORT).show();
        } else {
            File file1 = new File(urlsfzbm);
            File file2 = new File(urlfdfr);
            File file3 = new File(urlhpc);
            List<File> files = new ArrayList<>();
            files.add(file1);
            files.add(file2);
            files.add(file3);
            for (File f : files) {
                asynchttpUpload(ImageUrl, f);
            }
            submitMessage();
        }
    }

    private void asynchttpUpload(String url, File myFile) {
        RequestParams params = new RequestParams();
        try {
            params.put("xinxi", myFile);
            HttpUtil.post(url, params, new AsyncHttpResponseHandler() {
                //              int i = 1;
                @Override
                public void onSuccess(int statusCode, String content) {
                    Log.i("xq", "statueCode==>" + statusCode + "content==>" + content);
                    try {
                        JSONObject json = new JSONObject(content);
                        String status = json.getString("status");
                        if (status.equals("true")) {
//                            String str = json.getString("msg");
//                            Message msg1 = Message.obtain();
//                            msg1.what = i;
//                            msg1.obj = str;
//                            handler.sendMessage(msg1);
                        }
                    } catch (Exception e) {
                    }
                }
//              i++;
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    //提交所有信息
    private void submitMessage() {
        Map<String, Object> map = new HashMap<>();
        String uid = mSharedPref.getString("UserId", "");
        map.put("cmd", "39");
        map.put("uid", uid);
        map.put("xid", cellId);
        map.put("dizhi", louId);
        map.put("xinxi", str1 + "," + str3);
        map.put("rltp", str2);
        Log.i("xq", "上传map====>" + map.toString());
        if (mSample == null) {
            mSample = new SampleConnection(this, TJSC);
        }
        mSample.connectService1(map);
        submit.setText("提交中。。");
        submit.setEnabled(false);
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int mm = msg.what;
            switch (mm) {
//                case 1:
//                    str1 = (String) msg.obj;
//                    Log.i("xq","s1======>"+str1);
//                    break;
//                case 2:
//                    str2 = (String) msg.obj;
//                    Log.i("xq","s2======>"+str2);
//                    break;
//                case 3:
//                    str3 = (String) msg.obj;
//                    Log.i("xq","s3======>"+str3);
//                    break;
//                case 4:
//                    str4 = (String) msg.obj;
//                    Log.i("xq","s4======>"+str4);
//                    break;
            }
            return true;
        }
    });

    @Override
    protected void onResume() {
        super.onResume();
        submit.setText("提交");
        submit.setEnabled(true);
    }

    //重写外部返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            time2 = System.currentTimeMillis();
            if (time2 - time1 > 2000) {
                Toast.makeText(AddGuardActivity.this, "即将返回，请确保数据已上传", Toast.LENGTH_SHORT).show();
                time1 = time2;
                return true;
            } else {
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
