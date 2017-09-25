package com.fanhong.cn.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2017/9/20.
 */

public class MySharedPrefUtils {

    public static SharedPreferences getSharedPref(Context c){
        return c.getSharedPreferences("Setting",Context.MODE_PRIVATE);
    }
    public static String getUserId(Context c){
        return getSharedPref(c).getString("UserId","");
    }
    public static int getStatus(Context c){
        return getSharedPref(c).getInt("Status",0);
    }
    public static String getGardenName(Context c){
        return getSharedPref(c).getString("gardenName", "");
    }
}
