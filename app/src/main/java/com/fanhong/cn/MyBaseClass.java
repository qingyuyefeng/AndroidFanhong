package com.fanhong.cn;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

/**
 * Created by Administrator on 2017/7/13.
 */

public class MyBaseClass {
    private static MyBaseClass myBaseClass;
    private MyBaseClass(){}
    public static MyBaseClass getInstance(){
        if(myBaseClass == null){
            myBaseClass = new MyBaseClass();
        }
        return myBaseClass;
    }

    public static String[] PERMISSION = {Manifest.permission.READ_PHONE_STATE};
    public static boolean isLacksOfPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(
                    new App().getApplicationContext(), permission) == PackageManager.PERMISSION_DENIED;
        }
        return false;
    }
}
