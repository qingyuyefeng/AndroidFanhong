package com.fanhong.cn.tools;

import android.content.Context;

import java.io.File;

public class FileUtils {
    /**
     * ??????jpg???????��??
     * <p>?????????????????
     * @param context
     * @return
     */
    public static String getCachePath(Context context) {
        return context.getExternalCacheDir().getAbsolutePath() + File.separator + System.currentTimeMillis() + ".jpg";
    }
}
