package com.fallwater.utilslibrary.utils;

import com.fallwater.utilslibrary.init.UtilLib;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;


public class ColorUtils {

    /**
     * 获取颜色
     */
    public static int getColor(int colorId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return UtilLib.getsDefaultInstance().getContext().getResources()
                    .getColor(colorId, null);
        } else {
            return UtilLib.getsDefaultInstance().getContext().getResources().getColor(colorId);
        }
    }

    public static int getColor(String col) {
        return Color.parseColor(col);
    }

    /**
     * 转换颜色
     */
    public static int adjustAlpha(
            @ColorInt int color, @SuppressWarnings("SameParameterValue") float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }
}
