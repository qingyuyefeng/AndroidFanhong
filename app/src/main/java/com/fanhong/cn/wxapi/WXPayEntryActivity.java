package com.fanhong.cn.wxapi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fanhong.cn.R;
import com.fanhong.cn.SampleConnection;
import com.fanhong.cn.pay.ParameterConfig;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * Created by Administrator on 2017/9/26.
 */

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{

    private IWXAPI api;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);

        api = WXAPIFactory.createWXAPI(this, ParameterConfig.WX_APPID);
        api.handleIntent(getIntent(),this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent,this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
//        Log.i("xqWXPay", "onPayFinish, errCode = " + baseResp.errCode);

        if(baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX){
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("提示");
//            builder.setMessage(getString(R.string.wx_pay_result)+"："+baseResp.errCode);
//            builder.show();

            Intent intent = new Intent();
            intent.setAction(SampleConnection.MYPAY_RECEIVER);
            intent.putExtra("status",baseResp.errCode);
            intent.putExtra("msg",baseResp.errStr);
            sendBroadcast(intent);
        }
        finish();
        //在需要处理支付结果的地方注册广播来接收消息
    }
}
