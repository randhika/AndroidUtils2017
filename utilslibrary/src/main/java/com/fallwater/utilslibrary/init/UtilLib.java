package com.fallwater.utilslibrary.init;

import android.content.Context;

/**
 * @author fallwater on 2017/11/11
 * @mail 1667376033@qq.com
 * 功能描述:
 */
public class UtilLib {

    private static volatile UtilLib sDefaultInstance;

    private Context mContext;


    private UtilLib() {
    }

    public static UtilLib getsDefaultInstance() {
        if (sDefaultInstance == null) {
//            throw new Exception("UtilLib not init.....");
        }
        return sDefaultInstance;
    }

    public void init(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }
}
