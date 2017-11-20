package com.fallwater.utilslibrary.utils;

import com.fallwater.utilslibrary.init.UtilLib;

/**
 * @author Fallwater潘建波 on 2017/11/20
 * @mail 1667376033@qq.com
 * 功能描述:
 */
public class StringUtil {

    public static String getString(int resId) {
        return UtilLib.getsDefaultInstance().getContext().getString(resId);
    }

    public static String getString(int resId, Object... formatArgs) {
        return UtilLib.getsDefaultInstance().getContext().getString(resId, formatArgs);
    }

}
