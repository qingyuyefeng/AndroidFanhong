package com.fanhong.cn.usedmarket;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fanhong.cn.AccountSettingsActivity;
import com.fanhong.cn.App;
import com.fanhong.cn.CommStoreDetailsActivity;
import com.fanhong.cn.R;
import com.fanhong.cn.SampleActivity;
import com.fanhong.cn.SampleConnection;
import com.fanhong.cn.util.FileUtil;
import com.fanhong.cn.util.GetImagePath;
import com.fanhong.cn.util.JsonSyncUtils;
import com.fanhong.cn.util.StringUtils;
import com.fanhong.cn.view.SelectPicPopupWindow;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static android.net.Uri.fromFile;

/**
 * Created by Administrator on 2017/5/15.
 */

@ContentView(R.layout.usedmarkettop)
public class ShopActivity extends SampleActivity {
    @ViewInject(R.id.layout_ms_sort)
    private AutoLinearLayout layout_sort;//排序页面
    @ViewInject(R.id.layout_ms_classify)
    private AutoLinearLayout layout_classify;//分类页面
    @ViewInject(R.id.rb_ms_sort)
    private RadioButton rb_sort;
    @ViewInject(R.id.rb_ms_classify)
    private RadioButton rb_classify;
    @ViewInject(R.id.rb_ms_sort_1)
    private RadioButton rb_sort1;
    @ViewInject(R.id.rb_ms_sort_2)
    private RadioButton rb_sort2;
    @ViewInject(R.id.rb_ms_sort_3)
    private RadioButton rb_sort3;
    @ViewInject(R.id.rb_ms_classify_1)
    private RadioButton rb_classify_1;
    @ViewInject(R.id.rb_ms_classify_2)
    private RadioButton rb_classify_2;
    @ViewInject(R.id.rb_ms_classify_3)
    private RadioButton rb_classify_3;
    @ViewInject(R.id.rb_ms_classify_4)
    private RadioButton rb_classify_4;
    @ViewInject(R.id.rb_ms_classify_5)
    private RadioButton rb_classify_5;
    @ViewInject(R.id.rb_ms_classify_6)
    private RadioButton rb_classify_6;
    @ViewInject(R.id.rb_ms_classify_7)
    private RadioButton rb_classify_7;
    @ViewInject(R.id.rb_ms_classify_8)
    private RadioButton rb_classify_8;
    @ViewInject(R.id.rb_ms_classify1)
    private RadioButton rb_classify1;
    @ViewInject(R.id.rb_ms_classify2)
    private RadioButton rb_classify2;
    @ViewInject(R.id.rb_ms_classify3)
    private RadioButton rb_classify3;
    @ViewInject(R.id.rb_ms_classify4)
    private RadioButton rb_classify4;
    @ViewInject(R.id.rb_ms_classify5)
    private RadioButton rb_classify5;
    @ViewInject(R.id.rb_ms_classify6)
    private RadioButton rb_classify6;
    @ViewInject(R.id.rb_ms_classify7)
    private RadioButton rb_classify7;
    @ViewInject(R.id.rb_ms_classify8)
    private RadioButton rb_classify8;
    @ViewInject(R.id.lv_ms_goods)
    private ListView shopListView;

    private List<ShopModel> shopModels = new ArrayList<>();
    private ShopAdapter shopAdapter;
    private RadioGroup radioGroup;
    private RadioButton radioButton1, radioButton2;
    private RelativeLayout addgoodsLayout;
    private ImageView myusedgoods, yourGoodspicture;

    private SharedPreferences mSettingPref;
    private EditText[] edittext = new EditText[5];
    private Button sureToPost;
    private AutoRelativeLayout listLayout;
    private static final int REQUESTCODE_PICK = 60;        //相册选择
    private static final int REQUESTCODE_TAKE = 61;        //拍照
    private static final int REQUESTCODE_CUTTING = 62;    // 剪切
    private File file,cropFile;//拍照指定保存 cropFile:剪切的文件
    private String urlpath;//图片的路径
    private String picmsg; //resultStr中的图片名
    private String uid;//登录时的id
    //    String goodsUrl = "http://m.wuyebest.com/index.php/App/index/goods2";
    private String goodsUrl = SampleConnection.GOODS_PICTURE_URL;
    //    String cmdUrl = "http://m.wuyebest.com/index.php/App/index";
    private Context context;
    private Intent intent = null;
    private SelectPicPopupWindow pictureWindow;
    private int delaytime = 400;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        init();
    }

    public void init() {

        yourGoodspicture = (ImageView) findViewById(R.id.img_ms_add2);
        edittext[0] = (EditText) findViewById(R.id.edt_ms_title);
        edittext[1] = (EditText) findViewById(R.id.edt_ms_details);
        edittext[2] = (EditText) findViewById(R.id.edt_ms_price);
        edittext[3] = (EditText) findViewById(R.id.edt_ms_sender);
        edittext[4] = (EditText) findViewById(R.id.edt_ms_phone);

        sureToPost = (Button) findViewById(R.id.sure_to_postgoods);

        context = getApplicationContext();

        yourGoodspicture.setOnClickListener(ocl);
        sureToPost.setOnClickListener(ocl);


        mSettingPref = getSharedPreferences("Setting", Context.MODE_PRIVATE);
        uid = mSettingPref.getString("UserId", "");

        listLayout = (AutoRelativeLayout) findViewById(R.id.goods_listview_layout);
        shopListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ShopActivity.this, CommStoreDetailsActivity.class);
                ShopModel bean = shopModels.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("title", bean.getGoodsName());
                bundle.putString("img", bean.getGoodsPicture());
                bundle.putString("detail", bean.getGoodsMessages());
                bundle.putString("addr", bean.getOwnerName().replace("卖家姓名：", ""));
                bundle.putString("phone", bean.getOwnerPhone().replace("卖家电话：", ""));
                bundle.putString("price", bean.getPrice());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        addgoodsLayout = (RelativeLayout) findViewById(R.id.add_usedgoodslayout);


        shopAdapter = new ShopAdapter(shopModels, ShopActivity.this);
        shopAdapter.setCallseller(new ShopAdapter.Callseller() {
            @Override
            public void onCall(String str) {
                showDialog(str);
            }
        });

        findViewById(R.id.usedmarket_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        radioGroup = (RadioGroup) findViewById(R.id.radio_usedmarket);
        radioButton1 = (RadioButton) findViewById(R.id.radio_usedmarket_first);
        radioButton2 = (RadioButton) findViewById(R.id.radio_usedmarket_second);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.radio_usedmarket_first:
                        listLayout.setVisibility(View.VISIBLE);
                        addgoodsLayout.setVisibility(View.GONE);
                        myusedgoods.setVisibility(View.VISIBLE);
                        radioButton1.setTextColor(getResources().getColor(R.color.skyblue));
                        radioButton2.setTextColor(getResources().getColor(R.color.white));
                        break;
                    case R.id.radio_usedmarket_second:
                        if (isLogined() == 1) {
                            listLayout.setVisibility(View.GONE);
                            addgoodsLayout.setVisibility(View.VISIBLE);
                            myusedgoods.setVisibility(View.INVISIBLE);
                            radioButton1.setTextColor(getResources().getColor(R.color.white));
                            radioButton2.setTextColor(getResources().getColor(R.color.skyblue));
                        } else {
                            radioButton1.setChecked(true);
                            Toast.makeText(context, R.string.pleaselogin, Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        });
        myusedgoods = (ImageView) findViewById(R.id.select_mygoods);
        myusedgoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLogined() == 0) {
                    Toast.makeText(ShopActivity.this, R.string.pleaselogin, Toast.LENGTH_SHORT).show();
                } else {
                    intent = new Intent(ShopActivity.this, GoodsselectActivity.class);
                    startActivity(intent);
                }
            }
        });

        shopListView.setAdapter(shopAdapter);

    }

    private void classifyl(int id) {
        rb_classify_1.setEnabled(false);
        rb_classify_2.setEnabled(false);
        rb_classify_3.setEnabled(false);
        rb_classify_4.setEnabled(false);
        rb_classify_5.setEnabled(false);
        rb_classify_6.setEnabled(false);
        rb_classify_7.setEnabled(false);
        rb_classify_8.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rb_classify_1.setEnabled(true);
                rb_classify_2.setEnabled(true);
                rb_classify_3.setEnabled(true);
                rb_classify_4.setEnabled(true);
                rb_classify_5.setEnabled(true);
                rb_classify_6.setEnabled(true);
                rb_classify_7.setEnabled(true);
                rb_classify_8.setEnabled(true);
            }
        }, delaytime);
        rb_classify_1.setChecked(false);
        rb_classify_2.setChecked(false);
        rb_classify_3.setChecked(false);
        rb_classify_4.setChecked(false);
        rb_classify_5.setChecked(false);
        rb_classify_6.setChecked(false);
        rb_classify_7.setChecked(false);
        rb_classify_8.setChecked(false);
        switch (id) {
            case 1:
                rb_classify_1.setChecked(true);
                break;
            case 2:
                rb_classify_2.setChecked(true);
                break;
            case 3:
                rb_classify_3.setChecked(true);
                break;
            case 4:
                rb_classify_4.setChecked(true);
                break;
            case 5:
                rb_classify_5.setChecked(true);
                break;
            case 6:
                rb_classify_6.setChecked(true);
                break;
            case 7:
                rb_classify_7.setChecked(true);
                break;
            case 8:
                rb_classify_8.setChecked(true);
                break;
        }
    }

    private void classifyr(int id) {
        rb_classify1.setEnabled(false);
        rb_classify2.setEnabled(false);
        rb_classify3.setEnabled(false);
        rb_classify4.setEnabled(false);
        rb_classify5.setEnabled(false);
        rb_classify6.setEnabled(false);
        rb_classify7.setEnabled(false);
        rb_classify8.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rb_classify1.setEnabled(true);
                rb_classify2.setEnabled(true);
                rb_classify3.setEnabled(true);
                rb_classify4.setEnabled(true);
                rb_classify5.setEnabled(true);
                rb_classify6.setEnabled(true);
                rb_classify7.setEnabled(true);
                rb_classify8.setEnabled(true);
            }
        }, delaytime);
        rb_classify1.setChecked(false);
        rb_classify2.setChecked(false);
        rb_classify3.setChecked(false);
        rb_classify4.setChecked(false);
        rb_classify5.setChecked(false);
        rb_classify6.setChecked(false);
        rb_classify7.setChecked(false);
        rb_classify8.setChecked(false);
        switch (id) {
            case 1:
                rb_classify1.setChecked(true);
                break;
            case 2:
                rb_classify2.setChecked(true);
                break;
            case 3:
                rb_classify3.setChecked(true);
                break;
            case 4:
                rb_classify4.setChecked(true);
                break;
            case 5:
                rb_classify5.setChecked(true);
                break;
            case 6:
                rb_classify6.setChecked(true);
                break;
            case 7:
                rb_classify7.setChecked(true);
                break;
            case 8:
                rb_classify8.setChecked(true);
                break;
        }
    }

    private void delayTop() {
        rb_sort.setEnabled(false);
        rb_classify.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rb_sort.setEnabled(true);
                rb_classify.setEnabled(true);
            }
        }, delaytime);
    }

    private void delaySort() {
        rb_sort1.setEnabled(false);
        rb_sort2.setEnabled(false);
        rb_sort3.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rb_sort1.setEnabled(true);
                rb_sort2.setEnabled(true);
                rb_sort3.setEnabled(true);
            }
        }, delaytime);
    }

    @Event(value = {R.id.rb_ms_sort, R.id.rb_ms_classify, R.id.rb_ms_sort_1, R.id.rb_ms_sort_2, R.id.rb_ms_sort_3,
            R.id.rb_ms_classify_1, R.id.rb_ms_classify_2, R.id.rb_ms_classify_3, R.id.rb_ms_classify_4,
            R.id.rb_ms_classify_5, R.id.rb_ms_classify_6, R.id.rb_ms_classify_7, R.id.rb_ms_classify_8,
            R.id.tv_black_area1, R.id.tv_black_area2, R.id.add_usedgoodslayout, R.id.rb_ms_classify1,
            R.id.rb_ms_classify2, R.id.rb_ms_classify3, R.id.rb_ms_classify4, R.id.rb_ms_classify5,
            R.id.rb_ms_classify6, R.id.rb_ms_classify7, R.id.rb_ms_classify8})
    private void onClicks(View view) {
        switch (view.getId()) {
            case R.id.rb_ms_sort:
                delayTop();
                if (layout_sort.getVisibility() == View.VISIBLE) {
                    rb_sort.setChecked(false);
                    layout_sort.setVisibility(View.GONE);
                } else {
                    if (rb_classify.isChecked()) {
                        rb_classify.setChecked(false);
                        layout_classify.setVisibility(View.GONE);
                    }
                    rb_sort.setChecked(true);
                    layout_sort.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.rb_ms_classify:
                delayTop();
                if (layout_classify.getVisibility() == View.VISIBLE) {
                    rb_classify.setChecked(false);
                    layout_classify.setVisibility(View.GONE);
                } else {
                    if (rb_sort.isChecked()) {
                        rb_sort.setChecked(false);
                        layout_sort.setVisibility(View.GONE);
                    }
                    rb_classify.setChecked(true);
                    layout_classify.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tv_black_area1:
                onClicks(rb_sort);
                break;
            case R.id.tv_black_area2:
                onClicks(rb_classify);
                break;
            case R.id.rb_ms_sort_1:
                delaySort();
                rb_sort1.setChecked(true);
                rb_sort2.setChecked(false);
                rb_sort3.setChecked(false);
                break;
            case R.id.rb_ms_sort_2:
                delaySort();
                rb_sort1.setChecked(false);
                rb_sort2.setChecked(true);
                rb_sort3.setChecked(false);
                break;
            case R.id.rb_ms_sort_3:
                delaySort();
                rb_sort1.setChecked(false);
                rb_sort2.setChecked(false);
                rb_sort3.setChecked(true);
                break;
            case R.id.rb_ms_classify_1:
                classifyl(1);
                break;
            case R.id.rb_ms_classify_2:
                classifyl(2);
                break;
            case R.id.rb_ms_classify_3:
                classifyl(3);
                break;
            case R.id.rb_ms_classify_4:
                classifyl(4);
                break;
            case R.id.rb_ms_classify_5:
                classifyl(5);
                break;
            case R.id.rb_ms_classify_6:
                classifyl(6);
                break;
            case R.id.rb_ms_classify_7:
                classifyl(7);
                break;
            case R.id.rb_ms_classify_8:
                classifyl(8);
                break;
            case R.id.add_usedgoodslayout:
                hideSoftinputyer(addgoodsLayout);
                break;
            case R.id.rb_ms_classify1:
                classifyr(1);
                break;
            case R.id.rb_ms_classify2:
                classifyr(2);
                break;
            case R.id.rb_ms_classify3:
                classifyr(3);
                break;
            case R.id.rb_ms_classify4:
                classifyr(4);
                break;
            case R.id.rb_ms_classify5:
                classifyr(5);
                break;
            case R.id.rb_ms_classify6:
                classifyr(6);
                break;
            case R.id.rb_ms_classify7:
                classifyr(7);
                break;
            case R.id.rb_ms_classify8:
                classifyr(8);
                break;
        }
    }

    //隐藏软键盘的方法
    private void hideSoftinputyer(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        Log.i("windowToken", view.getWindowToken().toString());
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//        if (imm.isAcceptingText())
//            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    View.OnClickListener ocl = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.post_goods_cancel:
                    finish();
                    break;
                case R.id.img_ms_add2:
                    hideSoftinputyer(yourGoodspicture);
                    showWindow();
                    break;
                case R.id.sure_to_postgoods://确定上传
                    sureToPost.setEnabled(false);
                    if (urlpath == null
                            || TextUtils.isEmpty(edittext[0].getText().toString())
                            || TextUtils.isEmpty(edittext[1].getText().toString())
                            || TextUtils.isEmpty(edittext[2].getText().toString())
                            || TextUtils.isEmpty(edittext[3].getText().toString())
                            || TextUtils.isEmpty(edittext[4].getText().toString())) {
                        Toast.makeText(context, "传入数据不完整", Toast.LENGTH_SHORT).show();
                    } else if (!StringUtils.validPhoneNum("2", edittext[4].getText().toString())) {
                        Toast.makeText(context, "请输入正确的电话号码", Toast.LENGTH_SHORT).show();
                    } else {
                        File file1 = new File(urlpath);
                        asynchttpUpload(goodsUrl, file1);
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sureToPost.setEnabled(true);
                        }
                    }, 100);
                    break;
            }
        }
    };

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
//
//                        file = new File(Environment.getExternalStorageDirectory(), getPhotoFileName());
//                        takeIntent.putExtra(MediaStore.EXTRA_OUTPUT,
//                                Uri.fromFile(file));
//                        startActivityForResult(takeIntent, REQUESTCODE_TAKE);
                        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        if (Build.VERSION.SDK_INT >= 23) {
                            int check = ContextCompat.checkSelfPermission(ShopActivity.this, permissions[0]);
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
//                        startActivityForResult(pickIntent, REQUESTCODE_PICK);
                        String[] permissions1 = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        if (Build.VERSION.SDK_INT >= 24) {
                            int check = ContextCompat.checkSelfPermission(ShopActivity.this, permissions1[0]);
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
        pictureWindow.showAtLocation(findViewById(R.id.ershopmainLayout),
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
            uri = FileProvider.getUriForFile(this, "applicationId.fileprovider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        //添加权限
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);//将拍取的照片保存到指定URI
        startActivityForResult(intent, REQUESTCODE_TAKE);

    }

    /**
     * 相册选择
     */
    private void choosePhoto() {
        Intent intent1 = new Intent(Intent.ACTION_PICK, null);
        intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent1, REQUESTCODE_PICK);
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
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * 剪切图片
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
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);


        startActivityForResult(intent, REQUESTCODE_CUTTING);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK)
            return;
        switch (requestCode) {
            case REQUESTCODE_TAKE://拍照的回调
//                startPhotoZoom(Uri.fromFile(file), REQUESTCODE_TAKE, data);
                Uri uri = Uri.fromFile(file);
                if (Build.VERSION.SDK_INT >= 24) {
                    uri = FileProvider.getUriForFile(this, "applicationId.fileprovider", file);
                }
                startPhotoZoom(uri);
                break;
            case REQUESTCODE_PICK://选择图片的回调
                if (data != null)
                    startPhotoZoom(data.getData());
                break;
            case REQUESTCODE_CUTTING://剪切
                if (data != null)
                    setPicToView(data);
//                sureToPost.setEnabled(true);
                break;
        }
    }

    // 获取拍照照片的名字，时间命名
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        //3位随机数
        int kk = new Random().nextInt(900) + 100;
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + "_" + kk + ".jpg";
    }

    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras == null)
            return;

        Bitmap bitmap = extras.getParcelable("data");
        yourGoodspicture.setImageBitmap(bitmap);
        urlpath = FileUtil.saveFile(context, getPhotoFileName(), bitmap);
    }

    private void asynchttpUpload(String url, File myFile) {
        RequestParams params = new RequestParams(url);
        //使用表单上传，否则会导致服务器接收失败
        params.setMultipart(true);
        params.addBodyParameter("touxiang", myFile);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
//                Log.i("postgoods2", s);
                String status = JsonSyncUtils.getJsonValue(s, "status");
                picmsg = JsonSyncUtils.getJsonValue(s, "msg");
                if (status.equals("true")) {
                    postData();
                } else {
                    Toast.makeText(ShopActivity.this, "上传图片异常", Toast.LENGTH_SHORT).show();
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


    //上传卖品文字信息
    private void postData() {
        RequestParams params = new RequestParams(App.CMDURL);
        params.addBodyParameter("cmd", "31");
        params.addBodyParameter("name", edittext[0].getText().toString());
        params.addBodyParameter("tupian", picmsg);
        params.addBodyParameter("ms", edittext[1].getText().toString());
        params.addBodyParameter("jg", edittext[2].getText().toString());
        params.addBodyParameter("user", edittext[3].getText().toString());
        params.addBodyParameter("dh", edittext[4].getText().toString());
        params.addBodyParameter("uid", uid);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                int cw = Integer.parseInt(JsonSyncUtils.getJsonValue(s, "cw"));
                if (cw == 0) {
                    Message m = handler.obtainMessage();
                    m.what = 2;
                    handler.sendMessage(m);
                } else if (cw == 1) {
                    Message m1 = handler.obtainMessage();
                    m1.what = 3;
                    handler.sendMessage(m1);
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

    private int isLogined() {
        int result = 0;
        try {
            result = mSettingPref.getInt("Status", 0);
        } catch (Exception e) {
        }
        return result;
    }

    public synchronized void seleteAllpostgoods() {
        //接口地址，传入cmd参数，返回数据data
        RequestParams params = new RequestParams(App.CMDURL);
        params.addBodyParameter("cmd", "33");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.optJSONArray("data");
                    if (jsonArray == null || jsonArray.length() == 0) {
//                        Message message0 = handler.obtainMessage();
//                        message0.what = 0;
//                        handler.sendMessage(message0);
                    } else {
                        //name: tupian: ms: dh: dz: (uid)
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                            ShopModel shopModel = new ShopModel();
                            shopModel.setGoodsName(jsonObject1.optString("name"));
                            shopModel.setPrice(jsonObject1.optString("jg"));
                            shopModel.setGoodsPicture(jsonObject1.optString("tupian"));
                            shopModel.setGoodsMessages(jsonObject1.optString("ms"));
                            shopModel.setOwnerPhone("卖家电话：" + jsonObject1.optString("dh"));
                            shopModel.setOwnerName("卖家姓名：" + jsonObject1.optString("user"));
                            //商品有id,字符串类型
                            shopModel.setId(jsonObject1.optString("id"));
                            shopModels.add(shopModel);
                        }
                        Message message1 = handler.obtainMessage();
                        message1.what = 1;
                        handler.sendMessage(message1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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

    @Override
    protected void onResume() {
        super.onResume();
        shopModels.clear();
        //加载数据
        seleteAllpostgoods();

        shopAdapter = new ShopAdapter(shopModels, ShopActivity.this);
        shopAdapter.setCallseller(new ShopAdapter.Callseller() {
            @Override
            public void onCall(String str) {
                showDialog(str);
            }
        });
        shopListView.setAdapter(shopAdapter);
        shopListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ShopActivity.this, CommStoreDetailsActivity.class);
                ShopModel bean = shopModels.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("title", bean.getGoodsName());
                bundle.putString("img", bean.getGoodsPicture());
                bundle.putString("detail", bean.getGoodsMessages());
                bundle.putString("user", bean.getOwnerName().replace("卖家姓名：", ""));
                bundle.putString("phone", bean.getOwnerPhone().replace("卖家电话：", ""));
                bundle.putString("price", bean.getPrice());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        shopAdapter.notifyDataSetChanged();

    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
//                case 0:
//                    Toast.makeText(context,"还没有卖家上传卖品",Toast.LENGTH_SHORT).show();
//                    break;
                case 1:
                    shopAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    Toast.makeText(context, "上传成功", Toast.LENGTH_SHORT).show();
                    radioButton1.setChecked(true);
                    clearData();
                    ShopActivity.this.onResume();
                    break;
                case 3:
                    Toast.makeText(context, "上传失败", Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        }
    });

    private void clearData() {
        //清空数据
        for (int i = 0; i < edittext.length; i++) {
            edittext[i].setText("");
        }
        yourGoodspicture.setImageResource(R.drawable.btn_add_img);
    }

    public void onNull(View v) {

    }
}
