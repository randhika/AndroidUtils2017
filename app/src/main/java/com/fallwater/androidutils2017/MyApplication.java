package com.fallwater.androidutils2017;

import com.facebook.stetho.Stetho;
import com.orhanobut.hawk.Hawk;

import android.app.Application;

/**
 * Created by
 * @author fallwater on 2017/11/1.
 *         功能描述:
 */
public class MyApplication extends Application {

    private static MyApplication mInstance = null;

    public static MyApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Stetho.initializeWithDefaults(this);
        Hawk.init(this);
    }
}
