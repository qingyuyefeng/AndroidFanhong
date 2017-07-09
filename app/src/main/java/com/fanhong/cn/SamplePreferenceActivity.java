package com.fanhong.cn;

import org.json.JSONObject;

import android.preference.PreferenceActivity;

public class SamplePreferenceActivity extends PreferenceActivity {
	public synchronized void connectFail(int type) {}
	public synchronized void connectSuccess(JSONObject json, int type) {}
}
