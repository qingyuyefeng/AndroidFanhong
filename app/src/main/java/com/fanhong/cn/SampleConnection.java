package com.fanhong.cn;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/*
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
*/


public class SampleConnection extends Application{
	private final static String TAG = "FANHONG";
	private final static int CONNECTION_SUCCESS = 0;
	private final static int CONNECTION_FAIL = 1;
	private final int TIMEOUT = 60 * 1000;
	public static String USER = "";   //登录名
	public static String PHONENUMBER = "";
	public static int USER_STATE = 0;  //登录成功1，失败为0
	public static String ALIAS = "";   //昵称
	public static String PASSWORD = "";   //登录密码
	public static String LOGO_URL = "";   //头像的url
	public static String TOKEN = "";   //融云Token码
	public static String CHATROOM = "";   //聊天室ID
	private SampleActivity mSampleActivity;
	private SamplePreferenceActivity mSamplePreferenceActivity;
	private int mType;
	//private HttpClient mClient;
	private MyHandler mHandler;
	public static final String WEB_SITE="http://m.wuyebest.com";
	public static final String UPDATE_SERVER = "http://m.wuyebest.com/public/apk/";//UPDATE_SERVER = "http://www.safphone.com:801/apk/";
	public static final String UPDATE_APKNAME = "FanHong.apk";
	public static final String UPDATE_VERJSON = "ver_FanHong.json";
	public static final String UPDATE_SAVENAME = "Update_FanHong.apk";
	//final String url = "http://www.safphone.com:801/appAexec.do";
	//final String url = "http://mybama.cn/c.ashx";
	//统一路径
	public static final String url = "http://m.wuyebest.com/index.php/App/index";
	//用户头像上传服务器的路径
	public static final String HEAD_PICTURE_URL = "http://m.wuyebest.com/index.php/App/index/upapp";
	//public static final String HEAD_PICTURE_URL = "http://192.168.0.103/upload.php";
	//评论图片上传路径
	public static final String PINGLUN_PICTURE_URL = "http://m.wuyebest.com/index.php/App/index/plup";
	//门禁图片上传路径
	public static final String DOOR_PICTURE_URL = "http://m.wuyebest.com/index.php/App/index/user_xx";
	//二手卖品图片上传路径
	public static final String GOODS_PICTURE_URL = "http://m.wuyebest.com/index.php/App/index/goods2";
	//开门禁所需访问的路径
//	public static final String OPEN_LOCKED_URL = "http://fhtxjs.imwork.net:30626/BEST1.0.1/index.php/AppAdmin/Index/yjkd";
	public static final String OPEN_LOCKED_URL = "http://m.wuyebest.com/index.php/App/index/yjkm";

	public static boolean terminal_reboot = false;
	//支付成功广播
	public static final String MYPAY_RECEIVER = "com.fanhong.cn.payresult";
	private Context mcontext;
	//构造器type..
	public SampleConnection(SampleActivity context, int type) {
		mSampleActivity = context;
		mcontext = context;
		mType = type;
		mHandler = new MyHandler(this);
		resetUser(context);
	}

	public SampleConnection(SamplePreferenceActivity context, int type) {
		mSamplePreferenceActivity = context;
		mType = type;
		mHandler = new MyHandler(this);
		resetUser(context);
	}
/*
	public void resetUser1(Activity context){
		if(USER == null || USER.isEmpty())
		{
			SharedPreferences mSettingPref = context.getSharedPreferences("Setting", Context.MODE_PRIVATE);
			final String username = mSettingPref.getString("Name", "");;
			final String password = mSettingPref.getString("Password", "");


			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						HttpClient mClient1 = new DefaultHttpClient();
						HttpConnectionParams.setConnectionTimeout(mClient1.getParams(), TIMEOUT);
						HttpConnectionParams.setSoTimeout(mClient1.getParams(), TIMEOUT);
						//StringEntity entity = new StringEntity(info1.toString(), "gbk");

						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("cmd", "5"));
						params.add(new BasicNameValuePair("name", username));
						params.add(new BasicNameValuePair("password", password));
						Log.e("hu","*111*****resetUser");
						HttpPost request = new HttpPost(url);
						//request.addHeader("User-Agent", USER);
						//request.setEntity(entity);
						request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
						HttpResponse httpResponse = mClient1.execute(request);

						int httpCode = httpResponse.getStatusLine().getStatusCode();

						//Log.i(TAG, "Connection2222>>>"+"httpCode="+httpCode);

						if (httpCode == HttpStatus.SC_OK) {
							String httpRes = EntityUtils.toString(httpResponse.getEntity());
							//Log.e("hu","*111*****post  httpRes="+httpRes);
							int cmd = -1;
							int result = -1;
							int state = 0;
							String user = "";
							String phonenumber = "";
							String alias = "";
							try {
								httpRes = URLDecoder.decode(httpRes, HTTP.UTF_8);
								JSONObject json = new JSONObject(httpRes);
								cmd = json.getInt("cmd");
								result = json.getInt("cw");
								//state = json.getInt("state");
								user = json.get("name").toString();
								try{
									alias = json.get("user").toString();
								} catch (Exception e) {}
								try {
									phonenumber = json.get("phonenumber").toString();
								} catch (Exception e) {}
							}catch (Exception e) {}
							if (cmd == 6 && result == 0 && !user.isEmpty()) {
								USER = user;
								//PHONENUMBER = phonenumber;
								//USER_STATE = state;
								ALIAS = alias;
								//Log.e("hu","********bbbbbbb4 httpRes="+httpRes);
							}
						}
						mClient1.getConnectionManager().shutdown();
						mClient1 = null;
					} catch (Exception e) {
						//handlerConnectfail();
					}
				}
			}).start();
		}
	}*/

	public void resetUser(Activity context){
		if(USER == null || USER.isEmpty()) {
			SharedPreferences mSettingPref = context.getSharedPreferences("Setting", Context.MODE_PRIVATE);
			final String username = mSettingPref.getString("Name", "");
			final String password = mSettingPref.getString("Password", "");

			RequestQueue mQueue = Volley.newRequestQueue(mcontext);
			StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
				@Override
				public void onResponse(String response) {
					try {
						String httpRes = URLDecoder.decode(response, "UTF-8");
						JSONObject json = new JSONObject(httpRes);
						int cmd = json.getInt("cmd");
						int result1 = json.getInt("cw");
						String user = json.getString("name");
						String alias1 = "", phonenumber;
						try {
							alias1 = json.get("user").toString();
						} catch (Exception e) {
						}
						try {
							phonenumber = json.get("phonenumber").toString();
						} catch (Exception e) {
						}
						if (cmd == 6 && result1 == 0 && !user.isEmpty()) {
							USER = user;
							ALIAS = alias1;
						}
					} catch (Exception e) {
					}
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError arg0) {
					// TODO Auto-generated method stub
				}
			}) {
				@Override
				protected Map<String, String> getParams() throws AuthFailureError {
					Map<String, String> map2 = new HashMap<>();
					map2.put("cmd", "5");
					map2.put("name", username);
					map2.put("password", password);
					return map2;
				}
			};
			mQueue.add(stringRequest);
		}
	}

	/*public void connectService1(final Map<String, Object> map) {
		if (!getNetworkEnable()) {
			if (mSamplePreferenceActivity != null) {
				mSamplePreferenceActivity.connectFail(mType);
			} else {
				mSampleActivity.connectFail(mType);
			}
			return;
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {//android6.0不支持Httpclient,所以编译版本不能是6.0以上
					mClient = new DefaultHttpClient();
					HttpConnectionParams.setConnectionTimeout(mClient.getParams(), TIMEOUT);
					HttpConnectionParams.setSoTimeout(mClient.getParams(), TIMEOUT);
				//	mClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);  //读取超时
				//	mClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);

					List<NameValuePair> params = new ArrayList<NameValuePair>();


			     // 迭代Map
			       // for (Map.Entry<String, String> entry : map.entrySet()) {
			       //     params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			       // }

			        for (String key : map.keySet()) {
						params.add(new BasicNameValuePair(key, (String) map.get(key)));
					}
					HttpPost request = new HttpPost(url);
				//	request.addHeader("User-Agent", USER);
				//	request.setEntity(new StringEntity(json.toString(), HTTP.UTF_8));
					request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
					Log.e("hu","******post: entity="+EntityUtils.toString(request.getEntity()));

					HttpResponse httpResponse = mClient.execute(request);

					int httpCode = httpResponse.getStatusLine().getStatusCode();
					Log.i(TAG, "Connection>>>"+"httpCode="+httpCode);

					if (httpCode == HttpStatus.SC_OK) {
						String str = EntityUtils.toString(httpResponse.getEntity());
					//	Log.e("hu","******post2 get: json="+str);
						handlerConnectSuccess(str);
					} else {
						handlerConnectfail();
					}
				} catch (Exception e) {
					//handlerConnectfail();
				} finally {
					close();
				}
			}
		}).start();
	}*/

	/*public void connectService4(Map<String, Object> map) {
		if (!getNetworkEnable()) {
			if (mSamplePreferenceActivity != null) {
				mSamplePreferenceActivity.connectFail(mType);
			} else {
				mSampleActivity.connectFail(mType);
			}
			return;
		}
		//因为okHttp支持的一些API需要java7.0或者android M ,如果运行在低版本下okHttp就会报这些警告，直接忽略就可以了
		OkHttpClient mOkHttpClient = new OkHttpClient();
		FormEncodingBuilder builder = new FormEncodingBuilder();
		if (null != map && !map.isEmpty())
			for (String key : map.keySet()) {
				builder.add(key, map.get(key)+"");
			}
	//	if(UserInfoUtil.getInstance().getAuthKey()!=null) {
	//		builder.add("authKey", UserInfoUtil.getInstance().getAuthKey());
	//	}
	//	Log.i("gaolei", " authKey------------------"+UserInfoUtil.getInstance().getAuthKey());
		Log.i("hu","post:"+builder.toString());
		Request request = new Request.Builder()
				.url(url)
				.post(builder.build())
				.build();
		try {
			mOkHttpClient.setConnectTimeout(5000, TimeUnit.MILLISECONDS);

			mOkHttpClient.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Request request, IOException e) {
					Log.i("hu", "NetRequest-----------onFailure----------------" + e.getMessage());
				//	netRequestIterface.exception(e, requestUrl);
					handlerConnectfail();
				}

				@Override
				public void onResponse(final Response response) throws IOException {
					String result = response.body().string();
					//Log.i("hu", "onResponse----------------------" + result);
				//	netRequestIterface.changeView(result, requestUrl);
					handlerConnectSuccess(result);
				}
			});
		}catch (Exception e){

		}
	}*/

	public void connectService1(Map<String, Object> map) {
		if (!getNetworkEnable()) {
			/*if (mSamplePreferenceActivity != null) {
				mSamplePreferenceActivity.connectFail(mType);
			} else {
				mSampleActivity.connectFail(mType);
			}*/
			if (mSamplePreferenceActivity != null) {
				Toast.makeText(mSamplePreferenceActivity,R.string.wlbky,Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(mSampleActivity,R.string.wlbky,Toast.LENGTH_LONG).show();
			}
			return;
		}
		final Map<String, Object> map1 = map;
		RequestQueue mQueue = Volley.newRequestQueue(mcontext);
		StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				//try {
				//使用JSONObject给response转换编码
				//JSONObject jsonObject = new JSONObject(response);
				//responseText.setText(jsonObject.toString());
//				Log.e("hu","*3*****post recv:"+response);
				handlerConnectSuccess(response.trim());
				//} catch (JSONException e) {
				//	e.printStackTrace();
				//}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				// TODO Auto-generated method stub
				handlerConnectfail();
			}
		}){
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String,String> map2 = new HashMap<>();
				for (String key : map1.keySet()) {
					map2.put(key,  map1.get(key).toString());
				}
				return map2;
			}
		};
		mQueue.add(stringRequest);
	}

	public void close() {
		//	if (mClient != null) {
		//		mClient.getConnectionManager().shutdown();
		//		mClient = null;
		//	}
	}

	public void handlerConnectSuccess(String httpRes) {
		Bundle bundle = new Bundle();
		bundle.putString("HTTPRES", httpRes);
		Message localMessage = new Message();
		localMessage.what = CONNECTION_SUCCESS;
		localMessage.setData(bundle);
		mHandler.sendMessage(localMessage);
	}

	public void handlerConnectfail() {
		Message localMessage = new Message();
		localMessage.what = CONNECTION_FAIL;
		mHandler.sendMessage(localMessage);
	}

	private boolean getNetworkEnable() {
		try {
			ConnectivityManager cm;
			if (mSamplePreferenceActivity != null) {
				cm = (ConnectivityManager)mSamplePreferenceActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
			} else {
				cm = (ConnectivityManager)mSampleActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
			}
			if (cm != null) {
				NetworkInfo[] ni = cm.getAllNetworkInfo();
				if (ni != null) {
					for (int i = 0; i < ni.length; i++) {
						if (ni[i].getState() == NetworkInfo.State.CONNECTED) {
							return true;
						}
					}
				}
			}
			return false;
		} catch(Exception e) {
			return false;
		}
	}

	private boolean getNetworkEnable1() {
		// 判断网络是否连接
		try {
			ConnectivityManager cm;
			if (mSamplePreferenceActivity != null) {
				cm = (ConnectivityManager)mSamplePreferenceActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
			} else {
				cm = (ConnectivityManager)mSampleActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
			}
			if (cm != null) {
				NetworkInfo mNetworkInfo = cm.getActiveNetworkInfo();
				if (mNetworkInfo != null) {
					return mNetworkInfo.isAvailable();
				}
				return false;
			}
			return false;
		} catch(Exception e) {
			return false;
		}
	}

	//drawable转为bitmap
	public static Bitmap drawableToBitmap(Drawable drawable) {
		Bitmap bitmap = Bitmap.createBitmap(
				drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight(),
				drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
						: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		//canvas.setBitmap(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	private static class MyHandler extends Handler {
		WeakReference<SampleConnection> mConnectionReference;

		MyHandler(SampleConnection connection) {
			mConnectionReference = new WeakReference<SampleConnection>(connection);
		}

		@Override
		public void handleMessage(Message msg) {
			final SampleConnection connection = mConnectionReference.get();
			if (connection == null) {
				return;
			}
			switch (msg.what) {
				case CONNECTION_SUCCESS:
					Bundle bundle = msg.getData();
					String httpRes = bundle.getString("HTTPRES");
					if (httpRes == null) {
						httpRes = "";
					}
					//	Log.e("hu","*******httpRes="+httpRes);
					try {
						httpRes = URLDecoder.decode(httpRes, "UTF-8");
					} catch (Exception e) {
//						Log.e("hu","*****e="+e.toString());
					}
//					Log.i(TAG, "Connection>>>"+"httpResponse="+httpRes);
					try {
						JSONObject json = new JSONObject(httpRes);
//						Log.e("hu","******post get:  json="+json.toString());
						if (connection.mSamplePreferenceActivity != null) {
							connection.mSamplePreferenceActivity.connectSuccess(json, connection.mType);
						} else {
							//Log.e("hu","******connection.mSampleActivity.connectSuccess");
							connection.mSampleActivity.connectSuccess(json, connection.mType);
						}
					} catch (Exception e) {
						if (connection.mSamplePreferenceActivity != null) {
							connection.mSamplePreferenceActivity.connectFail(connection.mType);
						} else {
							connection.mSampleActivity.connectFail(connection.mType);
						}
					}
					break;
				case CONNECTION_FAIL:
					if (connection.mSamplePreferenceActivity != null) {
						connection.mSamplePreferenceActivity.connectFail(connection.mType);
					} else {
						connection.mSampleActivity.connectFail(connection.mType);
					}
					break;
			}
		}
	}

	public static String getUrlFromUri(Uri uri){
		return  WEB_SITE+uri.getPath();
	}
}
