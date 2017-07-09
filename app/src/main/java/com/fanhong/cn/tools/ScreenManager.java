package com.fanhong.cn.tools;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class ScreenManager {
    /**
     * ??????????
     *
     * @param context
     * @return
     */
    public static int[] getScreenSize(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    }
}
