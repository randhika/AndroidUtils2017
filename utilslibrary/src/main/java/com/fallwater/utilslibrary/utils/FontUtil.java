package com.fallwater.utilslibrary.utils;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;

/**
 * @author Fallwater潘建波 on 2017/11/20
 * @mail 1667376033@qq.com
 * 功能描述:
 */


public class FontUtil {

    private static HashMap<String, Typeface> fontCache = new HashMap<>();

    public static Typeface getDefaultTypeface(Context context) {
        String fontName = "DinBold.otf";
        return getTypeface(fontName, context);
    }

    public static Typeface getTypeface(String fontName, Context context) {
        Typeface typeface = fontCache.get(fontName);

        if (typeface == null) {
            try {
                typeface = Typeface.createFromAsset(context.getAssets(), fontName);
            } catch (Exception e) {
                return null;
            }

            fontCache.put(fontName, typeface);
        }

        return typeface;
    }
}
