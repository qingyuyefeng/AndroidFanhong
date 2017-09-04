package com.fanhong.cn;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.just.library.AgentWeb;
import com.just.library.ChromeClientCallbackManager;
import com.zhy.autolayout.AutoLinearLayout;

/**
 * Created by Administrator on 2017/7/20.
 */

public class AgentWebActivity extends Activity{
    private AgentWeb mAgentWeb;
    private RelativeLayout mRelativeLayout;
    private AutoLinearLayout titleLayout;
    private TextView webTitle;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agentweb);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.rl_client);
        webTitle = (TextView) findViewById(R.id.web_title);
        titleLayout = (AutoLinearLayout) findViewById(R.id.title_llayout);

        webTitle.setText(getIntent().getStringExtra("title"));
        String str = getIntent().getStringExtra("url");
        //检测网站的合法性
        if(URLUtil.isNetworkUrl(str)){
            goWeb(str);
        }

        //支持flash
//                String temp = "<html><body bgcolor=\"" + "black"
//                        + "\"> <br/><embed src=\"" + str + "\" width=\"" + "100%"
//                        + "\" height=\"" + "90%" + "\" scale=\"" + "noscale"
//                        + "\" type=\"" + "application/x-shockwave-flash"
//                        + "\"> </embed></body></html>";
//                String mimeType = "text/html";
//                String encoding = "utf-8";
//                mWebView.loadDataWithBaseURL("null", temp, mimeType, encoding, "");
//
//        View view1 = mRelativeLayout.getChildAt(0);
//        if(view1 instanceof FrameLayout){
//            View view2 = ((FrameLayout) view1).getChildAt(0);
//            if(view2 instanceof WebView){
//                mWebView = (WebView) view2;
//            }
//        }
        findViewById(R.id.back_img_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AgentWebActivity.this.finish();
            }
        });
    }

    private void goWeb(String url){
        mAgentWeb = AgentWeb.with(this)
                            .setAgentWebParent(mRelativeLayout,new RelativeLayout.LayoutParams(-1,-1))
                            .useDefaultIndicator()      //使用默认进度条
                            .defaultProgressBarColor()  //使用默认进度条的颜色
//                            .setReceivedTitleCallback(mCallback)    //设置web页面title的回调
                            .createAgentWeb()
                            .ready()
                            .go(url);
        mAgentWeb.getJsEntraceAccess().quickCallJs("callByAndroid");
    }
//    private ChromeClientCallbackManager.ReceivedTitleCallback mCallback = new ChromeClientCallbackManager.ReceivedTitleCallback() {
//        @Override
//        public void onReceivedTitle(WebView view, String title) {
////            Toast.makeText(AgentWebActivity.this,title,Toast.LENGTH_SHORT).show();
////            webTitle.setText(title);
//        }
//    };


    //封装的返回键监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
