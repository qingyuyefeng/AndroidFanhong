package com.fanhong.cn.pay;

public class ParameterConfig {

    public static final String[] descript = {"社区卖场购买商品消费","汽车代办年审消费"};

    // public static final String  GANHOST = "http://101.226.197.11"; //服务器地址ip（根据自己替换）
    public static final String  GANHOST = "http://m.wuyebest.com"; //服务器地址ip（根据自己替换）

    /**
     * 微信
     */
    //appid 微信分配的app应用ID
    public static final String WX_APPID = "wxea49e10e35c4b1ea";
    //商户号——微信分配的公众账号ID
    public static final String WX_MCH_ID = "1488497082";
    //需要生成的10位时间戳
    public static final String TIME_STAMP = System.currentTimeMillis()/1000 + "";
    //根据包名生成的签名
    public static final String WX_SIGN = "80891e400e0c3619df730ebef548a9e3";
    //  API密钥，在商户平台设置
    public static final String WX_API_KEY = "qiangxu15123073170QIANGXU1234567";//传到后台存入
    //服务器回调接口
    public static final String WX_notifyUrl = "http://m.wuyebest.com/public/WeiPay/index.php";// 用于微信支付成功的回调（按自己需求填写）
    //服务端统一下单接口
    public static final String WX_getOrderUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";


    /**
     * 支付宝
     */

    public static final String alipay_APPID = "2017082508372012";
    // 商户收款账号
    public static final String alipay_SELLER = "18725732573@139.com";
    // 支付宝公钥
    public static final String alipay_RSA_PUBLIC = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApdHlve2U3JjPDeXv+30PkA5pCkwtdPOodAHF6qCXwcyeS7/BVtx9GVbZhdI+inOY2oJI4ll3METeGmeGw99962V7YkAJu7+r9SVpDdoXz1jo8zATq/vVi7mCRSxhsfPmJ3YZfZUSWOf/ECfrkh6t+LROvBIa8VHhyaoLp5/zbCyFhFdfyk4/EWee+McNxtnehVlMknvjm6rCQ1A2Eyy+NyryA/nShclJL6wr5l/N4tSaH5dsSBHexoGXswE+5JQ6J+GQ/hNpiU4bUWLVDRd5OsnqYsS4xSqmVVwGG4Ts3xO1/skNORAFQ7DYMti1U8uxu1z5tPUljqbpYCWB3N8BOQIDAQAB";
    // 支付宝密钥
    public static  String alipay_RSA_PRIVATE = "";
    public static final String alipay_SERVICE_CALLBACK = "http://m.wuyebest.com/library/callback.php";
}