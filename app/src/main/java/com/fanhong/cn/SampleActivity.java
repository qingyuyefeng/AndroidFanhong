package com.fanhong.cn;

import org.json.JSONObject;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;

public class SampleActivity extends FragmentActivity {
	public synchronized void connectFail(int type) {}
	public synchronized void connectSuccess(JSONObject json, int type) {}
}
