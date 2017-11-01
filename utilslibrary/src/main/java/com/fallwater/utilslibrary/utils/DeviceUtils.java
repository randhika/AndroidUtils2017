package com.fallwater.utilslibrary.utils;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * @author fallwater on 2017/11/1.
 *         功能描述:DeviceUtils
 */
public class DeviceUtils {

    /**
     * 获取手机型号
     */
    public static String getPhoneModel() {
        return Build.MODEL;
    }

    /**
     * Android操作系统的版本
     */
    public static String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取手机号码
     */
    public static String getPhoneNumber(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getLine1Number();//手机号码
    }

    /**
     * 获取手机的DeviceId
     */
    public static String getIMEI(Context context) {
        String deviceId;
        try {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            deviceId = tm.getDeviceId();
        } catch (Exception e) {
            deviceId = null;
        }
        return deviceId;
    }

    public static String[] fetchImei(Context context) {
        String imei1 = "";
        String imei2 = "";
        if (Build.VERSION.SDK_INT < 21) {
            imei1 = getIMEI(context);
            // 21版本是5.0，判断是否是5.0以上的系统  5.0系统直接获取IMEI1,IMEI2,MEID
        } else if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT < 23) {
            imei1 = getImei(context, 0);
            imei2 = getImei(context, 1);
        } else if (Build.VERSION.SDK_INT >= 23) {
            imei1 = getImeiOf23(context, 0);
            imei2 = getImeiOf23(context, 1);
        }
        return new String[]{imei1, imei2};
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static String getImeiOf23(Context context, int slot) {
        String imei1 = "";
        String imei2 = "";
        if (ActivityCompat
                .checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {

            final TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(TELEPHONY_SERVICE);
            imei1 = tm.getDeviceId(0);
            imei2 = tm.getDeviceId(1);
        }
        if (slot == 0) {
            return imei1;
        }
        if (slot == 1) {
            return imei2;
        }
        return imei1;

    }

    private static String getImei(Context context, int slot) {
        String imei1 = "";
        String imei2 = "";
        Map<String, String> map = new HashMap<String, String>();
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(TELEPHONY_SERVICE);
        imei1 = tm.getDeviceId();
        Class<?> clazz = null;
        Method method = null;//(int slotId)
        try {

            clazz = Class.forName("android.os.SystemProperties");
            method = clazz.getMethod("get", String.class, String.class);
            String gsm = (String) method.invoke(tm, "ril.gsm.imei", "");
            Log.d("TAG", "" + gsm);
            if (!TextUtils.isEmpty(gsm)) {
                //the value of gsm like:xxxxxx,xxxxxx
                String imeiArray[] = gsm.split(",");
                if (imeiArray.length > 0) {
                    imei1 = imeiArray[0];
                    if (imeiArray.length > 1) {
                        imei2 = imeiArray[1];
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (slot == 0) {
            return imei1;
        }
        if (slot == 1) {
            return imei2;
        }
        return imei1;

    }

    public static String getSerial() {
        try {
            return Build.class.getField("SERIAL").get(null).toString();
        } catch (Exception e) {
            return "";
        }
    }

    public static String getAndroidId(Context context) {
        return Settings.Secure
                .getString(context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
    }

    public static String getPsuedoID() {
        return Build.BOARD
                + Build.BRAND
                + Build.CPU_ABI
                + Build.DEVICE
                + Build.MANUFACTURER
                + Build.MODEL
                + Build.PRODUCT;
    }

    /**
     * Return pseudo unique ID
     *
     * @return ID
     */
    public static String getUniquePsuedoID(Context context) {
        String id1 = "";
        try {
            id1 = getSerial();
        } catch (Exception exception) {
            //do nothing
        }
        String id2 = Settings.Secure
                .getString(context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
        if (id2 == null) {
            id2 = "";
        }
        String id3 = getPsuedoID();
        String id = id1 + id2 + id3;
        return new UUID(id.hashCode(), id3.hashCode()).toString();
    }

    /**
     * 生成Akulaku唯一标识符 (待数据服务器兼容处理后更改)
     *
     * @param str AndroidId+SerialNo+PsuedoId
     * @return (e.g ee34d343-0448-2472-3336-5a680f096744)
     * @throws NoSuchAlgorithmException 环境不支持MD5
     */
    private static String generateAllId(String str)
            throws NoSuchAlgorithmException {
        byte[] digest = MessageDigest.getInstance("MD5").digest((str).getBytes(
                Charset.forName("UTF-8")));
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < digest.length; i++) {
            hexString.append(Integer.toHexString(
                    (digest[i] & 0x000000FF) | 0xFFFFFF00).substring(6));
            if (i == 3 || i == 5 || i == 7 || i == 9) {
                hexString.append("-");
            }
        }
        return hexString.toString();
    }

    /**
     * 延时
     */
    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前进程名
     */
    public static String getProcessName(Context context) {
        int pid = android.os.Process.myPid();
        String processName = "";
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) {
            if (process.pid == pid) {
                processName = process.processName;
            }
        }
        return processName;
    }

    /**
     * 服务器指定Android端上传的DeviceType
     */
    public static String getDeviceType() {
        return "4";//TODO 设备类型待确定
    }

    /**
     * 获取当前APP的版本名
     */
    public static String getCurrentVersionName(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = pm.getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

}
