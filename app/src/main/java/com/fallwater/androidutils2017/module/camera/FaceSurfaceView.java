package com.fallwater.androidutils2017.module.camera;

import com.fallwater.androidutils2017.R;
import com.fallwater.utilslibrary.utils.LoggerUtils;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @author fallwater on 2017/11/11
 * @mail 1667376033@qq.com
 * 功能描述:用于显示Camera的SurfaceView
 */
public class FaceSurfaceView extends SurfaceView
        implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private static final String TAG = "FaceSurfaceView";

    private boolean isFrontCameraWhenInit;
//相机第一次打开时，配置打开前置相机/后置相机，true表示打开前置相机，false表示打开后置相机，仅在初始化时有效

    private ICamera mCamera;

    public FaceSurfaceView(Context context) {
        super(context);
        initView(context, null, 0);
    }

    public FaceSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0);
    }

    public FaceSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context
                .obtainStyledAttributes(attrs, R.styleable.FaceSurfaceView_Style, defStyleAttr, 0);
        isFrontCameraWhenInit = a.getBoolean(R.styleable.FaceSurfaceView_Style_frontCamera, true);
        a.recycle();
        mCamera = new CameraEmpty();
        startCameraCallback();
    }

    private void startCameraCallback() {
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            LoggerUtils.i("surfaceCreated");
            if (isFrontCameraWhenInit) {
                mCamera = CameraFactory.create(null);//打开前置相机
            } else {
                mCamera = CameraFactory.createBackCamera();//打开后置相机
            }
            //safeAutoFocus(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        LoggerUtils.i("surfaceChanged" + "w:" + width + ", h:" + height);
//        connectCamera();
        mCamera.changePreviewSize(holder, width, height);
        initFrames();
    }

    private void initFrames() {
        if (isCameraEmpty()) {
            return;
        }
        int mFrameWidth = mCamera.getPreSize()[0];
        int mFrameHeight = mCamera.getPreSize()[1];
        LoggerUtils.i("initFrames mFrameWidth:" + mFrameWidth + ", mFrameHeight:" + mFrameHeight);
        synchronized (this) {
            if (mFrameChain != null) {
                try {
                    mFrameChain[0].release();
                    mFrameChain[1].release();
                } catch (Exception e) {
                }
            }
            mFrameChain = new Mat[2];
            mFrameChain[0] = new Mat(mFrameHeight + (mFrameHeight / 2), mFrameWidth,
                    CvType.CV_8UC1);
            mFrameChain[1] = new Mat(mFrameHeight + (mFrameHeight / 2), mFrameWidth,
                    CvType.CV_8UC1);
            mCameraFrame = new JavaCameraFrame[2];
            mCameraFrame[0] = new JavaCameraFrame(mFrameChain[0], mFrameWidth, mFrameHeight);
            mCameraFrame[1] = new JavaCameraFrame(mFrameChain[1], mFrameWidth, mFrameHeight);
        }
    }

    private boolean isCameraEmpty() {
        return mCamera instanceof CameraEmpty;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreView();
    }

    private Mat[] mFrameChain;

    protected JavaCameraFrame[] mCameraFrame;

    private int mChainIdx = 0;

    private boolean mCameraFrameReady = false;


    @Override
    public void onPreviewFrame(byte[] frame, Camera camera) {
        synchronized (this) {
            mFrameChain[mChainIdx].put(0, 0, frame);
            mCameraFrameReady = true;
            this.notify();
        }
        if (mCamera != null) {
            mCamera.reloadCallbackBuffer();
        }
    }

    private Thread mThread;

    private boolean mStopThread;

    public void connectCamera() {
        if (isCameraEmpty()) {
            return;
        }
        mCameraFrameReady = false;
        /* now we can start update thread */
        mStopThread = false;
        mThread = new Thread(new CameraWorker());
        mThread.start();
    }

    public void disableView() {
        if (isCameraEmpty()) {
            return;
        }
        if (mStopThread) {
            return;
        }
        mStopThread = true;
        if (listener != null) {
            listener.onCameraViewStopped();
        }
        mCamera.stopPreView();
    }

    public void enableView(boolean flag) {
        if (isCameraEmpty()) {
            return;
        }
        if (listener != null) {
            listener.onCameraViewStarted(mCamera.getPreSize()[0], mCamera.getPreSize()[1]);
        }
        if (flag) {
            connectCamera();
        }
        mCamera.startPreView();
        safeAutoFocus(null);//开始预览,并且自动聚焦
        try {
            mCamera.setPreviewCallbackWithBuffer(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toggleCamera() {
        try {
            mCamera.toggleCamera();
            initFrames();
            mCamera.setPreviewCallbackWithBuffer(this);
        } catch (Exception e) {
            e.printStackTrace();
            mCamera = new CameraEmpty();
        }
    }

    public boolean isFrontCamera() {
        if (mCamera instanceof CameraOne) {
            return ((CameraOne) mCamera).isFrontCamera();
        }
        return false;
    }

    public int getCameraOrientation() {
        return mCamera.cameraOrientation();
    }

    public void releasCamera() {
        if (isCameraEmpty()) {
            return;
        }
        disableView();
        LoggerUtils.i("Disconnecting from camera");
        try {
            LoggerUtils.i("Notify thread");
            synchronized (this) {
                this.notify();
            }
            LoggerUtils.i("Wating for thread");
            if (mThread != null && !mStopThread) {
                mThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mThread = null;
        }
        mCameraFrameReady = false;
        /* Now release camera */
        mCamera.releaseCamera();
    }

    private class CameraWorker implements Runnable {

        @Override
        public void run() {
            do {
                boolean hasFrame = false;
                synchronized (FaceSurfaceView.this) {
                    try {
                        LoggerUtils.i("CameraWorker 1");
                        while (!mCameraFrameReady && !mStopThread) {
                            LoggerUtils.i("CameraWorker 2");
                            FaceSurfaceView.this.wait();
                        }
                        LoggerUtils.i("CameraWorker 3");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    LoggerUtils.i("CameraWorker mCameraFrameReady:" + mCameraFrameReady
                            + ",mStopThread:" + mStopThread + ", hasFrame:" + hasFrame);
                    if (mCameraFrameReady) {
                        LoggerUtils.i("CameraWorker 4");
                        mChainIdx = 1 - mChainIdx;
                        mCameraFrameReady = false;
                        hasFrame = true;
                    }
                }

                if (!mStopThread && hasFrame) {
                    if (!mFrameChain[1 - mChainIdx].empty()) {
                        deliverAndDrawFrame(mCameraFrame[1 - mChainIdx]);
                    }
                }
            } while (!mStopThread);
            LoggerUtils.i("Finish processing thread");
        }
    }

    private void deliverAndDrawFrame(CvCameraViewFrame mCvCameraViewFrame) {
        if (listener != null) {
            listener.onCameraFrame(mCvCameraViewFrame);
        }
    }

    public float getRadioW() {
        try {
            if (((mCamera.cameraOrientation() / 90) % 2 != 0)) {
                return getWidth() * 1.f / mCamera.getPreSize()[1];
            } else {
                return getWidth() * 1.f / mCamera.getPreSize()[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1.f;
    }

    public float getRadioH() {
        try {
            if (((mCamera.cameraOrientation() / 90) % 2 != 0)) {
                return getHeight() * 1.f / mCamera.getPreSize()[0];
            } else {
                return getHeight() * 1.f / mCamera.getPreSize()[1];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1.f;
    }

    public void autoFocus(Camera.AutoFocusCallback cb) {
        try {
            mCamera.autoFocus(cb);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 安全地聚焦,防止部分手机过慢而error
     * 调用应在preview之后
     */
    public void safeAutoFocus(Camera.AutoFocusCallback cb) {
        if (mCamera != null && isSupportAutoFocus()) {
            autoFocus(cb);
        }
    }

    public boolean isSupportAutoFocus() {
        if (mCamera instanceof CameraOne) {
            return ((CameraOne) mCamera).isSupportAutoFocus();
        }
        return false;
    }

    /**
     * 获取缓存中的图片
     */
    public Bitmap getPreBitmap() {
        Bitmap mCacheBitmap = null;
        synchronized (this) {
            Mat mYuvFrameData = null;
            Mat rgbaSrc = null;
            if (mFrameChain == null) {
                return null;
            }
            if (mFrameChain[mChainIdx] != null && !mFrameChain[mChainIdx].empty()) {
                mYuvFrameData = mFrameChain[mChainIdx];
                rgbaSrc = new Mat();
            } else if (mFrameChain[1 - mChainIdx] != null && !mFrameChain[1 - mChainIdx].empty()) {
                mYuvFrameData = mFrameChain[1 - mChainIdx];
                rgbaSrc = new Mat();
            } else {
                return null;
            }
            Imgproc.cvtColor(mYuvFrameData, rgbaSrc, Imgproc.COLOR_YUV2RGB_NV21, 4);
            Mat mRgba = new Mat();
            if (getCameraOrientation() == 270) {
                Core.flip(rgbaSrc.t(), mRgba, -1);
            } else if (getCameraOrientation() == 180) {
                // Rotate clockwise 180 degrees
                Core.flip(rgbaSrc, mRgba, -1);
            } else if (getCameraOrientation() == 90) {
                // Rotate clockwise 90 degrees
                Core.flip(rgbaSrc.t(), mRgba, 1);
            } else if (getCameraOrientation() == 0) {
                // No rotation
                mRgba = rgbaSrc;
            }
            mCacheBitmap = Bitmap
                    .createBitmap(mRgba.width(), mRgba.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(mRgba, mCacheBitmap);
            releaseMat(mRgba);
            releaseMat(rgbaSrc);
        }
        return mCacheBitmap;
    }

    private void releaseMat(Mat mat) {
        if (mat == null) {
            return;
        }
        try {
            mat.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void takePicture(Camera.ShutterCallback shutter, Camera.PictureCallback raw,
            Camera.PictureCallback jpeg) {
        mCamera.takePicture(shutter, raw, jpeg);
    }

    private CvCameraViewListener2 listener;

    public void setCvCameraViewListener(CvCameraViewListener2 listener) {
        this.listener = listener;
    }

    private class JavaCameraFrame implements CvCameraViewFrame {

        @Override
        public Mat gray() {
            return mYuvFrameData.submat(0, mHeight, 0, mWidth);
        }

        @Override
        public Mat rgba() {
            Imgproc.cvtColor(mYuvFrameData, mRgba, Imgproc.COLOR_YUV2RGBA_NV21, 4);
            return mRgba;
        }

        public JavaCameraFrame(Mat Yuv420sp, int width, int height) {
            super();
            mWidth = width;
            mHeight = height;
            mYuvFrameData = Yuv420sp;
            mRgba = new Mat();
        }

        public void release() {
            mRgba.release();
        }

        private Mat mYuvFrameData;

        private Mat mRgba;

        private int mWidth;

        private int mHeight;
    }

    ;

    public interface CvCameraViewFrame {

        /**
         * This method returns RGBA Mat with frame
         */
        Mat rgba();

        /**
         * This method returns single channel gray scale Mat with frame
         */
        Mat gray();
    }

    public interface CvCameraViewListener2 {

        /**
         * This method is invoked when camera preview has started. After this method is invoked
         * the frames will start to be delivered to client via the onCameraFrame() callback.
         *
         * @param width  -  the width of the frames that will be delivered
         * @param height - the height of the frames that will be delivered
         */
        void onCameraViewStarted(int width, int height);

        /**
         * This method is invoked when camera preview has been stopped for some reason.
         * No frames will be delivered via onCameraFrame() callback after this method is called.
         */
        void onCameraViewStopped();

        /**
         * This method is invoked when delivery of the frame needs to be done.
         * The returned values - is a modified frame which needs to be displayed on the screen.
         * TODO: pass the parameters specifying the format of the frame (BPP, YUV or RGB and etc)
         */
        Mat onCameraFrame(CvCameraViewFrame inputFrame);
    }

}
