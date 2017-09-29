package com.fanhong.cn.verification;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.fanhong.cn.App;
import com.fanhong.cn.ConfirmOrderActivity;
import com.fanhong.cn.R;
import com.fanhong.cn.SampleConnection;
import com.fanhong.cn.fenxiao.PostSuccessActivity;
import com.fanhong.cn.pay.ParameterConfig;
import com.fanhong.cn.pay.PayResult;
import com.fanhong.cn.pay.SignUtils;
import com.fanhong.cn.util.JsonSyncUtils;
import com.fanhong.cn.util.StringUtils;
import com.fanhong.cn.util.TopBarUtil;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/15.
 */
@ContentView(R.layout.activity_verification_car_form_confirm)
public class CarFormConfirmActivity extends Activity {
    @ViewInject(R.id.tv_title)
    private TextView title;
    @ViewInject(R.id.tv_form_name)
    private TextView tv_name;
    @ViewInject(R.id.tv_form_phone)
    private TextView tv_phone;
    @ViewInject(R.id.tv_form_address)
    private TextView tv_addr;
    @ViewInject(R.id.checkbox_zfb)
    private CheckBox checkbox_zfb;
    @ViewInject(R.id.checkbox_wx)
    private CheckBox checkbox_wx;
    //    @ViewInject(R.id.box_read)
//    private CheckBox cbox_read;
    @ViewInject(R.id.btn_commit)
    private Button btn_commit;

    private int payType = 0;
    private String WXorderNumber;
    private MyBroadcastReceiver myReceiver;//微信支付广播回调
    private IWXAPI api;

    private class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SampleConnection.MYPAY_RECEIVER)) {
                int reCode = intent.getIntExtra("status", 1); //0成功，-1签名错误等异常，-2用户取消支付操作
                String msg = intent.getStringExtra("msg");
                switch (reCode) {
                    case 0:
                        submitOrder();
                        break;
                    case -1:
                        payFailure();
                        break;
                    case -2:
                        payOrderCancel();
                        break;
                }
                CarFormConfirmActivity.this.unregisterReceiver(myReceiver);
                btn_commit.setEnabled(true);
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        init();
        registerReceiver();
    }

    //动态注册广播
    public void registerReceiver() {
        myReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SampleConnection.MYPAY_RECEIVER); //设置动作
        this.registerReceiver(myReceiver, intentFilter);
    }

    private void init() {
        title.setText("确认信息");
        Intent intent = getIntent();
        tv_name.setText(intent.getStringExtra("name"));
        tv_phone.setText(intent.getStringExtra("phone"));
        tv_addr.setText(intent.getStringExtra("address"));
    }

    /* @Event(value = R.id.box_read, type = CompoundButton.OnCheckedChangeListener.class)
     private void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
         btn_commit.setEnabled(isChecked);
     }*/
    @Event(value = {R.id.checkbox_zfb, R.id.checkbox_wx}, type = CompoundButton.OnCheckedChangeListener.class)
    private void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // TODO Auto-generated method stub
        switch (buttonView.getId()) {
            case R.id.checkbox_zfb:
                if (isChecked) {
                    checkbox_wx.setChecked(false);
                    payType = 1;
                } else
                    payType = 0;
                break;
            case R.id.checkbox_wx:
                if (isChecked) {
                    checkbox_zfb.setChecked(false);
                    payType = 2;
                } else
                    payType = 0;
                break;
        }
    }

    @Event({R.id.img_back, R.id.btn_commit})
    private void onClicks(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_commit://确认并去付款
                if (payType == 0) {
                    Toast.makeText(this, "请选择支付方式", Toast.LENGTH_SHORT).show();
                } else if (payType == 1) {
                    v.setEnabled(false);
                    RequestParams params = new RequestParams(App.CMDURL);
                    params.addParameter("cmd", "77");
                    x.http().post(params, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            if (JsonSyncUtils.getJsonValue(result, "cw").equals("0")) {
                                ParameterConfig.alipay_RSA_PRIVATE = JsonSyncUtils.getJsonValue(result, "data");
                                if (!StringUtils.isEmpty(ParameterConfig.alipay_RSA_PRIVATE)) {
                                    alipay();
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            Toast.makeText(CarFormConfirmActivity.this, "启动支付失败，请重试", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onCancelled(CancelledException cex) {
                            Toast.makeText(CarFormConfirmActivity.this, "启动支付失败，请重试", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFinished() {

                        }
                    });
                }else if(payType==2){
                    v.setEnabled(false);
                    api = WXAPIFactory.createWXAPI(this, ParameterConfig.WX_APPID);
                    WeixinPay(1);
                }
                break;
        }
    }

    private void submitOrder() {
        RequestParams params = new RequestParams(App.CMDURL);
        params.addBodyParameter("cmd", "75");
        params.addBodyParameter("name", tv_name.getText().toString());
        params.addBodyParameter("phone", tv_phone.getText().toString());
        params.addBodyParameter("mapdz", tv_addr.getText().toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (JsonSyncUtils.getJsonValue(result, "cw").equals("0")) {
                    Intent intent = new Intent(CarFormConfirmActivity.this, PostSuccessActivity.class);
                    intent.putExtra("fromVerification", true);
                    startActivity(intent);
                } else {
                    Toast.makeText(CarFormConfirmActivity.this, "数据提交错误", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                btn_commit.setEnabled(true);
            }
        });
    }

    private Handler alipayHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 9000://支付成功
                    //"alipay_trade_app_pay_response":{"timestamp":"","total_amount":"","out_trade_no":"","trade_no":""}
                    //结果：下单时间，总金额，商户订单号，支付订单号
//                    Toast.makeText(CarFormConfirmActivity.this, R.string.zhifu_success, Toast.LENGTH_SHORT).show();
                    submitOrder();
                    break;
                case 6001://用户中途取消
                    payOrderCancel();
//                    Toast.makeText(CarFormConfirmActivity.this, R.string.zhifu_failed, Toast.LENGTH_SHORT).show();
                    break;
                case 8000://正在处理中，支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
                case 4000://订单支付失败
                case 5000://重复请求
                case 6002://网络连接出错
                case 6004://支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
                default://其他支付错误
                    payFailure();
                    break;
            }
            btn_commit.setEnabled(true);
        }
    };

    private void payFailure() {
        AlertDialog alert = new AlertDialog.Builder(this)
                .setMessage("订单支付失败！")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                .create();
        alert.show();
    }

    private void payOrderCancel() {
        new AlertDialog.Builder(this)
                .setMessage("订单支付取消！")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                .create().show();
    }
    public void WeixinPay(int type) {
        WXorderNumber = TopBarUtil.getRandomString();
        RequestParams params = new RequestParams(ParameterConfig.WX_notifyUrl);
        params.addBodyParameter("body", ParameterConfig.descript[type]);
//        params.addBodyParameter("detail",order.toString());
        params.addBodyParameter("total_fee", "300");
//        params.addBodyParameter("total_fee", "0.01");
        params.addBodyParameter("ddh",WXorderNumber);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject json = new JSONObject(result);
                    Log.i("xqWXPay", json.toString());
                    if (null != json && !json.has("retcode")) {
                        PayReq req = new PayReq();
                        req.appId = ParameterConfig.WX_APPID;
                        req.partnerId = ParameterConfig.WX_MCH_ID;
                        req.prepayId = json.getString("prepay_id");
                        req.nonceStr = json.getString("nonce_str");
                        req.timeStamp = json.getString("timestamp");
                        req.packageValue = "Sign=WXPay";
//                        String content = "appid=" + req.appId + "&noncestr=" + req.nonceStr + "&package=" +
//                                req.packageValue + "&partnerid=" + req.partnerId + "&prepayid=" + req.prepayId +
//                                "&timestamp" + req.timeStamp + "&key=" + ParameterConfig.WX_API_KEY;
                        req.sign = json.getString("xsign");
//                        req.sign = MD5Util.MD5Encode(content, "").toUpperCase();
                        Log.i("xqWXPay", " sign==>" + req.sign);


                        Toast.makeText(CarFormConfirmActivity.this, "正在调起支付...", Toast.LENGTH_SHORT).show();
                        api.registerApp(ParameterConfig.WX_APPID);//注册到微信
                        api.sendReq(req);//调起支付
                    } else {
                        Toast.makeText(CarFormConfirmActivity.this, "返回错误" + json.getString("return_msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(CarFormConfirmActivity.this, "服务器请求错误", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(CarFormConfirmActivity.this, "发起支付失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinished() {

            }
        });
    }
    public void alipay() {
        final String orderInfo = getOrderInfo();
        if (orderInfo != null) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    PayTask payTask = new PayTask(CarFormConfirmActivity.this);
                    Map<String, String> result = payTask.payV2(orderInfo, true);//支付
                    PayResult payResult = new PayResult(result);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();

                    Message msg = new Message();
                    msg.obj = resultInfo;
                    Log.e("alipaytest", "resultInfo:" + resultInfo + "\nresultstatus:" + resultStatus);
                    msg.what = Integer.parseInt(resultStatus);
                    alipayHandler.sendMessage(msg);
                }
            };
            new Thread(runnable).start();
        }
    }

    public String getOrderInfo() {
        String orderInfo = null;
//        try {
//            orderInfo = "app_id=" + URLEncoder.encode(ParameterConfig.alipay_APPID, "UTF-8");
//            orderInfo += "&method=" + URLEncoder.encode("alipay.trade.app.pay", "UTF-8");
//            orderInfo += "&charset=" + URLEncoder.encode("utf-8", "UTF-8");
//            orderInfo += "&sign_type=" + URLEncoder.encode("RSA2", "UTF-8");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeStr = sdf.format(new Date());
//            orderInfo += "&timestamp=" + URLEncoder.encode(timeStr, "UTF-8");
//            orderInfo += "&version=" + URLEncoder.encode("1.0", "UTF-8");
//            orderInfo += "&notify_url=" + URLEncoder.encode(ParameterConfig.alipay_SERVICE_CALLBACK, "UTF-8");
//            orderInfo += "&biz_content=" + URLEncoder.encode(getGoodsContent(), "UTF-8");
//            orderInfo += "&sign=" + URLEncoder.encode(ParameterConfig.alipay_RSA_PRIVATE, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        Map<String, String> keyValues = new HashMap<String, String>();
        keyValues.put("app_id", ParameterConfig.alipay_APPID);
        keyValues.put("biz_content", getGoodsContent());
        keyValues.put("charset", "utf-8");
        keyValues.put("method", "alipay.trade.app.pay");
        keyValues.put("notify_url", ParameterConfig.alipay_SERVICE_CALLBACK);
        keyValues.put("sign_type", "RSA2");
        keyValues.put("timestamp", timeStr);
        keyValues.put("version", "1.0");

        Log.e("alipaytest", "info: " + orderInfo);

        orderInfo = buildOrderParam(keyValues);
        String sign = getSign(keyValues, ParameterConfig.alipay_RSA_PRIVATE, true);
        orderInfo += "&" + sign;
        return orderInfo;
    }

    /**
     * 这里填写订单信息
     *
     * @return
     */
    public String getGoodsContent() {
        String content = "{";
        content += "\"subject\":\"" + "汽车年审" + "\"";//商品的标题/交易标题/订单标题/订单关键字等。
        content += ",\"body\":\"" + "汽车年审支付" + "\"";//商品的描述
        content += ",\"out_trade_no\":\"" + TopBarUtil.getRandomString() + "\"";//商户网站唯一订单号
        content += ",\"timeout_express\":\"30m\"";//订单超时时间
        content += ",\"total_amount\":\"" + 300.00 + "\"";//订单总金额，单位为元，精确到小数点后两位，取值范围[0.01,100000000]
//        content += ",\"total_amount\":\"" + 0.01 + "\"";//测试0.01
        content += ",\"product_code\":\"" + "QUICK_MSECURITY_PAY" + "\"";//销售产品码，商家和支付宝签约的产品码，为固定值QUICK_MSECURITY_PAY
        return content + "}";
//        "{" +"\"timeout_express\":\"30m\",\"product_code\":\"QUICK_MSECURITY_PAY\",\"total_amount\":\"0.01\",\"subject\":\"1\",\"body\":\"我是测试数据\",\"out_trade_no\":\"" + getOutTradeNo() + "\"}");
    }

    public String buildOrderParam(Map<String, String> map) {
        List<String> keys = new ArrayList<String>(map.keySet());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.size() - 1; i++) {
            String key = keys.get(i);
            String value = map.get(key);
            sb.append(buildKeyValue(key, value, true));
            sb.append("&");
        }

        String tailKey = keys.get(keys.size() - 1);
        String tailValue = map.get(tailKey);
        sb.append(buildKeyValue(tailKey, tailValue, true));

        return sb.toString();
    }

    private String buildKeyValue(String key, String value, boolean isEncode) {
        StringBuilder sb = new StringBuilder();
        sb.append(key);
        sb.append("=");
        if (isEncode) {
            try {
                sb.append(URLEncoder.encode(value, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                sb.append(value);
            }
        } else {
            sb.append(value);
        }
        return sb.toString();
    }

    public String getSign(Map<String, String> map, String rsaKey, boolean rsa2) {
        List<String> keys = new ArrayList<String>(map.keySet());
        // key排序
        Collections.sort(keys);

        StringBuilder authInfo = new StringBuilder();
        for (int i = 0; i < keys.size() - 1; i++) {
            String key = keys.get(i);
            String value = map.get(key);
            authInfo.append(buildKeyValue(key, value, false));
            authInfo.append("&");
        }

        String tailKey = keys.get(keys.size() - 1);
        String tailValue = map.get(tailKey);
        authInfo.append(buildKeyValue(tailKey, tailValue, false));

        String oriSign = SignUtils.sign(authInfo.toString(), rsaKey, rsa2);
        String encodedSign = "";

        try {
            encodedSign = URLEncoder.encode(oriSign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "sign=" + encodedSign;
    }
}
