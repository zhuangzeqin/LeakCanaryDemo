# LeakCanaryDemo
总结经常遇见内存泄漏的场景
![image](https://github.com/zhuangzeqin/LeakCanaryDemo/blob/master/device-2017-12-06-162051.png)

package com.eeepay.cn.zzq.demo.leakcanary;

import android.content.Context;

import android.os.AsyncTask;

import android.os.Bundle;

import android.os.Handler;

import android.os.Message;

import android.os.SystemClock;

import android.support.v7.app.AppCompatActivity;

import android.view.View;

import android.widget.Button;

import java.lang.ref.WeakReference;

/**
 * 描述：内存泄漏的各个场景的分析
 * 场景 总结了大概这么几种方式吧；平常极有可能遇到的情况
 * 作者：zhuangzeqin
 * 时间: 2017/12/6-15:35
 * 邮箱：zzq@eeepay.cn
 */
 
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private android.widget.Button button1;
    
    private android.widget.Button button2;
    
    private android.widget.Button button3;
    
    private android.widget.Button button4;
    
    private android.widget.Button button5;
    
    private android.widget.Button button6;
    
    private static testOption mtestOption;//静态实例
    
    //比较常用的Handler 声明方式
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //...
        }
    };
    //正确的使用Handler 方式
//    private MyHandler mHandler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.button6 = (Button) findViewById(R.id.button6);
        this.button5 = (Button) findViewById(R.id.button5);
        this.button4 = (Button) findViewById(R.id.button4);
        this.button3 = (Button) findViewById(R.id.button3);
        this.button2 = (Button) findViewById(R.id.button2);
        this.button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button1://错误的使用单例造成的内存泄漏
                //当这Activity退出时，Activity应该被回收， 但是单例中又持有它的引用，导致Activity回收失败，造成内存泄漏。
                SingletonManger.getInstance(this);
                finish();
                break;
            case R.id.button2://Handler造成的内存泄漏
                //由于mHandler是Handler的非静态匿名内部类的实例，所以它持有外部类Activity的引用，我们知道消息队列是在一个Looper线程中不断轮询处理消息，那么当这个Activity退出时消息队列中还有未处理的消息或者正在处理消息，而消息队列中的Message持有mHandler实例的引用，
                // mHandler又持有Activity的引用，所以导致该Activity的内存资源无法及时回收，引发内存泄漏
                final Message message = Message.obtain();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mHandler.sendMessage(message);
                    }
                }, 3000 * 4);
                finish();
                break;
            case R.id.button3:
            //对于线程造成的内存泄漏，也是平时比较常见的一个场景
 
                //线程的使用方式1---Runnable都是一个匿名内部类，因此它们对当前Activity都有一个隐式引用。如果Activity在销毁之前，任务还未完成，         
//                那么将导致Activity的内存资源无法回收，造成内存泄漏

//                new Thread(new Runnable() {

//                    @Override
//                    public void run() {

//                        SystemClock.sleep(10000);
//                    }

//                }).start();

                //线程的使用方式2---异步使用AsyncTask 的时候；
                
                new AsyncTask<Void, Void, Void>() {
                
                    @Override
                    protected Void doInBackground(Void... params) {
                    
                        SystemClock.sleep(10000);//耗时的操作
                        
                        return null;
                    }
                }.execute();
                
                //正确的做法
//                new Thread(new MyRunnable()).start();

                //正确的做法
//                new MyAsyncTask(this).execute();

                finish();
                break;
            case R.id.button4:
                //非静态内部类创建静态实例造成的内存泄漏
                //因为非静态内部类默认会持有外部类的引用;而又使用了该非静态内部类创建了一个静态的实例，
                // 该实例的生命周期和应用的一样长，这就导致了该静态实例一直会持有该Activity的引用，导致Activity的内存资源不能正常回收。
                if (mtestOption == null) {
                    mtestOption = new testOption();
                }
                finish();
                break;
            case R.id.button5:

                break;
            case R.id.button6:
                //对于使用了BraodcastReceiver，ContentObserver，File，Cursor，Stream，Bitmap等资源的使用，
                // 应该在Activity销毁时及时关闭或者注销，否则这些资源将不会被回收，造成内存泄漏。这里就不一一举了
                break;
            default://7
                //另外
                
                //MVP模式的使用注意，因为如果在网络请求的过程中Activity就关闭了，Presenter还持有了V层的引用，也就是MainActivity，也会发生内存泄露。
                
                //将P层和V层的关联抽出两个方法，一个绑定，一个解绑，在需要的时候进行绑定V层，不需要的时候进行解绑就可以了。
                
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 移除消息队列中所有消息和所有的Runnable。
        
        // 当然也可以使用mHandler.removeCallbacks();
        
        // 或mHandler.removeMessages();来移除指定的Runnable和Message。
        
        mHandler.removeCallbacksAndMessages(null);
        
        //MyAsyncTask 销毁时候也应该取消相应的任务AsyncTask::cancel()，避免任务在后台执行浪费资源。
        
        //new MyAsyncTask(this).cancel(true);
    }

    static class MyRunnable implements Runnable {
        @Override
        public void run() {
            SystemClock.sleep(10000);
        }
    }

    /**
     * 正确的做法还是使用静态内部类的方式
     */
    static class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<Context> weakReference;//使用弱引用

        public MyAsyncTask(Context context) {
            weakReference = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(Void... params) {
            SystemClock.sleep(10000);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            MainActivity activity = (MainActivity) weakReference.get();
            if (activity != null) {
                //...
            }
        }
    }

    /**
     * 非静态类
     */
    class testOption {
        //正确的做法为：
        //将该内部类设为静态内部类
        // 或将该内部类抽取出来封装成一个单例，如果需要使用Context，请使用ApplicationContext
        String name;
    }
    /**
     * 创建一个静态Handler内部类，然后对Handler持有的对象使用弱引用，这样在回收时也可以回收Handler持有的对象，这样虽然避免了Activity泄漏，
     * 不过Looper线程的消息队列中还是可能会有待处理的消息，所以我们在Activity的Destroy时或者Stop时应该移除消息队列中的消息，
     */
    private static class MyHandler extends Handler {
        private WeakReference<Context> reference;//使用弱引用

        public MyHandler(Context context) {
            reference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = (MainActivity) reference.get();
            if (activity != null) {
                //更新UI
            }
        }
    }

}

如果对你有用；不妨请小编我喝个咖啡；嘻嘻；你的动力；是我不懈努力

![image](https://github.com/zhuangzeqin/APPChannel/blob/master/TIM20171109141728.png)
