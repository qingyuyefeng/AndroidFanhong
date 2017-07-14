package com.fanhong.cn;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

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

    //判断权限集合
    public boolean lacksPermissions(String... permissions){
        for(String permission:permissions){
            if(lacksPermissions(permission)){
                return true;
            }
        }
        return false;
    }
    private void requestPermission(Activity activity,int permissionCode) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // 第一次请求权限时，用户如果拒绝，下一次请求shouldShowRequestPermissionRationale()返回true
            // 向用户解释为什么需要这个权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_PHONE_STATE)) {
                //申请此权限
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.CAMERA}, permissionCode);
            }
        }
    }
//    private void requestPermission() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
//                != PackageManager.PERMISSION_GRANTED) {
//            // 第一次请求权限时，用户如果拒绝，下一次请求shouldShowRequestPermissionRationale()返回true
//            // 向用户解释为什么需要这个权限
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
//                new AlertDialog.Builder(this)
//                        .setMessage("申请相机权限")
//                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                //申请相机权限
//                                ActivityCompat.requestPermissions(RuntimePermissionsActivity.this,
//                                        new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
//                            }
//                        })
//                        .show();
//            } else {
//                //申请相机权限
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
//            }
//        } else {
//            tvPermissionStatus.setTextColor(Color.GREEN);
//            tvPermissionStatus.setText("相机权限已申请");
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == CAMERA_REQUEST_CODE) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                tvPermissionStatus.setTextColor(Color.GREEN);
//                tvPermissionStatus.setText("相机权限已申请");
//            } else {
//                //用户勾选了不再询问
//                //提示用户手动打开权限
//                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
//                    Toast.makeText(this, "相机权限已被禁止", Toast.LENGTH_SHORT).show();
//                }
}
