package com.fanhong.cn.synctaskpicture;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by Administrator on 2017/5/26.
 */

public class Upload {
    //???? ???????????????
    //url
    //String url = "http://192.168.0.2/yanshi_test/service/single_upload.php";
    //??��??
    //String filePath = "/mnt/sdcard/1.jpg";
    //???????????
    //String fileName = "1.jpg";
    //new UploadThread().start();

    static class UploadThread extends Thread {
        private String url;
        private String filePath;
        private String fileName;

        public UploadThread(String url, String filePath, String fileName) {
            this.url = url;
            this.filePath = filePath;
            this.fileName = fileName;
        }

        @Override
        public void run() {
            //url
            //String url = "http://192.168.0.2/yanshi_test/service/single_upload.php";
            //??��??
            //String filePath = "/mnt/sdcard/1.jpg";
            //???????????
            //String fileName = "1.jpg";
            String result = upload(url, fileName, filePath);
            System.out.println(result);
        }
    }

    private static String upload(String url, String fileName, String filePath) {
        BufferedReader in = null;
        int statusCode = -1;
        HttpURLConnection httpURLConnection;
        StringBuffer stringBuffer = new StringBuffer();
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "----WebKitFormBoundaryY3DVP8e5xKLHgAvm";
        try {
            URL realUrl = new URL(url);
            httpURLConnection = (HttpURLConnection) realUrl.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Accept-Encoding:", "gzip,deflate,sdch");
            httpURLConnection.setRequestProperty("Accept-Language:", "zh-CN,zh;q=0.8,en;q=0.6");
            httpURLConnection.setRequestProperty("User-Agent:", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/537.36");
            httpURLConnection.setRequestProperty("accept", "*/*");
            httpURLConnection.setRequestProperty("connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            long sendLength = 0; //???????????
            /* ????DataOutputStream */
            DataOutputStream ds = new DataOutputStream(httpURLConnection.getOutputStream());

            ds.writeBytes(twoHyphens + boundary + end);//��?????
            ds.writeBytes("Content-Disposition: form-data; " + "name=\"touxiang" +  "\";filename=\"");//��??Content-Disposition ?????????????
            ds.write(fileName.getBytes(Charset.forName("UTF-8"))); //��???????????
            ds.writeBytes("\"" + end);
            ds.writeBytes("Content-Type:" + "image/*" + end); //��??Content-Type?????MIME????
            ds.writeBytes(end);//��?????
            //* ????????FileInputStream *//*
            FileInputStream fStream = new FileInputStream(filePath);
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int length = -1;
            while ((length = fStream.read(buffer)) != -1) {//* ???????????????????? *//*
                ds.write(buffer, 0, length);//* ??????��??DataOutputStream?? *//*
                ds.flush();
                sendLength += length;
                /*if(asyncHttpResponseHandler != null) { //?????????????????
                    asyncHttpResponseHandler.onUploadProgress((int)(sendLength * 100 / sumSize));
                }*/
            }
            ds.writeBytes(end); //��???��?
            fStream.close();//close streams
            ds.flush();

            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);//��????????
            ds.flush();
            ds.close();
            //????????
            /*if(asyncHttpResponseHandler != null) {
                asyncHttpResponseHandler.onUploadCompleted();
            }*/

            // ????BufferedReader???????????URL?????
            in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            statusCode = httpURLConnection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                String line;
                char[] buffer1 = new char[1024];
                int len = -1;
                while( (len = in.read(buffer1, 0, buffer1.length)) != -1) {
                    stringBuffer.append(buffer1,0,len);
                }
            }
            httpURLConnection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //??????????????
        switch (statusCode) {
            case HttpURLConnection.HTTP_CLIENT_TIMEOUT:
                //errorMsg = "connect to server time out";
                break;
            case HttpURLConnection.HTTP_INTERNAL_ERROR:
                //errorMsg = "connect to server internal error";
                break;
            case HttpURLConnection.HTTP_NOT_FOUND:
                //errorMsg = "the url not found";
                break;
            case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
                //errorMsg = "gateway timeout";
                break;
        }
        return stringBuffer.toString();
    }

}
