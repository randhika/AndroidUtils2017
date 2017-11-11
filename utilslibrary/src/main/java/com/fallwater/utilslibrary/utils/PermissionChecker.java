package com.fallwater.utilslibrary.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;


public class PermissionChecker {

    public static boolean checkPermission(Context context, String permission) {
        try {
            return ActivityCompat.checkSelfPermission(context, permission)
                    == PackageManager.PERMISSION_GRANTED;
        } catch (Exception e) {
            return true;
        }
    }

}
