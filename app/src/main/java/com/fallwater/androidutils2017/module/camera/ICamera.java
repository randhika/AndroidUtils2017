package com.fallwater.androidutils2017.module.camera;

import android.hardware.Camera;
import android.view.SurfaceHolder;

public interface ICamera {

    void setPreViewDisplay(SurfaceHolder holder);

    void startPreView();

    void stopPreView();

    void releaseCamera();

    void changePreviewSize(SurfaceHolder holder, int w, int h);

    /**
     * 切换前后摄像头
     */
    void toggleCamera();

    void autoFocus(Camera.AutoFocusCallback cb);

    void setPreviewCallbackWithBuffer(Camera.PreviewCallback cb);

    void reloadCallbackBuffer();

    /**
     * 获取预览尺寸
     * @return
     */
    int[] getPreSize();

    /**
     * 获取摄像头选择角度
     * @return
     */
    int cameraOrientation();

    void takePicture(Camera.ShutterCallback shutter, Camera.PictureCallback raw,
            Camera.PictureCallback jpeg);
}
