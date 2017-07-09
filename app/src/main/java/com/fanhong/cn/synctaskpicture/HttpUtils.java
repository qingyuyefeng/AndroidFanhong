package com.fanhong.cn.synctaskpicture;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2017/6/2.
 */

public class HttpUtils {
    private HttpUtils httpUtils;

    private HttpUtils(){}

    public HttpUtils getInstance(){
        if(httpUtils == null){
            httpUtils = new HttpUtils();
        }
        return httpUtils;
    }

    //中间没有handler发送消息指令时可调用
    public String httpPost(String url,int readTime,int connectTime,String content) throws Exception{
        StringBuffer sb = new StringBuffer();
        URL url1 = new URL(url);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url1.openConnection();
        httpURLConnection.setReadTimeout(readTime);
        httpURLConnection.setConnectTimeout(connectTime);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setUseCaches(false);
        OutputStream os = httpURLConnection.getOutputStream();
        os.write(content.getBytes());
        os.flush();
        if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
            BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(),"utf-8"));
            String s;
            while((s=br.readLine())!=null){
                sb.append(s);
            }
        }
        return sb.toString();
    }
}
