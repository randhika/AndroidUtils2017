package com.fallwater.utilslibrary.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.List;


/**
 * Created by
 *
 * @author fallwater on 2017/10/30.
 *         功能描述:系统工具类
 */
public class OsUtil {

    /**
     * 是否为MIUI系统
     */
    public static boolean isMIUI() {
        return !TextUtils.isEmpty(getSystemProperty("ro.miui.ui.version.name"));
    }

    /**
     * 是否为flyme系统
     */
    public static boolean isFlyme() {
        try {
            final Method method = Build.class.getMethod("hasSmartBar");
            return method != null;
        } catch (final Exception e) {
            return false;
        }
    }

    /**
     * 获取指定属性值
     */
    public static String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return line;
    }

    /**
     * PackageManager可以乱用吗？
     * https://mp.weixin.qq.com/s?__biz=MzIxNjc0ODExMA==&mid=2247484104&idx=1&sn=4082bc400d6784c5e07e3779bde662f4&chksm=97851be9a0f292ffb1a1c770dd027a3460ae49d3a2b34875ed0a72ce7d7118288cfa28dd6ee0&scene=21#wechat_redirect
     * 使用缓存和锁解决抛出「Package manager has died」的RuntimeException的异常
     */

    /**
     * 获取安装的所有APP PackageInfo
     */
    public static List<PackageInfo> getInstalledApps(Context context) {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES);
    }

    /**
     * 判断指定包名的APP是否安装
     */
    public static boolean isInstalled(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager
                    .getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return packageInfo != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 根据包名，获取PackageInfo
     */
    public static PackageInfo getPackageInfo(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return null;
        }
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager
                    .getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return packageInfo;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取版本号
     */
    public static int getVersionCode(Context context, String packageName) {
        PackageInfo packageInfo = getPackageInfo(context, packageName);
        if (packageInfo != null) {
            return packageInfo.versionCode;
        }
        return -1;
    }

    /**
     * 获取版本名称
     */
    public static String getVersionName(Context context, String packageName) {
        PackageInfo packageInfo = getPackageInfo(context, packageName);
        if (packageInfo != null) {
            return packageInfo.versionName;
        }
        return null;
    }

    /**
     * 获取应用Icon
     */
    public static String getApplicationLabel(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return null;
        }
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = getPackageInfo(context, packageName);
        if (packageInfo != null) {
            return packageInfo.applicationInfo.loadLabel(packageManager).toString();
        }
        return null;
    }

    /**
     * 根据apk文件获取PackageInfo
     */
    public static PackageInfo getPackageArchiveInfo(Context context, String apkPath) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
            return packageInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
