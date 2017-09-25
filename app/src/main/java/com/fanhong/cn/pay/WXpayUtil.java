package com.fanhong.cn.pay;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.apache.http.conn.ssl.SSLSocketFactory;
import org.xmlpull.v1.XmlPullParser;
import org.xutils.http.RequestParams;

import java.io.IOException;
import java.io.StringReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class WXpayUtil {
    private IWXAPI api;
    private Context context;
    private PayReq req;
    private Map<String, String> resultunifiedorder;
    private static final String TAG = "WX_PAY_TAG";

    public WXpayUtil(Context context){
        //初始化微信支付
        this.context=context;
        api = WXAPIFactory.createWXAPI(context, ParameterConfig.WX_APPID);
        req = new PayReq();
        //生成prepay_id
        GetPrepayIdTask getPrepayId = new GetPrepayIdTask();
        getPrepayId.execute();
    }

    public void WeixinPay(OrderInfo order){
        RequestParams params = new RequestParams(ParameterConfig.WX_notifyUrl);
        params.addBodyParameter("order",order.toString());

    }

    /**
     * 用于获取
     * @author 95
     *
     */
    private class GetPrepayIdTask extends AsyncTask<String,Void, Map<String, String>> {

        private ProgressDialog dialog;


        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(context, "提示", "正在获取预支付订单...");
        }

        @Override
        protected void onPostExecute(Map<String, String> result) {
            if (dialog != null) {
                dialog.dismiss();
            }
            resultunifiedorder=result;
            String str = result.get("return_msg");
            Log.e("hu","********return_msg:"+str);
            if(str.equals("OK")){
                genPayReq();
            }else{
                showDialog(str);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected Map<String, String> doInBackground(String... params) {
            // TODO Auto-generated method stub

            String url = String.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
            //   String entity = genProductArgs();
            //   Log.e("orion","entity="+entity);

            //  byte[] buf = httpPost(url, entity);

            String content = new String("");
            Log.e("orion", "content="+content);
            Map<String,String> xml=decodeXml(content);

            return xml;
        }
    }


    private void genPayReq() {

        req.appId = ParameterConfig.WX_APPID;
        req.partnerId = ParameterConfig.WX_MCH_ID;
        req.prepayId = resultunifiedorder.get("prepay_id");
        req.packageValue = "prepay_id="+resultunifiedorder.get("prepay_id");
        req.nonceStr = genNonceStr();
        req.timeStamp = String.valueOf(genTimeStamp());


        Map<String, Object> map = new HashMap<String, Object>();
        map.put("appid", req.appId);
        map.put("noncestr", req.nonceStr);
        map.put("package", req.packageValue);
        map.put("partnerid", req.partnerId);
        map.put("prepayid", req.prepayId);
        map.put("timestamp", req.timeStamp);

        //req.sign = genAppSign(signParams);
        // Log.e("orion", signParams.toString());
        // sendPayReq();
    }
    private void sendPayReq() {
        api.registerApp(ParameterConfig.WX_APPID);
        api.sendReq(req);

    }

   /* private String genProductArgs() {
        StringBuffer xml = new StringBuffer();

        try {
            String  nonceStr = genNonceStr();
            xml.append("<xml>");
            List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
            packageParams.add(new BasicNameValuePair("appid", ParameterConfig.WX_APP_ID));
            packageParams.add(new BasicNameValuePair("body", order.productname));
            packageParams.add(new BasicNameValuePair("mch_id", ParameterConfig.WX_MCH_ID));
            packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
            packageParams.add(new BasicNameValuePair("notify_url", ParameterConfig.WX_notifyUrl));
            packageParams.add(new BasicNameValuePair("out_trade_no",order.outtradeno));
           // packageParams.add(new BasicNameValuePair("spbill_create_ip","127.0.0.1"));
            double dl = Double.parseDouble(order.totalamount) * 100;
            //packageParams.add(new BasicNameValuePair("total_fee", (int)(dl)+""));
            packageParams.add(new BasicNameValuePair("total_fee", "1"));
            packageParams.add(new BasicNameValuePair("trade_type", "APP"));


            String sign = genPackageSign(packageParams);
            packageParams.add(new BasicNameValuePair("sign", sign));



           String xmlstring =toXml(packageParams);
         //  return new String(xmlstring.toString().getBytes(), "ISO8859-1");
            return xmlstring;

        } catch (Exception e) {
            Log.e(TAG, "genProductArgs fail, ex = " + e.getMessage());
            return null;
        }


    }*/

    /*private String genAppSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(ParameterConfig.WX_API_KEY);


        String appSign = getMessageDigest(sb.toString().getBytes());
        Log.e("orion",appSign);
        return appSign;
    }*/


    /* private  HttpClient getNewHttpClient() {
            try {
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(null, null);

                SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
                sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

                HttpParams params = new BasicHttpParams();
                HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
                HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

                SchemeRegistry registry = new SchemeRegistry();
                registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
                registry.register(new Scheme("https", sf, 443));

                ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

                return new DefaultHttpClient(ccm, params);
            } catch (Exception e) {
                return new DefaultHttpClient();
            }
         }*/
    private class SSLSocketFactoryEx extends SSLSocketFactory {

        SSLContext sslContext = SSLContext.getInstance("TLS");

        public SSLSocketFactoryEx(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
                }
            };

            sslContext.init(null, new TrustManager[] { tm }, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }
    /*public  byte[] httpPost(String url, String entity) {
        if (url == null || url.length() == 0) {
            Log.e(TAG, "httpPost, url is null");
            return null;
        }

        HttpClient httpClient = getNewHttpClient();
        HttpPost httpPost = new HttpPost(url);

        try {
            httpPost.setEntity(new StringEntity(entity));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse resp = httpClient.execute(httpPost);
            if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                Log.e(TAG, "httpGet fail, status code = " + resp.getStatusLine().getStatusCode());
                return null;
            }

            return EntityUtils.toByteArray(resp.getEntity());
        } catch (Exception e) {
            Log.e(TAG, "httpPost exception, e = " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }*/

    //生成订单号
    // private String genOutTradNo() {
    //    Random random = new Random();
    //     return getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    // }

    public Map<String,String> decodeXml(String content) {

        try {
            Map<String, String> xml = new HashMap<String, String>();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(content));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {

                String nodeName=parser.getName();
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:

                        break;
                    case XmlPullParser.START_TAG:

                        if("xml".equals(nodeName)==false){
                            //实例化student对象
                            xml.put(nodeName,parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }

            return xml;
        } catch (Exception e) {
            Log.e("orion",e.toString());
        }
        return null;

    }

    //生成随机号，防重发
    private String genNonceStr() {
        Random random = new Random();
        return getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    private long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    public  String getMessageDigest(byte[] buffer) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(buffer);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    private void showDialog(String msg){
        AlertDialog alert = new AlertDialog.Builder(context)
                .setMessage(msg)
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                .create();
        alert.show();
    }
}
//</namevaluepair></namevaluepair></string,></string,></string,string></namevaluepair></namevaluepair></namevaluepair></namevaluepair></namevaluepair></string,string></string,string></string,string></void,></string,string>
