package com.fanhong.cn;

import android.content.Intent;
import android.os.Bundle;

public class LogoActivity extends SampleActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(2000L);
					startActivity(new Intent(LogoActivity.this, LoginActivity.class));
					finish();
				} catch (Exception e) {}
			}
		}).start();
    }
}
