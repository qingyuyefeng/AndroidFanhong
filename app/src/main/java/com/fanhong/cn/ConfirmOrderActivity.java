package com.fanhong.cn;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.fanhong.cn.database.Cartdb;
import com.fanhong.cn.listviews.ConfirmOrderListView;
import com.fanhong.cn.pay.OrderInfo;
import com.fanhong.cn.pay.ParameterConfig;
import com.fanhong.cn.pay.PayResult;
import com.fanhong.cn.pay.SignUtils;
import com.fanhong.cn.shippingaddress.AllAddressActivity;
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

@ContentView(R.layout.activity_confirmorder)
public class ConfirmOrderActivity extends Activity {
    @ViewInject(R.id.show_address)
    private TextView tvShow;
    //    @ViewInject(R.id.tv_person)
//    private TextView tv_person;
//    @ViewInject(R.id.tv_phone)
//    private TextView tv_phone;
//    @ViewInject(R.id.tv_address)
//    private TextView tv_address;
    @ViewInject(R.id.tv_totalmoney)
    private TextView tv_totalmoney;
    @ViewInject(R.id.checkbox_zfb)
    private CheckBox checkbox_zfb;
    @ViewInject(R.id.checkbox_wx)
    private CheckBox checkbox_wx;
    @ViewInject(R.id.lv_list)
    private ConfirmOrderListView lv_list;
    @ViewInject(R.id.ll_addaddress)
    private LinearLayout ll_addaddress;
    @ViewInject(R.id.btn_ok)
    private Button btn_ok;

    private SharedPreferences mSettingPref;
    private Cartdb _dbad;
    private float fl_total = 0.0f;  //合计价格
    private String id = null;
    private String name, descript;
    private String user, phone, addr, addrid;
    private int isCart = 0;
    private int payway = 0;   //支付方式    1支付宝，2微信
    private String goods_str = null;

    private IWXAPI api;
    private MyBroadcastReceiver myReceiver;//微信支付广播回调
    private String WXorderNumber;

    private class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SampleConnection.MYPAY_RECEIVER)) {
                int reCode = intent.getIntExtra("status", 1); //0成功，-1签名错误等异常，-2用户取消支付操作
                String msg = intent.getStringExtra("msg");
                switch (reCode) {
                    case 0:
                        new AlertDialog.Builder(ConfirmOrderActivity.this)
                                .setMessage(R.string.pay_success)
                                .setPositiveButton("确定",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                .create().show();
                        if (isCart == 1) {
                            deletCartItem();
                        }
                        postWXOrder();
                        break;
                    case -1:
                        payFailure();
                        break;
                    case -2:
                        payOrderCancel();
                        break;
                }
                ConfirmOrderActivity.this.unregisterReceiver(myReceiver);
                btn_ok.setEnabled(true);
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        TopBarUtil.initStatusBar(this);
        mSettingPref = getSharedPreferences("Setting", Context.MODE_PRIVATE);
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //重写选项被单击事件的处理方法
                lv_list.listItemAdapter.setSelectItem(position);
                lv_list.listItemAdapter.notifyDataSetInvalidated();
            }
        });
        _dbad = new Cartdb(this);
        initData();

        registerReceiver();
    }

    //动态注册广播
    public void registerReceiver() {
        myReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SampleConnection.MYPAY_RECEIVER); //设置动作
        this.registerReceiver(myReceiver, intentFilter);
    }

    private void initData() {
        lv_list.Bulid();
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null)
            isCart = bundle.getInt("iscart");
        StringBuffer buf = new StringBuffer();
        if (isCart == 0) {
            id = bundle.getString("id");
            name = bundle.getString("name");
            descript = bundle.getString("describe");
            String logourl = bundle.getString("logourl");
            String price = bundle.getString("price");
            int amount = bundle.getInt("amount");
            float fl = Float.parseFloat(price);
            fl_total = fl * amount;
            tv_totalmoney.setText(String.valueOf(fl_total));
            buf.append(id);
            buf.append(":");
            buf.append(amount);
            lv_list.addItem(id, name, price, amount, logourl);
        } else {
            _dbad.open();
            Cursor cursor = _dbad.selectConversationList();
            if (cursor.moveToNext()) {
                if (cursor.getInt(6) == 1) {
                    lv_list.addItem(cursor.getString(0), cursor.getString(1), cursor.getString(3), cursor.getInt(4), cursor.getString(5));
                    buf.append(cursor.getString(0));
                    buf.append(":");
                    buf.append(cursor.getInt(4));

                    id = cursor.getString(0);
                    name = cursor.getString(1);
                    descript = cursor.getString(2);
                }
            }
            while (cursor.moveToNext()) {
                if (cursor.getInt(6) == 1) {
                    lv_list.addItem(cursor.getString(0), cursor.getString(1), cursor.getString(3), cursor.getInt(4), cursor.getString(5));
                    buf.append(",");
                    buf.append(cursor.getString(0));
                    buf.append(":");
                    buf.append(cursor.getInt(4));
                }
            }
            fl_total = _dbad.getTotalPrice();
            tv_totalmoney.setText(String.valueOf(fl_total));
            _dbad.close();
        }
        goods_str = buf.toString();
        lv_list.listItemAdapter.notifyDataSetInvalidated();
        TopBarUtil.setListViewHeight(lv_list);
    }

    public void alipay() {
        final String orderInfo = getOrderInfo();
        if (orderInfo != null) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    PayTask payTask = new PayTask(ConfirmOrderActivity.this);
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

    private Handler alipayHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //支付宝
                case 9000://支付成功
                    uploadTradeNO(JsonSyncUtils.getJsonValue(msg.obj.toString(), "alipay_trade_app_pay_response"), 1);
                    if (isCart == 1)
                        deletCartItem();
                    break;
                case 6001://用户中途取消
                    payOrderCancel();
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
            btn_ok.setEnabled(true);
        }
    };


    private void deletCartItem() {
        //支付成功后删除购物车里购买的商品
        _dbad.open();
        _dbad.deleteSelectedItem();
        _dbad.close();
    }

    private void payFailure() {
        AlertDialog alert = new AlertDialog.Builder(ConfirmOrderActivity.this)
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
        new AlertDialog.Builder(ConfirmOrderActivity.this)
                .setMessage("订单支付取消！")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                .create().show();
    }

    @Event({R.id.btn_back, R.id.ll_addaddress, R.id.btn_ok})
    private void onClicks(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.ll_addaddress:
                Intent intent2 = new Intent();
                intent2.putExtra("status", 1);
                intent2.setClass(ConfirmOrderActivity.this, AllAddressActivity.class);
                startActivityForResult(intent2, 1);
                break;
            case R.id.btn_ok:
                if (StringUtils.isEmpty(tvShow.getText().toString()) || tvShow.getText().toString().equals("选择地址")) {
                    Toast.makeText(ConfirmOrderActivity.this, "请选择收货地址", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (payway == 1) {
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
                            Toast.makeText(ConfirmOrderActivity.this, "启动支付失败，请重试", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onCancelled(CancelledException cex) {
                            Toast.makeText(ConfirmOrderActivity.this, "启动支付失败，请重试", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFinished() {

                        }
                    });
                } else if (payway == 2) {
                    v.setEnabled(false);
                    api = WXAPIFactory.createWXAPI(this, ParameterConfig.WX_APPID);
                    WeixinPay(new OrderInfo(name, descript), 0);
//                    Toast.makeText(ConfirmOrderActivity.this, "微信支付正在开通中..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ConfirmOrderActivity.this, "请先选择支付方式", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    public void WeixinPay(OrderInfo order, int type) {
        WXorderNumber = TopBarUtil.getRandomString();
        RequestParams params = new RequestParams(ParameterConfig.WX_notifyUrl);
        params.addBodyParameter("body", ParameterConfig.descript[type]);
//        params.addBodyParameter("detail",order.toString());
        params.addBodyParameter("total_fee", fl_total+"");
//        params.addBodyParameter("total_fee", "0.01");
        params.addBodyParameter("ddh",WXorderNumber);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject json = new JSONObject(result);
//                    Log.i("xqWXPay", json.toString());
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
//                        Log.i("xqWXPay", " sign==>" + req.sign);


                        Toast.makeText(ConfirmOrderActivity.this, "正在调起支付...", Toast.LENGTH_SHORT).show();
                        api.registerApp(ParameterConfig.WX_APPID);//注册到微信
                        api.sendReq(req);//调起支付
                    } else {
                        Toast.makeText(ConfirmOrderActivity.this, "返回错误" + json.getString("return_msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(ConfirmOrderActivity.this, "服务器请求错误", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(ConfirmOrderActivity.this, "发起支付失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinished() {

            }
        });
    }


    @Event(value = {R.id.checkbox_zfb, R.id.checkbox_wx}, type = CompoundButton.OnCheckedChangeListener.class)
    private void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // TODO Auto-generated method stub
        switch (buttonView.getId()) {
            case R.id.checkbox_zfb:
                if (isChecked) {
                    checkbox_wx.setChecked(false);
                    payway = 1;
                } else
                    payway = 0;
                break;
            case R.id.checkbox_wx:
                if (isChecked) {
                    checkbox_zfb.setChecked(false);
                    payway = 2;
                } else
                    payway = 0;
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                break;
            case 21:  //登录返回
                break;
            case 121: //选择地址返回
                addr = data.getStringExtra("address");
                user = data.getStringExtra("name");
                phone = data.getStringExtra("phone");
                addrid = data.getStringExtra("addrId");
                tvShow.setText(addr);
                break;
        }
    }

    //支付宝单号
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

        orderInfo = buildOrderParam(keyValues);
        String sign = getSign(keyValues, ParameterConfig.alipay_RSA_PRIVATE, true);
        orderInfo += "&" + sign;
        return orderInfo;
    }

    public String getGoodsContent() {
        String content = "{";
        content += "\"subject\":\"" + name + "\"";//商品的标题/交易标题/订单标题/订单关键字等。
        content += ",\"body\":\"" + descript + "\"";//商品的描述
        content += ",\"out_trade_no\":\"" + TopBarUtil.getRandomString() + "\"";//商户网站唯一订单号
        content += ",\"timeout_express\":\"30m\"";//订单超时时间
        content += ",\"total_amount\":\"" + fl_total + "\"";//订单总金额，单位为元，精确到小数点后两位，取值范围[0.01,100000000]
//        content += ",\"total_amount\":\"" + 0.01 + "\"";
        content += ",\"product_code\":\"" + "QUICK_MSECURITY_PAY" + "\"";//销售产品码，商家和支付宝签约的产品码，为固定值QUICK_MSECURITY_PAY
        return content + "}";
//        "{" +"\"timeout_express\":\"30m\",\"product_code\":\"QUICK_MSECURITY_PAY\",\"total_amount\":\"0.01\",\"subject\":\"1\",\"body\":\"我是测试数据\",\"out_trade_no\":\"" + getOutTradeNo() + "\"}");
    }

//    private static String getOutTradeNo() {
//
//        long t1 = System.currentTimeMillis() / 1000;
//
//        Random r = new Random();
//        String key = String.valueOf(t1);
//
//        key = key + (r.nextInt(899999) + 100000);
//        key = key.substring(0, 15);
//
//        return key;
//    }

    //将map字符串序列化
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

    private void uploadTradeNO(String alipay_result, int type) {
        RequestParams param = new RequestParams(App.CMDURL);
        param.addParameter("cmd", "21");
        String str = mSettingPref.getString("UserId", "");
        param.addParameter("uid", str);  //下订单用户ID
        param.addParameter("time", JsonSyncUtils.getJsonValue(alipay_result, "timestamp"));
        param.addParameter("zjje", JsonSyncUtils.getJsonValue(alipay_result, "total_amount"));  //支付金额
        param.addParameter("zffs", type + "");    //支付方式（1支付宝，2微信）
        param.addParameter("user", user);  //收货人姓名
        param.addParameter("dh", phone); //收货人手机号
        param.addParameter("ldh", addr);  //详细地址
        param.addParameter("ddh", JsonSyncUtils.getJsonValue(alipay_result, "out_trade_no"));   //订单号
        param.addParameter("qid", mSettingPref.getString("gardenId", ""));   //小区ID号
        param.addParameter("goods", goods_str);    //商品
        x.http().post(param, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (JsonSyncUtils.getJsonValue(result, "cw").equals("0"))
                    new AlertDialog.Builder(ConfirmOrderActivity.this)
                            .setMessage("订单支付成功！")
                            .setPositiveButton("确定",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent();
                                            ConfirmOrderActivity.this.setResult(51, intent);
                                            ConfirmOrderActivity.this.finish();
                                        }
                                    })
                            .create().show();
                else
                    Toast.makeText(ConfirmOrderActivity.this, "订单提交出错，请联系客服人员", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(ConfirmOrderActivity.this, "onError订单提交出错，请联系客服人员", Toast.LENGTH_LONG).show();
                Log.e("alipaytest", "onError订单提交");
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(ConfirmOrderActivity.this, "onCancelled订单提交出错，请联系客服人员", Toast.LENGTH_LONG).show();
                Log.e("alipaytest", "onCancelled");
            }

            @Override
            public void onFinished() {

            }
        });
    }
    private void postWXOrder(){
        String ddh1 = WXorderNumber.substring(5,15);

        RequestParams param = new RequestParams(App.CMDURL);
        param.addParameter("cmd", "21");
        param.addParameter("uid",  mSettingPref.getString("UserId", ""));  //下订单用户ID
        param.addParameter("time", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Long.parseLong(ddh1)*1000));
        param.addParameter("zjje", fl_total);  //支付金额
        param.addParameter("zffs", "2");    //支付方式（1支付宝，2微信）
        param.addParameter("user", user);  //收货人姓名
        param.addParameter("dh", phone); //收货人手机号
        param.addParameter("ldh", addr);  //详细地址
        param.addParameter("ddh", WXorderNumber);   //订单号
        param.addParameter("qid", mSettingPref.getString("gardenId", ""));   //小区ID号
        param.addParameter("goods", goods_str);    //商品
        x.http().post(param, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if(JsonSyncUtils.getJsonValue(result,"cw").equals("0")){
                    ConfirmOrderActivity.this.finish();
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

            }
        });
    }
}