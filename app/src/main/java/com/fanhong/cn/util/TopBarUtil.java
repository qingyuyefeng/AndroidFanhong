package com.fanhong.cn.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.fanhong.cn.GardenSelecterActivity;
import com.fanhong.cn.LoginActivity;

import java.util.Random;

/**
 * Created by Administrator on 2017/9/14.
 */

public class TopBarUtil {
    public static void initStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    public static void createDialog(final Context context, int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        switch (i) {
            case 0:
                builder.setTitle("你还没有登录哦");
                builder.setMessage("是否立即登录？");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, LoginActivity.class);
                        ((Activity) context).startActivityForResult(intent, 100);
                    }
                });
                break;
            case 1:
                builder.setTitle("你还没选择小区");
                builder.setMessage("是否立即去选择小区？");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, GardenSelecterActivity.class);
                        ((Activity) context).startActivityForResult(intent, 112);
                    }
                });
                break;
        }
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //设定listview的高度

    public static void setListViewHeight(ListView listView) {
        BaseAdapter listAdapter = (BaseAdapter) listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    //生成15位的随机字符串
    public static String getRandomString() {
        String s = "";
        CharSequence cs = "abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        int j = cs.length();
        for (int i = 0; i < 5; i++) {
            s += cs.charAt(random.nextInt(j));
        }
        s += System.currentTimeMillis() / 1000;
        return s;
    }
}
