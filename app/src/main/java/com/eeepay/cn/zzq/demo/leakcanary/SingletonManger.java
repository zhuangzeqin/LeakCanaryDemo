package com.eeepay.cn.zzq.demo.leakcanary;

import android.content.Context;

/**
 * 描述：常用的一种单例模式
 * 作者：zhuangzeqin
 * 时间: 2017/12/6-15:55
 * 邮箱：zzq@eeepay.cn
 */
public class SingletonManger {
   private static volatile SingletonManger mInstance = null;
    private Context context;

   private SingletonManger(Context context) {
       //使用Applicaton的Context，而我们单例的生命周期和应用的一样长，这样就防止了内存泄漏。
//       this.context = context.getApplicationContext();
       this.context = context;
   }
   public static SingletonManger getInstance(Context context) {
       if(mInstance == null) {
           synchronized (SingletonManger.class) {
               if(mInstance ==null) {
                   mInstance = new SingletonManger(context);
               }
           }
       }
       return mInstance;
   }
   //............其它的逻辑操作................
}
