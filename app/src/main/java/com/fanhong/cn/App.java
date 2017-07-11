package com.fanhong.cn;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.fanhong.cn.synctaskpicture.SaveInMemery;

import org.xutils.x;

import java.util.HashSet;
import java.util.Set;

import io.rong.imlib.RongIMClient;

/**
 * Created with Android Studio.
 *
 * @time: 2017/4/17 14:51
 * @author: Qiao He (hexianqiao3755@gmail.com)
 */
public class App extends Application {
    public int whichImg;

    public int getWhichImg() {
        return whichImg;
    }

    public void setWhichImg(int whichImg) {
        this.whichImg = whichImg;
    }

    public static String CMDURL = "http://m.wuyebest.com/index.php/App/index";
    public static SaveInMemery memery = SaveInMemery.getMemoryCache(50);

    public static Set<Long> old_msg_times=new HashSet<>();

    @Override
    public void onCreate() {
        super.onCreate();
        //Fresco初始化
        Fresco.initialize(this);
        //xUtils初始化
        x.Ext.init(this);
        /**
         * OnCreate 会被多个进程重入，这段保护代码，确保只有您需要使用 RongIMClient 的进程和 Push 进程执行了 init。
         * io.rong.push 为融云 push 进程名称，不可修改。
         */
        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext())) ||
                "io.rong.push".equals(getCurProcessName(getApplicationContext()))) {
            RongIMClient.init(this);
        }
    }

    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

}
