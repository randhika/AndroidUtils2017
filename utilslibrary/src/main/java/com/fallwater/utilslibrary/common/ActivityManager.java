package com.fallwater.utilslibrary.common;

import android.app.Activity;

import java.util.Stack;

/**
 * Created by cy on 2017/9/8.
 */

public class ActivityManager {

    private static ActivityManager mActivityManager;

    private static Stack<Activity> mActivityStack = new Stack<>();

    //将构造方法私有化，所以不能通构造方法来初始化ActivityManager
    private ActivityManager() {
    }

    //采用单例模式初始化ActivityManager，使只初始化一次
    public static ActivityManager getInstance() {
        if (mActivityManager == null) {
            mActivityManager = new ActivityManager();
        }
        return mActivityManager;
    }

    //添加activity
    public void addActivity(Activity activity) {
        if (activity != null) {
            mActivityStack.add(activity);
        }
    }

    //移除activity
    public void remove(Activity activity) {
        mActivityStack.remove(activity);
    }

    //删除当前的activity
    public void removeCurrent() {
        Activity activity = getCurrentActivity();
        activity.finish();
        mActivityStack.remove(activity);
    }

    public Activity getCurrentActivity() {
        Activity activity = mActivityStack.lastElement();
        return activity;
    }

    //移除指定的Activity
    public void removeAndFinish(Activity activity) {
        if (activity != null) {
            for (int i = mActivityStack.size() - 1; i >= 0; i--) {
                Activity currentActivity = mActivityStack.get(i);
                if (currentActivity.getClass().equals(activity.getClass())) {
                    currentActivity.finish();//销毁当前的activity
                    mActivityStack.remove(i);//从栈空间移除
                }
            }
        }
    }

    //移除所有的Activity
    public void removeAll(Activity activity) {
        for (int i = mActivityStack.size() - 1; i >= 0; i--) {
            activity.finish();
            mActivityStack.remove(i);
        }
    }

    //返回栈大小
    public int size() {
        return mActivityStack == null ? 0 : mActivityStack.size();
    }
}
