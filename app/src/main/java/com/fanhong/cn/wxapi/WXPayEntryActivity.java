package com.fanhong.cn.wxapi;

import com.fanhong.cn.R;
import com.fanhong.cn.SampleConnection;
import com.fanhong.cn.pay.ParameterConfig;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{
	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

	private IWXAPI api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_result);

		api = WXAPIFactory.createWXAPI(this, ParameterConfig.WX_APP_ID);

		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.e(TAG, "onPayFinish, errCode = " + resp.errCode);
		Toast.makeText(WXPayEntryActivity.this, resp.errCode, Toast.LENGTH_SHORT).show();
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("提示");
			builder.setMessage("微信支付结果" +String.valueOf(resp.errCode));
			builder.show();
			//Intent intent;
			int code = resp.errCode;

			Log.e("hu","*******微信支付返回结果！  code="+code);

			Intent mIntent = new Intent(SampleConnection.MYPAY_RECEIVER);
			Bundle mBundle = new Bundle();
			mBundle.putInt("errcode", code);
			mIntent.putExtras(mBundle);
			this.sendBroadcast(mIntent);
			this.finish();
            /*
            switch (code) {
            case 0:
               // Toast.makeText(this, "支付成功",0).show();
                //intent=new Intent(this,ConfirmOrderActivity.class);
               // intent.putExtra("result", 0);
               // startActivity(intent);
            	//showDialog("微信支付成功");
            	uploadTradeNO();
            	deletCartItem();
                finish();
                break;
            case -1:
               // Toast.makeText(this, "支付失败",0).show();
                //intent=new Intent(this,ConfirmOrderActivity.class);
               // intent.putExtra("result", -1);
               // startActivity(intent);
            	showDialog("微信支付失败");
               // finish();
                break;
            case -2:
               // Toast.makeText(this, "支付取消",0).show();
                //intent=new Intent(this,ConfirmOrderActivity.class);
               // intent.putExtra("result", -2);
               // startActivity(intent);
            	showDialog("微信支付取消");
                //finish();
                break;
            default:
                break;
            }*/
		}
	}
}