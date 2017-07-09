package com.fanhong.cn.pay;

public class ParameterConfig {

    // public static final String  GANHOST = "http://101.226.197.11"; //服务器地址ip（根据自己替换）
    public static final String  GANHOST = "http://m.wuyebest.com"; //服务器地址ip（根据自己替换）

    /**
     * 微信
     */
    //appid 微信分配的公众账号ID
    public static final String WX_APP_ID = "wx14af47ab474a0991";
    //商户号 微信分配的公众账号ID
    public static final String WX_MCH_ID = "141567530";
    //  API密钥，在商户平台设置
    public static final String WX_API_KEY = "ganzhoualpha112114115WXZHOUALPHA";
    //服务器回调接口
    public static final String WX_notifyUrl = "http://m.nongjia7.com/Home/Pay/returnurl";// 用于微信支付成功的回调（按自己需求填写）


    /**
     * 支付宝
     */
 /*
  //合作身份者id，以2088开头的16位纯数字，申请支付宝签约成功后就会看见   商户PID
  	public static final String PARTNER = "2088801938414822";
  	//填写收款支付宝账号，即付款后到账的支付宝账号    商户收款账号
  	public static final String SELLER = "linjianhua@chinabird.com";
  	//商户私钥，自助生成，即rsa_private_key.pem中去掉首行，最后一行，空格和换行最后拼成一行的字符串；
  	//如果android版本太高，要用PKCS8格式用户私钥   商户私钥，pkcs8格式
  	public static final String RSA_PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKzXAqmo1On94CQZ"+
  			"MQloJgcFW6FaVWSJQp9Fy44j0pZqnaSqKUhHn51ihl1FB0ealOCpJgyjDf5/jK53"+
  			"6rrs2hLKenTLsUbJ/OMGRZNkquDQvN6nx+FDCJum3aSWRv//Ldk64hMatIXfxtMT"+
  			"PnscOvhUGFOpDwqE1nc4epxv03srAgMBAAECgYAVRF+PgLayFAsAZhkijy4erwoX"+
  			"UIYxWLJZ5rylQMMovvWmzUlHG4kIVJcz4sqChQLMogOMioOuTQOCdD7twTcT5QCG"+
  			"MgDWqRZ3of3o+hJCBQnvWJCL39hDm+M94CgcxogoBsuaRbttYbrbgfM6LgObdvdg"+
  			"GMb0wKqyntDtU5s0AQJBAN16mnLDI+s1egMoKwJI2tmze/EX6V+DhuVNJtRDee/b"+
  			"rqQ9qtx7L8/cvcYE0vWr5+wVIWDBwbxRBnj8wXr4JnUCQQDHx5/avRlpiWtIhrXY"+
  			"YB2bOoQod/RSHl5GbSI4rkmoj6wlQdVoM6ZoCKWoL5735iDVIPDA94V+YUXHgYiG"+
  			"3ycfAkEA1NBjKhUCJZd8Bx6IvSg+SmOhm5eLYoDilODeh6XSvDyOAjlTPYqM9aG5"+
  			"JFYsU238aygeq3dibk449vZ//2YEpQJAd7DBtOtWLkSGYg1TJS9wjW+hfh1f41MO"+
  			"aGM5/Xtt5e8t06R9ltBbAjksBneBZkQZp5b0pqek8vKUO9L/WM6ZQQJBAJ3BWzuR"+
  			"RNFjTjcXFbOESB97080iXNuVSwwz6fExO1nl/qJxEnIhtL3JGP4nk2yHJ7nKE6Yw"+
  			"d+TY0+3jKAaebHU=";
  	//支付宝（RSA）公钥，用签约支付宝账号登录ms.alipay.com后，在密钥管理页面上传生成的公钥并获取   支付宝公钥
  	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQ" +
  			"KBgQCs1wKpqNTp/eAkGTEJaCYHBVuhWlVkiUKfRcuOI9KWap2kqilIR5+dYoZdRQdH" +
  			"mpTgqSYMow3+f4yud+q67NoSynp0y7FGyfzjBkWTZKrg0Lzep8fhQwibpt2klkb//y" +
  			"3ZOuITGrSF38bTEz57HDr4VBhTqQ8KhNZ3OHqcb9N7KwIDAQAB";
     */


    /** 支付宝支付业务：入参app_id */
    public static final String APPID = "2017042006834434";

    /** 支付宝账户登录授权业务：入参pid值 */
    public static final String PID = "2088021513189336";
    /** 支付宝账户登录授权业务：入参target_id值 */
    public static final String TARGET_ID = "";

    /** 商户私钥，pkcs8格式 */
    /** 如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个 */
    /** 如果商户两个都设置了，优先使用 RSA2_PRIVATE */
    /** RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议使用 RSA2_PRIVATE */
    /** 获取 RSA2_PRIVATE，建议使用支付宝提供的公私钥生成工具生成， */
    /** 工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1 */
    public static final String RSA2_PRIVATE = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDgMLAYZFc8SP9q/4+jNQhl1dAYXNVogAC3QeWfgEX/Prfs4iA/mNNcd65QI7/JHwiyAVXGwzwfhO0uNvnFKSHT3qyWPR0JzaMlFp9MbKN7jUXyRshwSQWMXepBtZrrKOczL3rHQf71LH6JBTs4dffMHXLkUWfp2N2Al9cMIhXaShiNZsLBCkSSTWNc05N1OdLuuT2/H7h1RJu3GGFTeSKuU1dNXUaZ+jl6h6iy2mSABYsBuDs/d9M46mBasNMswZQfeyJWyXHigfQmHdVkPlEa1q+XDIWhmroqb+EQAH0ITQFaol+KumB44qczg4sICb7wBm+Av+mRqSIvOpPwCbq7AgMBAAECggEBAJ4KtJj9Y2HkM+rB7ggnwpvHdsM17luUoOyPT8h5LN8a353masVqftWYqRFlojWt4vtZZZaQ2zl9A5aourr6nBY3y4K94lKe3HuwzuuZSkY2+TAqJfLCB19EdXeNBoDwfMvglLUTGSDNFQaS4YST5PPHbs71dEOt1bhpUfgDr1QJxcA3pxGBpOTl6zBl6SE9X1k5IsG3PDHRJmN1l65GXXzA8i3l4oJByALP8x5JCG4k5L2XwiyojI1hYh0OcjnaQm5++xDGNCBCuChDg+aL/g6pZqgOIUeGz40mMQL/LWeiTvzxRM+pyHLFu0COqRSbHY4ZxfdJmFh0R7q7wfTTDIECgYEA/BLay5Ue5y9lYQ8qGX5LFc/6r3b9DIr2+v5XZKSZRXgSog+c4mnDhphmlBt+kSfqJ/ydGZxOOIwpsYm1Ee+UpZ+3wFDkcg9kDNJbZLhEG9KOY2ElmqXHOgsMmQFRhuI+OxytmgAGs9+p+Sd8+zWWUEmJtYSs/CWo4J+iZyd70VMCgYEA466l0DMvdodq9NYwcySC1rSpkOz75S63tLuc+XhQ+Iuu9cpoN/3VB4a5GJcz5GpXTbpuVpYGqo5aup3TG+84Y5yzxTuR5ky3uRNUQmzafMU0XVBAUnXoYnHO1XXWA12Dz4LMM22hO5QiN0yO3UqD0m2d6AlgBIGE7onEnGaPO/kCgYBFjGY1uWUXYCpWhzqUYhYRZJrEAs56wXS3d6lhKVDoq+JoOGqOxaOe5TmYL6XSIvMmKTqnMEscqv9t7pK46iNXNyQZubfuCHUQXJG+zmBsVMHYhZs8efTn/Nhy9rwpvQ5yfM0hKlSvGEzrY4vGO9jHOiL2pC6eOz3HDARg9bHh5QKBgA9tsUzHeyrkCEppDUVwLvGPsoHgnT0tikNUB1IcZhhBluyO97r7l2j4JyjUbi7HSf05aeqyJed+2AUXVsvozQBrCP+PhtZ9UmHXPnl12ORllKPSIVm1JbcuER8D6im2N41jRqUhg7B+3Ea1CGVUfFRRG0czA2+quzBS46DZjlE5AoGAWlglFUzAB+Ev69FlV9bN++eXDTKVGRpvINiqins3/Fy+dxUbu4woLab6CghSs7ZF0no4F3cGD0sdZNpgPwvJMEBUIFvKdtnHNEUw2J2GIgJktn6JYdXyNoquBY0xL//GB4+BghEMlYO3XUAq+wr+6/jm9yVxonr21eUOCStXV+c=";
    public static final String RSA_PRIVATE = "";
}