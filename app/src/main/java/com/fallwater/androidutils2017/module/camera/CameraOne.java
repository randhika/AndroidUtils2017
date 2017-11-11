package com.fallwater.androidutils2017.module.camera;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.List;

/**
 * @author fallwater on 2017/11/11
 * @mail 1667376033@qq.com
 * 功能描述: CameraOne
 */
public class CameraOne implements ICamera {

    private Camera camera;

    private Activity activity;

    private int cameraId;

    private SurfaceHolder holder;

    private int cameraNum;

    /**
     * 打开前置摄像头
     */
    public CameraOne(Activity activity) {
        cameraNum = Camera.getNumberOfCameras();
        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int i = 0; i < cameraNum; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                break;
            }
        }
        camera = Camera.open(cameraId);
        this.activity = activity;
    }

    /**
     * 打开后置摄像头
     */
    public CameraOne() {
        cameraNum = Camera.getNumberOfCameras();
        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int i = 0; i < cameraNum; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                break;
            }
        }
        camera = Camera.open(cameraId);
    }

    @Override
    public void setPreViewDisplay(SurfaceHolder holder) {
        try {
            this.holder = holder;
            setOrientation(activity);
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startPreView() {
        try {
            camera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopPreView() {
        try {
            camera.stopPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void releaseCamera() {
        try {
            camera.stopPreview();
            camera.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int preW, preH;

    @Override
    public void changePreviewSize(SurfaceHolder holder, int w, int h) {
        preW = w;
        preH = h;
        initCameraParameters(w, h);
        setPreViewDisplay(holder);
        startPreView();
    }

    private byte[] mBuffer;

    private int[] preSize;

    private void initCameraParameters(int w, int h) {
        try {
            Camera.Parameters parameters = camera.getParameters();
            preSize = getRightSize(w, h);
            int size = preSize[0] * preSize[1];
            size = size * ImageFormat.getBitsPerPixel(parameters.getPreviewFormat()) / 8;
            mBuffer = new byte[size];
            camera.addCallbackBuffer(mBuffer);
            parameters.setPreviewSize(preSize[0], preSize[1]);
            parameters.setPreviewFormat(ImageFormat.NV21);
            parameters.setPictureFormat(ImageFormat.JPEG);
            int[] picSize = getRigthPicSize(preSize[0], preSize[1]);
            if (picSize[0] != 0 && picSize[1] != 0) {
                parameters.setPictureSize(picSize[0], picSize[1]);
            }
            camera.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int[] getPreSize() {
        return preSize;
    }

    @Override
    public int cameraOrientation() {
        return mCameraOrientation;
    }

    private boolean isLand;

    private int mCameraOrientation;

    public boolean isFrontCamera() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            return true;
        }
        return false;
    }

    /**
     * 获取合适的预览尺寸
     */
    private int[] getRightSize(int w, int h) {
        List<Camera.Size> size = camera.getParameters().getSupportedPreviewSizes();
        int[] result = null;
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        if (size != null) {
            result = new int[2];
            isLand = ((info.orientation / 90) % 2 != 0);
            mCameraOrientation = info.orientation;
            float radioSpan = Integer.MAX_VALUE;
            float viewRadio = getSizeRadio(w, h); // 界面的宽高比
            for (int i = 0; i < size.size(); ++i) {
                if (isLand) { // 摄像头选择了90度
                    if (size.get(i).height == w) { // 找到与View 相同的宽度的预览尺寸，有可能有多个 , 找出比例最接近的
                        float s = Math.abs(getSizeRadio(size.get(i).height, size.get(i).width)
                                - viewRadio);
                        if (s < radioSpan) {
                            radioSpan = s;
                            result[0] = size.get(i).width;
                            result[1] = size.get(i).height;
                        }
                    }
                } else {
                    if (size.get(i).width == w) {
                        float s = Math.abs(getSizeRadio(size.get(i).width, size.get(i).height)
                                - viewRadio);
                        if (s < radioSpan) {
                            radioSpan = s;
                            result[0] = size.get(i).width;
                            result[1] = size.get(i).height;
                        }
                    }
                }
            }

            if (result[0] == 0 || result[1] == 0) { // 如果当前没有找到，则重新使用另一种策略。
                for (int i = 0; i < size.size(); ++i) {
                    if (isLand) { // 摄像头选择了90度
                        float s = Math.abs(getSizeRadio(size.get(i).height, size.get(i).width)
                                - viewRadio);
                        if (s < radioSpan) {
                            radioSpan = s;
                            result[0] = size.get(i).width;
                            result[1] = size.get(i).height;
                        }
                    } else {
                        float s = Math.abs(getSizeRadio(size.get(i).width, size.get(i).height)
                                - viewRadio);
                        if (s < radioSpan) {
                            radioSpan = s;
                            result[0] = size.get(i).width;
                            result[1] = size.get(i).height;
                        }
                    }
                }
            }
        }
        return result;
    }

    private float getSizeRadio(int w, int h) {
        return w * 1.f / h;
    }

    private int[] getRigthPicSize(int w, int h) {
        List<Camera.Size> size = camera.getParameters().getSupportedPictureSizes();
        int[] result = new int[2];
        try {
            for (int i = 0; i < size.size(); i++) {
                int picW = size.get(i).width;
                int picH = size.get(i).height;
                if ((picW == w && picH == h) || (picW == h && picH == w)) {
                    result[0] = picW;
                    result[1] = picH;
                }
            }
            if (result[0] == 0 || result[1] == 0) {
//                ACRA.getErrorReporter().handleSilentException(new RuntimeException("picSize == 0"));
                result[0] = size.get(0).width;
                result[1] = size.get(1).height;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void toggleCamera() {
        releaseCamera();
        initCamera(holder);
        startPreView();
    }

    @Override
    public void autoFocus(Camera.AutoFocusCallback cb) {
        try {
            camera.autoFocus(cb);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isSupportAutoFocus() {
        if (camera == null) {
            return false;
        }
        try {
            Camera.Parameters p = camera.getParameters();
            if (p == null) {
                return false;
            }
            List<String> focusModes = p.getSupportedFocusModes();
            if (focusModes != null && focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                //Phone supports autofocus!
                return true;
            } else {
                //Phone does not support autofocus!
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void takePicture(Camera.ShutterCallback shutter, Camera.PictureCallback raw,
            Camera.PictureCallback jpeg) {
        try {
            camera.takePicture(shutter, raw, jpeg);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initCamera(SurfaceHolder holder) {
        cameraId = (cameraId + 1) % cameraNum;
        camera = Camera.open(cameraId);
        initCameraParameters(preW, preH);
        setPreViewDisplay(holder);
    }

    /**
     * 旋转摄像头
     */
    public void setOrientation(Activity activity) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = 0;
        if (activity != null) {
            rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        }
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
            default:
                break;
        }

        int result = 0;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    public void setPreviewCallbackWithBuffer(Camera.PreviewCallback cb) {
        camera.setPreviewCallbackWithBuffer(cb);
    }

    @Override
    public void reloadCallbackBuffer() {
        camera.addCallbackBuffer(mBuffer);
    }

}
