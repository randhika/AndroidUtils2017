package com.fallwater.androidutils2017.module.camera;

import android.app.Activity;

/**
 * @author fallwater on 2017/11/11
 * @mail 1667376033@qq.com
 * 功能描述: 相机工厂
 */
public class CameraFactory {

    /**
     * 创建前置摄像头
     */
    public static ICamera create(Activity activity) throws RuntimeException {
        return new CameraOne(activity);
    }

    /**
     * 创建后置摄像头
     */
    public static ICamera createBackCamera() throws RuntimeException {
        return new CameraOne();
    }

}
