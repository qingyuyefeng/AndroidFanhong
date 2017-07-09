package com.fanhong.cn;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.fanhong.cn.tools.BitmapUtils;
import com.fanhong.cn.tools.CleanCacheManager;
import com.fanhong.cn.tools.FileUtils;
import com.fanhong.cn.util.HttpUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.zhy.autolayout.utils.AutoUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class AddGardenActivity extends SampleActivity implements OnClickListener,TextWatcher{
	private SharedPreferences mSettingPref;
	private String userid;
	private SampleConnection mSafoneConnection;
	private Context mcontext;
	private EditText et_comment_content;
	private TextView tv_garden;
	private Button btn_submit;
	private Button btn_selectgarden;
	//String iid,good;
	private ImageView iv_choose_goods_pic;
	private HorizontalScrollView hsv_comment_imgs;
	//private ArrayList<String> mSelectPath;//选择的图片地址集
	public static final String KEY_IMAGE_LIST = "imageList";
	public static final String KEY_CURRENT_INDEX = "currentIndex";
	private final int REQUEST_CODE_PICTURE = 1;
	private final int RESULT_CODE_LARGE_IMAGE = 1;
	private final int MAX_PIC = 6; //晒单图片最多选择
	private List<String> imageUrls = new ArrayList<>();//所有晒图图片路径
	private String picturelist;  //上传的图片名字集
	private int picturecount;
	private String gardenName,gardenId,gardenProperty;

	public synchronized void connectFail(int type) {
		btn_submit.setEnabled(true);
		btn_submit.setText(this.getString(R.string.submit));
		//SampleConnection.USER = "";
		//SampleConnection.USER_STATE = 0;
		//Toast.makeText(this, getString(R.string.login_fail), Toast.LENGTH_SHORT).show();
	}

	public synchronized void connectSuccess(JSONObject json, int type) {
		int cmd = -1;
		int result = -1;
		String str;
		try {
			str = json.getString("cmd");
			cmd = Integer.parseInt(str);
			str = json.getString("cw");
			result = Integer.parseInt(str);
		} catch (Exception e) {
			connectFail(type);
			return;
		}
		if(cmd == 40){
			if(result == 0){
				//setData(json);
				Intent intent = new Intent();
				//intent.putExtra("iid", iid);
				this.setResult(24, intent);
				this.finish();
				Toast.makeText(this, "添加小区门禁提交成功！", Toast.LENGTH_SHORT).show();
			}else{
				btn_submit.setEnabled(true);
				btn_submit.setText(this.getString(R.string.submit));
				Toast.makeText(this, "数据异常!添加小区门禁提交失败！", Toast.LENGTH_SHORT).show();
			}
		} else {
			connectFail(type);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addgarden);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			//透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			//透明导航栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		mcontext = getApplicationContext();
		mSettingPref = getSharedPreferences("Setting", Context.MODE_PRIVATE);
		userid = mSettingPref.getString("UserId", "");

		et_comment_content = (EditText)findViewById(R.id.et_comment_content);
		tv_garden = (TextView)findViewById(R.id.tv_garden);
		iv_choose_goods_pic = (ImageView) findViewById(R.id.iv_choose_goods_pic);
		hsv_comment_imgs = (HorizontalScrollView) findViewById(R.id.hsv_comment_imgs);
		iv_choose_goods_pic.setOnClickListener(this);
		et_comment_content.addTextChangedListener(this);
		btn_submit = (Button) findViewById(R.id.btn_submit);
		btn_submit.setOnClickListener(this);
		btn_selectgarden = (Button) findViewById(R.id.btn_selectgarden);
		btn_selectgarden.setOnClickListener(this);

		Button titleBackImageBtn = (Button)findViewById(R.id.btn_back);
		titleBackImageBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CleanCacheManager.cleanExternalCache(AddGardenActivity.this);
				finish();
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mSafoneConnection != null) {
			mSafoneConnection.close();
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		//清除临时压缩图片文件
		CleanCacheManager.cleanExternalCache(this);
	}

	private void getDoorsInfo(){
		String content = et_comment_content.getText().toString();
		Log.i("hu","*******getDoorsInfo()");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cmd", "39");
		map.put("uid", userid);
		map.put("xid", gardenId);
		map.put("dizhi", content);  //楼栋号
		map.put("xinxi", picturelist);

		if(mSafoneConnection == null)
			mSafoneConnection = new SampleConnection(AddGardenActivity.this, 0);
		mSafoneConnection.connectService1(map);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
			case 21:  //登录返回
				break;
			case RESULT_CODE_LARGE_IMAGE:
				//晒单大图页返回, 重新设置晒单图片
				handleCommentPicList(imageUrls = data.getStringArrayListExtra(KEY_IMAGE_LIST), true);
				break;
			case 51:  //选择小区返回
				Bundle bundle = data.getExtras();
				if(bundle != null){
					gardenName = bundle.getString("gardenName");
					gardenId = bundle.getString("gardenId");
					gardenProperty = bundle.getString("gardenProperty");
					Log.e("hu","********返回 gardenName="+gardenName+" gardenId="+gardenId+" gardenProperty="+gardenProperty);
					tv_garden.setText(gardenName);
				}
				break;
		}
		if(requestCode == REQUEST_CODE_PICTURE){
			if(resultCode == RESULT_OK){
				// 获取返回的图片列表
				List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
				imageUrls.addAll(path);
				StringBuilder sb = new StringBuilder();
				for(String p: path){
					sb.append(p);
					sb.append("\n");
				}
				Log.e("hu","********图片地址："+sb.toString());
				handleCommentPicList(imageUrls, false);
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.btn_submit:
				//门禁提交
				validateComment();
				break;
			case R.id.iv_choose_goods_pic:
				//选择晒单图片, 调出图片选择界面
				Log.i("hu","******3333");
				MultiImageSelector.create().count(MAX_PIC - imageUrls.size()).start(this, REQUEST_CODE_PICTURE);
				Log.i("hu","******3333  1");
				break;
			case R.id.btn_selectgarden:
				Intent intent2 = new Intent(this, GardenSelecterActivity.class);
				startActivityForResult(intent2,12);
				break;
		}
	}

	/**
	 * 门禁提交
	 */
	private void validateComment() {
		String garden = tv_garden.getText().toString();
		if (TextUtils.isEmpty(garden)) {
			Toast.makeText(mcontext, "请选择小区", Toast.LENGTH_SHORT).show();
			return;
		}
		String content = et_comment_content.getText().toString();
		if (TextUtils.isEmpty(content)) {
			Toast.makeText(mcontext, "楼栋号不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		btn_submit.setEnabled(false);
		btn_submit.setText("提交中...");
		picturelist = "";
		if( imageUrls.size() > 0 ) {
			submitPicture();
		}else{
			Toast.makeText(mcontext, "请选择需要的图片", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
								  int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		String content = et_comment_content.getText().toString();
		if (content.length() >= 255) {
			Toast.makeText(mcontext, "评论内容不能多于255个字符", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	/**
	 * 处理选择的评价图片
	 *
	 * @param paths      图片的路径集合
	 * @param isFromBack 是否来自LargeImageActivity返回
	 */
	private void handleCommentPicList(final List<String> paths, boolean isFromBack) {
		Log.i("hu","******handleCommentPicList");
		LinearLayout rootview = new LinearLayout(mcontext);
		View commentView;
		SimpleDraweeView sdv_pic;
		for (int i = 0, len = paths.size(); i < len; i++) {
			commentView = getLayoutInflater().inflate(R.layout.order_comment_pic_item, null);
			sdv_pic = (SimpleDraweeView) commentView.findViewById(R.id.sdv_pic);
			if (isFromBack) {
				//来自LargeImageActivity
				sdv_pic.setImageURI(Uri.parse("file://" + paths.get(i)));
			} else {
				//来自图片选择器
				Log.i("hu","******缓存图片 i="+i);
				String path = FileUtils.getCachePath(mcontext);//获取app缓存路径来存放临时图片
				Log.i("hu","******缓存图片 path="+path);
				BitmapUtils.compressImage(paths.get(i), path, 95);
				sdv_pic.setImageURI(Uri.parse("file://" + path));
				imageUrls.set(i, path);
			}


			StringBuilder sb = new StringBuilder();
			for(String p: paths){
				sb.append(p);
				sb.append("\n");
			}
			Log.e("hu","******2222*图片地址："+sb.toString());

			final int finalI = i;
			sdv_pic.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//点击HorizontalScrollView里的晒单图进入图片详情页
					Intent intent = new Intent(AddGardenActivity.this, CommentLargeImageActivity.class);
					intent.putExtra(KEY_CURRENT_INDEX, finalI);
					intent.putStringArrayListExtra(KEY_IMAGE_LIST, (ArrayList<String>) paths);
					startActivityForResult(intent, REQUEST_CODE_PICTURE);
				}
			});
			AutoUtils.auto(commentView);
			rootview.addView(commentView);
		}
		hsv_comment_imgs.removeAllViews();
		hsv_comment_imgs.addView(rootview);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			CleanCacheManager.cleanExternalCache(this);
		}
		return super.onKeyDown(keyCode, event);
	}

	private void submitPicture(){
		String path = SampleConnection.DOOR_PICTURE_URL;
		picturecount = imageUrls.size();
		for (int i = 0, len = imageUrls.size(); i < len; i++) {
			Log.e("hu","******i="+i+" imageUrls.get()="+imageUrls.get(i));
			String oldpath = imageUrls.get(i);
			String newpath = renamePicture(oldpath);
			File file = new File(newpath);
			asynchttpUploadPicture(path,file);
		}
	}

	private String renamePicture(String oldPath){
		//Log.e("hu","******path1="+oldPath);
		String dir = oldPath.substring(0,oldPath.lastIndexOf('/'));
		//Log.e("hu","******dir="+dir);
		String suffix = oldPath.substring(oldPath.lastIndexOf('.'));
		//Log.e("hu","******suffix="+suffix);
		String newname = dir + "/";
		newname = newname + getPictureNewName();
		newname = newname + suffix;
		Log.e("hu","******newname="+newname);
		File file = new File(oldPath);
		file.renameTo(new File(newname));
		return newname;
	}

	private String getPictureNewName(){
		long t1 = System.currentTimeMillis() / 1000;
		Random r = new Random();
		String str = String.valueOf(t1) + r.nextInt(1000);
		return str;
	}

	private void asynchttpUploadPicture(String path,File myFile){
		Log.e("hu","***上传图片**path="+path+" myFile="+myFile);
		RequestParams params = new RequestParams();
		try {
			params.put("xinxi", myFile);
			HttpUtil.post(path, params, new AsyncHttpResponseHandler(){
				@Override
				public void onSuccess(int statusCode, String content) {
					super.onSuccess(statusCode, content);
					Log.e("hu","**********statusCode="+statusCode+" content="+content);
					//Toast.makeText(AssessActivity.this, "上传成功！", Toast.LENGTH_LONG).show();
					dealResult(content);
				}
			});
		} catch(FileNotFoundException e) {
			Log.e("hu","***上传图片失败************e="+e.toString());
		}
	}

	private void dealResult(String content){
		try {
			JSONObject json = new JSONObject(content);
			String status = json.getString("status");
			String pig = json.getString("msg");
			if(status.equals("true")){
				if(picturecount != imageUrls.size()) {
					picturelist = picturelist + ",";
				}
				picturelist = picturelist + pig;
				picturecount--;
				if(picturecount == 0){
					//上传图片完成
					getDoorsInfo();
				}
			}
		} catch (Exception e) {

		}
	}
}
