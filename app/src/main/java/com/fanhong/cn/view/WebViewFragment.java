package com.fanhong.cn.view;

import com.fanhong.cn.R;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class WebViewFragment extends Fragment {
	public static final int PAGER_INDEX = 0;
	WebView webView;
	ProgressDialog progressBar;
	AlertDialog alertDialog;
	String url;
	public static String NAVIGATION_URL = "http://m.wapreach.com/?cpid=7461";  //导航地址
	//public static String NAVIGATION_URL = "http://www.qq.com";  //导航地址

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		return inflater.inflate(R.layout.webviewfragment, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		webView = (WebView)view.findViewById(R.id.webView);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new webClient());
		goHelpWeb();
	}


	//设置回退
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			Log.e("hu","******web goback");
			webView.goBack(); //goBack()表示返回WebView的上一页面
			return true;
		}else if(keyCode == KeyEvent.KEYCODE_BACK){
			Log.e("hu","******web finish");
			return false;
		}

		return false;
	}

	private void goHelpWeb(){
		//设计进度条
		progressBar = ProgressDialog.show(WebViewFragment.this.getActivity(), null, "正在进入网页，请稍后…");
		alertDialog = new AlertDialog.Builder(WebViewFragment.this.getActivity()).create();

		//String strURI = ApplicationUtil.URL + "tools/news.aspx?id=3";
		String strURI = NAVIGATION_URL;
		//检测网站的合法性
		if(URLUtil.isNetworkUrl(strURI)){
			webView.loadUrl(strURI);
		}
	}

	private class webClient extends WebViewClient{

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			//if(progressBar.isShowing()){
			progressBar.dismiss();
			//}
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
									String description, String failingUrl) {
			Toast.makeText(WebViewFragment.this.getActivity(), "网页加载出错！", Toast.LENGTH_LONG);
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
