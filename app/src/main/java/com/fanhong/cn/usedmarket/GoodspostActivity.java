package com.fanhong.cn.usedmarket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fanhong.cn.R;
import com.fanhong.cn.SampleConnection;
import com.fanhong.cn.synctaskpicture.CastiePostUtils;
import com.fanhong.cn.util.FileUtil;
import com.fanhong.cn.util.HttpUtil;
import com.fanhong.cn.util.NetUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/15.
 */

public class GoodspostActivity extends Activity {
    private SharedPreferences preferences;
    private ImageView yourGoodspicture,back;
    private EditText[] edittext = new EditText[4];
    private Button sureToPost,goCamera,goChoosepicture,cancelChoose;
    private LinearLayout cameraLayout;
    private static final int REQUESTCODE_PICK = 60;		//相册选择
    private static final int REQUESTCODE_TAKE = 61;		//拍照
    private static final int REQUESTCODE_CUTTING = 62;	// 剪切
    private File file;//??????????????
    private Bitmap bitmap; //???????????????bitmap
    private String urlpath;//图片的路径
    private String status;//resultStr中的状态值
    private String picmsg; //resultStr中的图片名
    private String uid;//登录时的id
    //    String goodsUrl = "http://m.wuyebest.com/index.php/App/index/goods2";
    private String goodsUrl = SampleConnection.GOODS_PICTURE_URL;
    //    String cmdUrl = "http://m.wuyebest.com/index.php/App/index";
    private String cmdUrl = SampleConnection.url;
    String resultStr;//上传图片返回的数据
    private Context context;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usedmarketpost);
        init();
    }
    public void init(){
        back = (ImageView) findViewById(R.id.post_goods_cancel);
        yourGoodspicture = (ImageView) findViewById(R.id.your_goodspicture);
        edittext[0] = (EditText) findViewById(R.id.your_goodsname);
        edittext[1] = (EditText) findViewById(R.id.your_goodsmessage);
        edittext[2] = (EditText) findViewById(R.id.your_phone);
        edittext[3] = (EditText) findViewById(R.id.your_address);
        sureToPost = (Button) findViewById(R.id.sure_to_postgoods);

        cameraLayout = (LinearLayout) findViewById(R.id.layout_of_camera);
        goCamera = (Button) findViewById(R.id.start_camera);
        goChoosepicture = (Button) findViewById(R.id.choose_from_sdpicture);
        cancelChoose = (Button) findViewById(R.id.goods_picture_cancel);
        context = getApplicationContext();

        back.setOnClickListener(ocl);
        yourGoodspicture.setOnClickListener(ocl);
        sureToPost.setOnClickListener(ocl);
        goCamera.setOnClickListener(ocl);
        goChoosepicture.setOnClickListener(ocl);
        cancelChoose.setOnClickListener(ocl);

        preferences = getSharedPreferences("Setting", Context.MODE_PRIVATE);
        uid = preferences.getString("UserId","");
        Log.i("xq","userid====>"+uid);
    }
    private int isLogined(){
        int result = 0;
        try{
            result = preferences.getInt("Status", 0);
        }catch (Exception e) {}
        return result;
    }

    View.OnClickListener ocl = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.post_goods_cancel:
                    finish();
                    break;
                case R.id.your_goodspicture:
                    if(cameraLayout.getVisibility()==View.GONE){
                        cameraLayout.setVisibility(View.VISIBLE);
                    }else
                        cameraLayout.setVisibility(View.GONE);
                    break;
                case R.id.start_camera://拍照
                    Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    file = new File(Environment.getExternalStorageDirectory(), getPhotoFileName());
                    takeIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(file));
                    startActivityForResult(takeIntent, REQUESTCODE_TAKE);
                    break;
                case R.id.choose_from_sdpicture://相册选择
                    Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                    pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                    startActivityForResult(pickIntent, REQUESTCODE_PICK);
                    break;
                case R.id.goods_picture_cancel:
                    cameraLayout.setVisibility(View.GONE);
                    break;
                case R.id.sure_to_postgoods://确定上传
                    if(isLogined() == 1){
                        if(urlpath != null){
                            File file1 = new File(urlpath);
                            asynchttpUpload(goodsUrl,file1);
                        }else {
                            Toast.makeText(GoodspostActivity.this,"未传入图片",Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(GoodspostActivity.this,R.string.pleaselogin,Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };
    /**
     * 剪切图片
     * @param uri
     * @param type
     * @param data
     */
    public void startPhotoZoom(Uri uri,int type,Intent data) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
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
        switch (requestCode){
            case REQUESTCODE_TAKE://拍照的回调
                startPhotoZoom(Uri.fromFile(file),REQUESTCODE_TAKE,data);
                break;
            case REQUESTCODE_PICK://选择图片的回调
                if (data != null)
                    startPhotoZoom(data.getData(),REQUESTCODE_PICK,data);
                break;
            case REQUESTCODE_CUTTING://剪切
                if (data == null)
                    return;
                setPicToView(data);
                cameraLayout.setVisibility(View.GONE);
//                sureToPost.setEnabled(true);
                break;
        }
    }
    // 获取拍照照片的名字，时间命名
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras == null)
            return;

        bitmap = extras.getParcelable("data");
        yourGoodspicture.setImageBitmap(bitmap);
        urlpath = FileUtil.saveFile(context, getPhotoFileName(), bitmap);
//        urlpath = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,null,null))+".jpg";
    }

    private void asynchttpUpload(String url,File myFile){
        Log.i("xq","*****path="+url+" myFile="+myFile);
        RequestParams params = new RequestParams();
        try {
            params.put("touxiang", myFile);
            //AsyncHttpClient client = new AsyncHttpClient();
            HttpUtil.post(url, params, new AsyncHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, String content) {
                    dealResult(content);
                }
            });
        } catch(FileNotFoundException e) {
        }
    }
    private void dealResult(String content){
        try {
            JSONObject json = new JSONObject(content);
            String status = json.getString("status");
            String pig = json.getString("msg");
            picmsg = pig;
            if(status.equals("true")){
                new Thread(){
                    @Override
                    public void run() {
                        postData();
                    }
                }.start();
            }
        } catch (Exception e) {

        }
    }


    //上传卖品文字信息
    private void postData() {
        BufferedReader br = null;
        try {
            URL url = new URL(cmdUrl);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setConnectTimeout(5000);
            http.setReadTimeout(5000);
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setDoInput(true);
            http.setUseCaches(false);
            String content = "cmd=" + 31 + "&name=" + edittext[0].getText() + "&tupian=" + picmsg +
                    "&ms=" + edittext[1].getText() + "&dh=" + edittext[2].getText() + "&dz=" + edittext[3].getText() +
                    "&uid=" + uid;
            OutputStream os = http.getOutputStream();
            os.write(content.getBytes());

            if (http.getResponseCode() == 200) {
                br = new BufferedReader(new InputStreamReader(http.getInputStream(), "utf-8"));
                StringBuffer sb = new StringBuffer();
                String str;
                while ((str = br.readLine()) != null) {
                    sb.append(str);
                }
                JSONObject object = new JSONObject(sb.toString());
                int cmd = object.optInt("cmd");
                int cw = object.optInt("cw");
                Log.e("result","cmd="+cmd+"cw="+cw);
                if(cw == 0){
                    Message m = handler.obtainMessage();
                    m.what = 0;
                    handler.sendMessage(m);
                }else if(cw == 1){
                    Message m1 = handler.obtainMessage();
                    m1.what = 1;
                    handler.sendMessage(m1);
                }
            }
        } catch(MalformedURLException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            int k = message.what;
            switch (k){
                case 0:
                    Toast.makeText(GoodspostActivity.this,"上传成功",Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 1:
                    Toast.makeText(GoodspostActivity.this,"上传失败",Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(GoodspostActivity.this,"卖品图片上传异常",Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        }
    });
}
