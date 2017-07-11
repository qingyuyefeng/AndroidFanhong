package com.fanhong.cn;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class WebViewActivity extends Activity{
	private WebView webView;
	private ProgressDialog progressBar;
	AlertDialog alertDialog;
	private String url;
	public static String NAVIGATION_URL = "http://m.wapreach.com/?cpid=7461";  //导航地址
	private Context context;
	private TextView tv_title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			//透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			//透明导航栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}

		context = getApplicationContext();
		Bundle bundle = this.getIntent().getExtras();
		if(bundle != null){
			url = bundle.getString("url");
		}
		tv_title = (TextView)findViewById(R.id.tv_title);
		webView = (WebView)findViewById(R.id.webView);
		findViewById(R.id.back_img_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				WebViewActivity.this.finish();
			}
		});

		// 开启 localStorage
		webView.getSettings().setDomStorageEnabled(true);
		// 设置支持javascript
		webView.getSettings().setJavaScriptEnabled(true);
		// 启动缓存
		webView.getSettings().setAppCacheEnabled(true);
		// 设置缓存模式
		webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);// 设置js可以直接打开窗口，如window.open()，默认为false
		webView.getSettings().setSupportZoom(true);// 是否可以缩放，默认true
		webView.getSettings().setBuiltInZoomControls(true);// 是否显示缩放按钮，默认false
		webView.getSettings().setUseWideViewPort(true);// 设置此属性，可任意比例缩放。大视图模式
		webView.getSettings().setLoadWithOverviewMode(true);// 和setUseWideViewPort(true)一起解决网页自适应问题
		//webSettings.setAppCacheEnabled(true);// 是否使用缓存
		//	webSettings.setDomStorageEnabled(true);// DOM Storage

		//  webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new webClient());
		goHelpWeb(url);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(progressBar.isShowing())
			return true;
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			Log.i("xq","===>"+webView.canGoBack());
			webView.goBack(); //goBack()表示返回WebView的上一页面
			return true;
		}else if(keyCode == KeyEvent.KEYCODE_BACK){
			this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	private void goHelpWeb(String url){
		//设计进度条
		progressBar = ProgressDialog.show(WebViewActivity.this, null, "正在进入网页，请稍后…");
		alertDialog = new AlertDialog.Builder(WebViewActivity.this).create();

		//String strURI = ApplicationUtil.URL + "tools/news.aspx?id=3";
		String strURI = url;
		//检测网站的合法性
		if(URLUtil.isNetworkUrl(strURI)){
			webView.loadUrl(strURI);
		}
	}
	private static final String INJECTION_TOKEN = "**injection**";
	private class webClient extends WebViewClient{

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			//view.loadUrl(url);
			//return true;
			Log.e("hu","打开网页 url="+url);
			if(url.startsWith("http:") || url.startsWith("https:") ) {
				view.loadUrl(url);
				return false;
			}else{
				try{
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					startActivity(intent);
				}catch(Exception e){}
				return false;
			}
		}

		@Override
		public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
			WebResourceResponse response = super.shouldInterceptRequest(view, url);
			if(url != null && url.contains(INJECTION_TOKEN)) {
				String assetPath = url.substring(url.indexOf(INJECTION_TOKEN) + INJECTION_TOKEN.length(), url.length());
				try {
					response = new WebResourceResponse(
							"application/javascript",
							"UTF8",
							context.getAssets().open(assetPath)
					);
				} catch (IOException e) {
					e.printStackTrace(); // Failed to load asset file
				}
			}
			return response;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			tv_title.setText(url);
			//if(progressBar.isShowing()){
			progressBar.dismiss();
			//}
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
									String description, String failingUrl) {
			Toast.makeText(WebViewActivity.this, "网页加载出错！", Toast.LENGTH_LONG);
			alertDialog.setTitle("ERROR");
			alertDialog.setMessage(description);
			alertDialog.setButton("OK", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					alertDialog.cancel();
				}
			});
			alertDialog.show();
		}
	}
}
