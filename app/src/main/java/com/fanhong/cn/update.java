package com.fanhong.cn;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * 版本更新
 * @author zk
 *
 */
public class update extends Activity implements OnClickListener{
	/** Called when the activity is first created. */
	Button btn_update,btn_cancle;
	//ListViewset lv_setlist;
	TextView tv_updateinfo;
	RelativeLayout Rl_bomup,Rl_seekbar;
	SeekBar sk_seekBar;
	LinearLayout ll_update;
	String sNowversion;
	int sNowverCode;
	private String currentFilePath = "";
	private String currentTempFilePath = "";
	private String fileEx="";
	private String fileNa="";
	String sUpdateUrl = "";
	Context context;
	private int newVerCode = 0;
	private String newVerName = "";
	private String strUpdateMsg = "";
	private Button titleBackImageBtn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			//透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			//透明导航栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		context = getApplicationContext();

		titleBackImageBtn = (Button)findViewById(R.id.btn_back);
		titleBackImageBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//finish();
				backFun();
			}
		});

		Rl_bomup = (RelativeLayout)findViewById(R.id.Rl_bomup);
		Rl_bomup.setVisibility(View.GONE);
		Rl_seekbar = (RelativeLayout)findViewById(R.id.Rl_seekbar);
		Rl_seekbar.setVisibility(View.GONE);
		sk_seekBar = (SeekBar)findViewById(R.id.sk_seekBar);
		sk_seekBar.setEnabled(false);
		ll_update = (LinearLayout)findViewById(R.id.ll_update);
		ll_update.setVisibility(View.GONE);

		btn_update = (Button)findViewById(R.id.btn_update);
		btn_update.setOnClickListener(this);
		btn_update.setSelected(true);//选中
		btn_cancle = (Button)findViewById(R.id.btn_cancle);
		btn_cancle.setOnClickListener(this);
		btn_cancle.setSelected(true);//选中
		tv_updateinfo = (TextView)findViewById(R.id.tv_updateinfo);
		try {
			sNowversion = getVersionName();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sNowverCode = getVersionCode();
		sUpdateUrl = SampleConnection.UPDATE_SERVER+SampleConnection.UPDATE_APKNAME;
		Log.i("hu","*******sNowversion="+sNowversion+" sNowverCode="+sNowverCode);
		new getUpdate().execute();
	}

	void backFun()
	{
		update.this.finish();
		Intent mainIntent = new Intent(context, FragmentMainActivity.class);
		ActivityManager am = (ActivityManager) context
				.getSystemService(ACTIVITY_SERVICE);
		List<RunningTaskInfo> appTask = am.getRunningTasks(1);
		if (appTask.size() <= 0
				|| !appTask.get(0).baseActivity
				.equals(mainIntent.getComponent())) {
			startActivity(new Intent(update.this, LogoActivity.class));
		}
	}

	public void onClick(View view) {//按钮监听方法
		if(view.getId()==R.id.btn_cancle){
			backFun();
		}
		else if(view.getId()==R.id.btn_update){
			bgupdate();
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			backFun();
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
			case RESULT_OK:
				break;
			default:
				break;
		}
	}

	private class getUpdate extends AsyncTask<Object, Object, Object> {

		@Override
		protected void onProgressUpdate(Object... progress) {

		}

		@Override
		protected void onPostExecute(Object result){
			try
			{
				Rl_bomup.setVisibility(View.GONE);
				String sresult = (String)result;
				if(!sresult.equals("")){
					String info = update.this.getResources().getString(R.string.ver1)+newVerName+"\n";
					info += update.this.getResources().getString(R.string.ver2)+"\n\n"+strUpdateMsg;
					tv_updateinfo.setText(info);
					ll_update.setVisibility(View.VISIBLE);
				}
				else{
					tv_updateinfo.setText(update.this.getResources().getString(R.string.update_zwgx));
				}
			}
			catch (Exception e) {

			}
		}
		@Override
		protected void onPreExecute (){
			Rl_bomup.setVisibility(View.VISIBLE);
			if(!isConnectNet()){
				//	Toast.makeText(update.this,R.string.carsruntimebaidu_wlbky,Toast.LENGTH_LONG).show();
			}
		}
		@Override
		protected Object doInBackground(Object... arg0) {
			boolean result = getServerVerCode();
			return result==true?"1":"";
		}
	}

	private void bgupdate(){
		if(!isConnectNet()){
			//	Toast.makeText(update.this,R.string.carsruntimebaidu_wlbky,Toast.LENGTH_LONG).show();
			return;
		}
		Rl_seekbar.setVisibility(View.VISIBLE);
		btn_update.setEnabled(false);
		String strURL = sUpdateUrl;
		//tv_updateinfo.setText("更新文件下载中....");
        /*取得欲安装程序之文件名称*/
		fileEx = strURL.substring(strURL.lastIndexOf(".")
				+1,strURL.length()).toLowerCase();
		fileNa = strURL.substring(strURL.lastIndexOf("/")
				+1,strURL.lastIndexOf("."));
		getFile(strURL);

	}

	/* 处理下载URL文件自定义函数 */
	private void getFile(final String strPath){
		try
		{
			if (strPath.equals(currentFilePath) )
			{
				getDataSource(strPath);
			}
			currentFilePath = strPath;
			Runnable r = new Runnable()
			{
				public void run()
				{
					try
					{
						getDataSource(strPath);
					}
					catch (Exception e)
					{
						Message message = new Message();
						message.what = 1;
						handler.sendMessage(message);
					}
				}
			};
			new Thread(r).start();
		}
		catch(Exception e)
		{
			//	tv_updateinfo.setText(ApplicationUtil.getResourcesText(context, R.string.update_hqwjcc));
			Rl_seekbar.setVisibility(View.GONE);
			btn_update.setEnabled(true);
		}
	}

	final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					// 	tv_updateinfo.setText(ApplicationUtil.getResourcesText(context, R.string.update_hqwjcc));
					btn_update.setEnabled(true);

					break;
			}
			super.handleMessage(msg);
		}
	};
	/*取得远程文件*/
	private void getDataSource(String strPath) throws Exception
	{
		if (!URLUtil.isNetworkUrl(strPath))
		{
			//  	tv_updateinfo.setText(ApplicationUtil.getResourcesText(context, R.string.update_cwurl));
		}
		else
		{
	      /*取得URL*/
			URL myURL = new URL(strPath);
	      /*创建连接*/
			URLConnection conn = myURL.openConnection();
			conn.connect();
	      /*InputStream 下载文件*/
			InputStream is = conn.getInputStream();
			sk_seekBar.setMax(conn.getContentLength());
			if (is == null)
			{
				throw new RuntimeException("stream is null");
			}

			File myTempFile;
			//检查手机上是否有外部存储卡
			boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
			if(sdCardExist)
			{
				//下载文件到SD卡
				File dir = new File(Environment.getExternalStorageDirectory(),"/jjyl/");
				if(!dir.exists()){
					dir.mkdir();
				}
				myTempFile = new File(dir,fileNa+"."+fileEx);
			}
			else{
				throw new RuntimeException("cannot find SDcard!");
	    	  /*if(android.os.Build.VERSION.SDK_INT>15){
	    		  throw new RuntimeException("cannot find SDcard!");
	    	  }
	    	  else{
	    		  创建临时文件
	    	      myTempFile = File.createTempFile(fileNa, "."+fileEx);
	    	  }	    	  */
			}
	      /*创建临时文件*/
			//File myTempFile = File.createTempFile(fileNa, "."+fileEx);
	      /*取得站存盘案路径*/
			currentTempFilePath = myTempFile.getAbsolutePath();
	      /*将文件写入暂存盘*/
			FileOutputStream fos = new FileOutputStream(myTempFile);
			byte buf[] = new byte[128];
			int total=0;
			do
			{
				int numread = is.read(buf);
				if (numread <= 0)
				{
					break;
				}
				total+= numread;

				//获取当前下载量

				sk_seekBar.setProgress(total);
				fos.write(buf, 0, numread);
			}while (true);
	      /*打开文件进行安装*/
			openFile(myTempFile);
			update.this.finish();
			try
			{
				is.close();
			}
			catch (Exception ex)
			{

			}
		}
	}

	/* 在手机上打开文件的method */
	private void openFile(File f)
	{
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);

	    /* 调用getMIMEType()来取得MimeType */
		String type = getMIMEType(f);
	    /* 设置intent的file与MimeType */
		intent.setDataAndType(Uri.fromFile(f),type);
		startActivity(intent);
	}

	/* 判断文件MimeType的method */
	private String getMIMEType(File f)
	{
		String type="";
		String fName=f.getName();
	    /* 取得扩展名 */
		String end=fName.substring(fName.lastIndexOf(".")
				+1,fName.length()).toLowerCase();

	    /* 依扩展名的类型决定MimeType */
		if(end.equals("m4a")||end.equals("mp3")||end.equals("mid")||
				end.equals("xmf")||end.equals("ogg")||end.equals("wav"))
		{
			type = "audio";
		}
		else if(end.equals("3gp")||end.equals("mp4"))
		{
			type = "video";
		}
		else if(end.equals("jpg")||end.equals("gif")||end.equals("png")||
				end.equals("jpeg")||end.equals("bmp"))
		{
			type = "image";
		}
		else if(end.equals("apk"))
		{
	      /* android.permission.INSTALL_PACKAGES */
			type = "application/vnd.android.package-archive";
		}
		else
		{
			type="*";
		}
	    /*如果无法直接打开，就跳出软件列表给用户选择 */
		if(end.equals("apk"))
		{
		}
		else
		{
			type += "/*";
		}
		return type;
	}

	/*自定义删除文件方法*/
	private void delFile(String strFileName)
	{
		File myFile = new File(strFileName);
		if(myFile.exists())
		{
			myFile.delete();
		}
	}

	private String getVersionName() throws Exception
	{
		PackageManager packageManager = getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),0);
		String version = packInfo.versionName;
		return version;
	}

	private int getVersionCode()
	{
		int version = 0;
		PackageManager packageManager = getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(),0);
			version = packInfo.versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return version;
	}

	private boolean isConnectNet() {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		if (mNetworkInfo != null) {
			return mNetworkInfo.isAvailable();
		}
		return false;
	}

	public static String getContent(String url) throws Exception{
		//StringBuilder sb = new StringBuilder();

	/*	HttpClient client = new DefaultHttpClient();
		HttpParams httpParams = client.getParams();
		//设置网络超时参数
		HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
		HttpConnectionParams.setSoTimeout(httpParams, 5000);
		HttpResponse response = client.execute(new HttpGet(url));
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "GBK"), 8192);

			String line = null;
			while ((line = reader.readLine())!= null){
				sb.append(line + "\n");
			}
			reader.close();
		}*/
		URL url2 = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) url2.openConnection();
		//取得inputStream，并进行读取
		InputStream input=conn.getInputStream();
		BufferedReader in=new BufferedReader(new InputStreamReader(input, "GBK"));
		String line=null;
		StringBuffer sb=new StringBuffer();
		while((line=in.readLine())!=null){
			sb.append(line);
		}
		in.close();
		return sb.toString();
	}

	private boolean getServerVerCode() {
		try {
			String verjson = getContent(SampleConnection.UPDATE_SERVER
					+ SampleConnection.UPDATE_VERJSON);
			Log.i("hu","verjson="+verjson);
			JSONArray array = new JSONArray(verjson);
			if (array.length() > 0) {
				for(int i=0;i<array.length();i++){
					JSONObject obj = array.getJSONObject(i);
					try {
						if(!obj.getString("appname").equals("FanHong"))
							continue;
						newVerCode = Integer.parseInt(obj.getString("verCode"));
						newVerName = obj.getString("verName");
						strUpdateMsg = obj.getString("updateMsg");
					} catch (Exception e) {
						newVerCode = -1;
						newVerName = "";
						strUpdateMsg = "";
						return false;
					}
				}
			}
		} catch (Exception e) {
			//Log.e(TAG, e.getMessage());
			//Toast.makeText(LoginActivity.this, String.format("%s", e.getMessage()), Toast.LENGTH_SHORT).show();
			return false;
		}
		if(newVerCode>sNowverCode)
			return true;
		else
			return false;
	}
}