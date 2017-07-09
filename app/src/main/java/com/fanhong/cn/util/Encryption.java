package com.fanhong.cn.util;

//采用MD5加密方式
public class Encryption {
	public final static String getEncryptString(String str) {
		String result = null;
		try {
			result = MD5Util.getMD5String(str);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}