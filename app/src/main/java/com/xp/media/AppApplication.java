package com.xp.media;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.xp.media.util.LogUtils;


/**
 * @类描述：应用常量类
 * @创建人：Wangxiaopan
 * @创建时间：2018/1/17 0017 14:30
 * @修改人：
 * @修改时间：2018/1/17 0017 14:30
 * @修改备注：
 */

public class AppApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashCapture.getInstance().init(this);
        LogUtils.init(this);
        refWatcher = LeakCanary.install(this);
    }

    //refWatcher.watch(object)来监控当前对象的回收情况
    private RefWatcher refWatcher;

    public static RefWatcher getRefWatcher(Context context) {
        AppApplication application = (AppApplication) context.getApplicationContext();
        return application.refWatcher;
    }

}
