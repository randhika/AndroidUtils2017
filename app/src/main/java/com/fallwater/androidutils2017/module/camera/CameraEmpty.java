package com.fallwater.androidutils2017.module.camera;

import android.hardware.Camera;
import android.view.SurfaceHolder;

/**
 * Created by
 *  on 2017/6/29.
 * 空摄像头实现。处理打开摄像头异常时逻辑处理
 */

public class CameraEmpty implements ICamera {
    @Override
    public void setPreViewDisplay(SurfaceHolder holder) {

    }

    @Override
    public void startPreView() {

    }

    @Override
    public void stopPreView() {

    }

    @Override
    public void releaseCamera() {

    }

    @Override
    public void changePreviewSize(SurfaceHolder holder, int w, int h) {

    }

    @Override
    public void toggleCamera() {

    }

    @Override
    public void autoFocus(Camera.AutoFocusCallback cb) {

    }

    @Override
    public void setPreviewCallbackWithBuffer(Camera.PreviewCallback cb) {

    }

    @Override
    public void reloadCallbackBuffer() {

    }

    @Override
    public int[] getPreSize() {
        return new int[0];
    }

    @Override
    public int cameraOrientation() {
        return 0;
    }

    @Override
    public void takePicture(Camera.ShutterCallback shutter, Camera.PictureCallback raw, Camera.PictureCallback jpeg) {

    }
}
