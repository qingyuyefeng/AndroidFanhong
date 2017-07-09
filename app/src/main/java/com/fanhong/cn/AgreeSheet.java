package com.fanhong.cn;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;



public class AgreeSheet extends Activity implements OnClickListener{
	Button btn_refuse,btn_accept;
	LinearLayout ll_bottom;
	private ImageView titleBackImageBtn;
	
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);  
	        setContentView(R.layout.activity_agreesheet);        
	        
	        // ????activity???????????????
	      	getWindow().setSoftInputMode(
	      				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	      	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				//???????  
		        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);  
		        //?????????  
		        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);  
			}
	      	Bundle bundle = this.getIntent().getExtras();	        
	        int num=0;
	        if(bundle != null)
	        	num= bundle.getInt("buttonvisible");	        
	      	ll_bottom = (LinearLayout)findViewById(R.id.ll_bottom); 
	      	if(num == 0)
	        	ll_bottom.setVisibility(View.GONE);
	      	
	      	btn_refuse = (Button)findViewById(R.id.btn_refuse);        
	      	btn_refuse.setOnClickListener(this);
	      	btn_refuse.setSelected(true);//???
	      	
	      	btn_accept = (Button)findViewById(R.id.btn_accept);        
	      	btn_accept.setOnClickListener(this);
	      	btn_accept.setSelected(true);//???
	      	
	      	titleBackImageBtn = (ImageView)findViewById(R.id.titleBackImageBtn);
	      	titleBackImageBtn.setOnClickListener(this);
	 }
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if(view.getId()==R.id.btn_refuse){
			Intent intent = new Intent();
			this.setResult(2, intent);
        	this.finish();
		}else if(view.getId()==R.id.btn_accept){
			Intent intent = new Intent();
        	this.setResult(RESULT_OK, intent);
        	this.finish();
		}
		else if(view.getId()==R.id.titleBackImageBtn){
    		this.finish();
		}
	}
	
	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event)  { 
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { 
        	Intent intent = new Intent();
			this.setResult(2, intent);
        	this.finish();
        }
        return super.onKeyDown(keyCode, event); 
    } 
	
}
	 