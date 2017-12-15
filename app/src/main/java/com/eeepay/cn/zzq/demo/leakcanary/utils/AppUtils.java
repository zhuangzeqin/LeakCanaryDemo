package com.eeepay.cn.zzq.demo.leakcanary.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;

/**
 * 描述：使用 ApplicationInfo.FLAG_DEBUGGABLE
 * Android 技巧-Debug 判断不再用 BuildConfig
 * 作者：zhuangzeqin
 * 时间: 2017/12/15-10:35
 * 邮箱：zzq@eeepay.cn
 */
public class AppUtils {
    private static Boolean isDebug = null;

    /**
     * 是否为Debug
     *
     * @return
     */
    public static boolean isDebug() {
        return isDebug == null ? false : isDebug.booleanValue();
    }

    /**
     * 在自己的 Application 内调用进行初始化，
     AppUtils.syncIsDebug(getApplicationContext());
     这样以后调用 AppUtils.isDebug() 即可判断是否是 Debug 版本
     * @param context
     */
    public static void syncIsDeBug(Context context) {
        if (isDebug == null) {
            isDebug = context.getApplicationInfo() != null &&
                    (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        }
    }
}
