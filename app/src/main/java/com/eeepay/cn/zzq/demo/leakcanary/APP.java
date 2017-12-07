package com.eeepay.cn.zzq.demo.leakcanary;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * 描述：利用 LeakCanary 来检查 Android 内存泄漏
 * 作者：zhuangzeqin
 * 时间: 2017/12/6-15:27
 * 邮箱：zzq@eeepay.cn
 */
public class APP extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }
}
