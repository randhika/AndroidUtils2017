package com.fallwater.androidutils2017.module.camera;

import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fallwater.androidutils2017.R;
import com.fallwater.androidutils2017.common.ThreadPoolFactory;
import com.fallwater.androidutils2017.utils.ToastManager;
import com.fallwater.utilslibrary.common.BaseActivity;
import com.fallwater.utilslibrary.constant.Const;
import com.fallwater.utilslibrary.utils.CommonUtils;
import com.fallwater.utilslibrary.utils.FileUtils;
import com.fallwater.utilslibrary.utils.GlideUtil;
import com.fallwater.utilslibrary.utils.ImageUtils;
import com.fallwater.utilslibrary.utils.LoggerUtils;
import com.fallwater.utilslibrary.utils.PermissionChecker;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Observable;
import java.util.concurrent.atomic.AtomicBoolean;

import uk.co.senab.photoview.PhotoView;

public class FaceDetectionActivity extends BaseActivity
        implements View.OnClickListener, FaceSurfaceView.CvCameraViewListener2 {

    private final static int REQUEST_CODE_ALBUM = 1000;

    private static int REQUEST_FILESYSTEM_PERMISSION = 2001;

    private static int REQUEST_CAMERA_PERMISSION = 2002;

    /**
     * 用于标记那个界面跳转过来的
     */
    private final static String EXTRA_ACTIONNAME = "action_name";

    private final static String TAG = "FaceDetectionActivity";

    private TextView albumTv;

    private TextView batalTv;

    private PhotoView photoView;

    private ViewStub surfaceViewStub;

    private FaceSurfaceView surfaceView;

    private View toggleView;

    private TextView faceTips;

    private View faceMain;

    private View faceMainGet;

    private FaceDetectionFloatView floatView;

    private View takePhotoView;

    private View detectTipView;

    private String actionName;

    private FaceGuideView guideView;

    private FaceGuideStartView guideStartView;

    private ToastManager toastManager;

    private MyObservable.MyObserver observer = new MyObservable.MyObserver(
            "load_finish") {

        @Override
        public void update(Observable observable, MyObservable.MyObserver observer) {
            finish();
        }
    };

    static {
        try {
            System.loadLibrary("opencv_java3");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startActivity(Context context, String actionName) {
        Intent intent = new Intent(context, FaceDetectionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_ACTIONNAME, actionName);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facedetection_layout);
        initView();
    }

    private void initView() {
        albumTv = (TextView) findViewById(R.id.detection_album);
        albumTv.setOnClickListener(this);
        batalTv = (TextView) findViewById(R.id.detection_batal);
        batalTv.setOnClickListener(this);
        photoView = (PhotoView) findViewById(R.id.face_photo);
        toggleView = findViewById(R.id.face_switch_btn);
        toggleView.setOnClickListener(this);
        floatView = (FaceDetectionFloatView) findViewById(R.id.face_flowview);
        faceTips = (TextView) findViewById(R.id.face_tips);
        faceMain = findViewById(R.id.face_main);
        faceMainGet = findViewById(R.id.face_main_get);
        surfaceViewStub = (ViewStub) findViewById(R.id.face_camera);
        takePhotoView = findViewById(R.id.detection_take_photo);
        takePhotoView.setOnClickListener(this);
        detectTipView = findViewById(R.id.face_detecting_tips);
        guideView = (FaceGuideView) findViewById(R.id.face_guide_view);
        guideView.setOnClickListener(this);
        guideStartView = (FaceGuideStartView) findViewById(R.id.face_detection_guide_start);
        guideStartView.setOnStartClickListener(this);
        guideStartView.setStartText(getString(R.string.face_start));
        toastManager = new ToastManager(this);
        MyObservable.instance().add(observer);
    }

    private void requestCamera() {
        if (PermissionChecker.checkPermission(this, Manifest.permission.CAMERA)) {
            initSurfaceView();
        } else {
            String[] INITIAL_PERMS = {Manifest.permission.CAMERA};
//            ActivityCompat.requestPermissions(this, INITIAL_PERMS, REQUEST_CAMERA_PERMISSION);
//            requestMyPermissions(INITIAL_PERMS, REQUEST_CAMERA_PERMISSION);
        }
    }

    /**
     * 初始化surfaceView
     * 当获取权限后再加载摄像头
     */
    private void initSurfaceView() {
        if (surfaceView == null) {
            surfaceView = (FaceSurfaceView) surfaceViewStub.inflate()
                    .findViewById(R.id.face_camera_surfaceview);
            surfaceView.setCvCameraViewListener(this);
            mHandler.sendEmptyMessageDelayed(WHAT_LOAD_SUC, 500);
        }
    }

    /**
     * 人脸识别模式
     */
    private final static int MODE_CAMERA_FACE = 0;

    /**
     * 选择照片模式
     */
    private final static int MODE_PHOTO = 1;

    /**
     * 手动拍照模式
     */
    private final static int MODE_CAMERA_TAKE = 2;

    /**
     * 当前模式
     */
    private int currentMode = MODE_CAMERA_FACE;

    /**
     * 推动相片模式
     */
    private void toPhotoMode() {
        currentMode = MODE_PHOTO;
        toggleView.setVisibility(View.GONE);
        faceTips.setVisibility(View.VISIBLE);
        faceTips.setText(getString(R.string.face_detection_drag));
        takePhotoView.setVisibility(View.GONE);
        if (surfaceView != null) {
            surfaceView.setVisibility(View.GONE);
            surfaceView.disableView();
        }
        faceMain.setVisibility(View.VISIBLE);
        photoView.setVisibility(View.VISIBLE);
        albumTv.setVisibility(View.VISIBLE);
        albumTv.setText(getString(R.string.face_submit));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.detection_album:
                int resId = R.id.detection_album;//相册按钮 //ctrlNo:3
                if (currentMode != MODE_PHOTO) { //当前是相机模式，点击跳转到相册选择
                    clickToPhoto();
                } else { // 当前是相片模式，点击跳转到提交
                    clickToSubmit();
                }
                v.setTag(resId);
                break;
            case R.id.face_switch_btn:
                clickToChangeCamera();
                break;
            case R.id.detection_batal: // 取消界面
                finish();
                break;
            case R.id.face_camera:
                surfaceView.autoFocus(null);
                break;
            case R.id.detection_take_photo://拍照
                clickToTakePhoto();
                break;
            case R.id.face_guide_view:
                clickGuideView();
                break;
            case R.id.face_guide_btn:
                clickGuideStart();
                break;
            default:
                break;
        }
    }

    private void clickGuideStart() {
        guideStartView.setVisibility(View.GONE);
        guideStartView.timerFinish();
        requestCamera();
    }

    private void clickGuideView() {
        guideView.setVisibility(View.GONE);
    }

    /**
     * 点击手动拍照
     */
    private void clickToTakePhoto() {
        if (surfaceView == null) {
            return;
        }
        if (takePhotoView.getVisibility() == View.VISIBLE) {
            takePhotoView.setClickable(false);
        }
        if (surfaceView.isSupportAutoFocus()) {//如果支持自动对焦，则对焦后再取照片
            surfaceView.autoFocus((success, camera) -> {
                final Bitmap bitmap = surfaceView.getPreBitmap();
                surfaceView.disableView();
                if (bitmap != null) {
                    processPhoto(bitmap, true);
                } else {
                    showBackToTakePhotoDialog();
                }
            });

        } else {//如果相机不支持自动对焦，直接取照片
            final Bitmap bitmap = surfaceView.getPreBitmap();
            surfaceView.disableView();
            if (bitmap != null) {
                processPhoto(bitmap, true);
            } else {
                showBackToTakePhotoDialog();
            }
        }
    }

    private void clickToSubmit() {
//        final Bitmap bitmap = photoView.getVisibleRectangleBitmap();
        photoView.setDrawingCacheEnabled(true);
        final Bitmap bitmap = Bitmap.createBitmap(photoView.getDrawingCache());
        photoView.setDrawingCacheEnabled(false);
        processPhoto(bitmap, false);//此处false表示处理相册图片
    }

    private void saveAndExit(final Bitmap bitmap) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                String path = FileUtils.getImageCachePath(System.currentTimeMillis() + ".jpg");
                boolean result = ImageUtils.saveBitmapToFile(path, bitmap);
                bitmap.recycle();
                if (result) {
                    postPhotoBack(path);
                }
                finish();
                return null;
            }

        }.execute();
    }

    /**
     * 回传拍摄或者选中的照片到ValDynamicFragment
     */
    private void postPhotoBack(String path) {
    }

    /**
     * 点击去相册选择图片
     */
    private void clickToPhoto() {
        dismissDialog();
        if (surfaceView != null) {
            surfaceView.disableView();
        }
        requestFileSystemPermission();
    }

    private void dismissDialog() {
//        if (mSelectDialog != null && mSelectDialog.isShowing()) {
//            mSelectDialog.dismiss();
//        }
    }

    private void requestFileSystemPermission() {
        if (PermissionChecker.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            chooseExistingPhoto();
        } else {
            String[] INITIAL_PERMS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
//            ActivityCompat.requestPermissions(this, INITIAL_PERMS, REQUEST_FILESYSTEM_PERMISSION);
//            requestMyPermissions(INITIAL_PERMS, REQUEST_FILESYSTEM_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_FILESYSTEM_PERMISSION) {
            chooseExistingPhoto();
        } else if (requestCode == REQUEST_CAMERA_PERMISSION) {
            initSurfaceView();
        }
    }

    /*******************************************************************
     * 附加选项 数据类型 描述
     * crop String 发送裁剪信号
     * aspectX int X方向上的比例
     * aspectY int Y方向上的比例
     * outputX int 裁剪区的宽
     * outputY int 裁剪区的高
     * scale boolean 是否保留比例
     * return-data boolean 是否将数据保留在Bitmap中返回
     * data Parcelable 相应的Bitmap数据
     * circleCrop String 圆形裁剪区域？
     * MediaStore.EXTRA_OUTPUT ("output") URI 将URI指向相应的file:///...
     *******************************************************************/
    @SuppressLint("InlinedApi")
    private void chooseExistingPhoto() {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        } else {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        }
        intent.setType(Const.IMAGE_UNSPECIFIED);
        setOutputInfo(intent);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        Intent chooserIntent = Intent.createChooser(intent, "facedetection");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(chooserIntent, REQUEST_CODE_ALBUM);
        } else {
            Toast.makeText(this, "pick_photo_not_supported", Toast.LENGTH_SHORT).show();
        }
    }

    private void setOutputInfo(Intent intent) {
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_CODE_ALBUM) {
                loadAlbum(data.getData());
                toPhotoMode();
                showIndicator();
            }
        }
        if (requestCode == REQUEST_CODE_ALBUM) {
            String ctrlValue = data == null ? null : data.toString();
            int resId = (int) albumTv.getTag();
            if (resId == 0) {
                resId = R.id.detection_album;//普通界面相册选择结果
            }
        }
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

    private android.graphics.Rect imageRect;

    private android.graphics.Rect faceRect;

    private int minWidth;

    /**
     * 手动拍照或者从相册获取的图像是否通过人脸识别检测,如果没有通过检测弹出对应对话框，提示用户重新拍照或者选择
     *
     * @param bitmap 需要上传的图像
     */
    private void processPhoto(final Bitmap bitmap, boolean fromCamera) {
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        if (fromCamera) {
            if (surfaceView.isFrontCamera()) {
                minWidth = (int) (bitmap.getWidth() * 0.25f);
            } else {
                minWidth = (int) (bitmap.getWidth() * 0.10f);
            }
        } else {
            minWidth = (int) (bitmap.getWidth() * 0.10f);
        }
        imageRect = new android.graphics.Rect(0, 0, bitmapWidth, bitmapHeight);
        Mat mat = new Mat(bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8UC4);
        Utils.bitmapToMat(bitmap, mat);
        MatOfRect pictureMatOfRect = new MatOfRect();

        //CascadeClassifier.detectMultiScale(Mat image, MatOfRect objects, double scaleFactor, int minNeighbors, int flags, Size minSize, Size maxSize)说明
        //image:灰阶图像Mat，必须为8位，
        //objects:输出矩形
        //scaleFactor:缩放比例，必须大于1
        //minNeighbors:合并窗口时最小neighbor，每个候选矩阵至少包含的附近元素个数
        //flags:flag常数
        //minSize:最小检测目标
        //maxSize:最大检测目标
        if (mJavaDetector != null) {
            mJavaDetector.detectMultiScale(mat, pictureMatOfRect, 1.1, 2, 2,
                    new Size(minWidth, minWidth), new Size());//检测参数暂时如此设定
        }
        Rect[] facesArray = pictureMatOfRect.toArray();
        releaseMat(mat);
        pictureMatOfRect.release();

        int string = (facesArray != null) ? facesArray.length : -1;
        LoggerUtils.d(TAG, "识别出的人脸数目:" + string);
        if (fromCamera) {//如果图片来自摄像头，弹出重新拍照对话框
            if (facesArray == null || facesArray.length == 0) {
                showBackToTakePhotoDialog();
                return;
            }
            Rect corRect = getAreaMaxRect(facesArray);
//            setFaceRect(corRect);
            if (!isFitFaceRect3(corRect, bitmapWidth, bitmapHeight, 0)) {
                showBackToTakePhotoDialog();
                return;
            }
            faceRect = new android.graphics.Rect(corRect.x, corRect.y, corRect.x + corRect.width,
                    corRect.y + corRect.height);//这里是否需要乘以RadioW/RadioH?
            saveAndExit(bitmap);
        } else {//如果图片来自相册，弹出是否返回相册对话框
            if (facesArray == null || facesArray.length == 0) {
                showBackToAblumDialog();
                return;
            }
            Rect corRect = getAreaMaxRect(facesArray);
//            setFaceRect(corRect);
            if (!isFitFaceRect3(corRect, bitmapWidth, bitmapHeight, 1)) {
                showBackToAblumDialog();
                return;
            }
            faceRect = new android.graphics.Rect(corRect.x, corRect.y, corRect.x + corRect.width,
                    corRect.y + corRect.height);
            saveAndExit(bitmap);
        }
    }

    /**
     * 判断提交的图片是否符合位置和大小要求
     *
     * @param rect
     * @param width
     * @param height
     * @param flag,标志位，flag=0表示图片来自手动拍照，flag=1表示来自相册
     * @return
     */
    float widthMinRate;

    private boolean isFitFaceRect3(Rect rect, int width, int height, int flag) {
        if (rect == null) {
            return false;
        }
        float positionMinRate;
        float positionMaxRate;
        float positionYBottomRate = 0.100f;//脸部识别区域Y方向最低点距离底部最低要求
        float widthMaxRate;

        //flag只能是0，1
        if (flag == 0) {//来自拍照
            positionMinRate = 0.010f;
            positionMaxRate = 0.500f;
            if (surfaceView.isFrontCamera()) {//使用前置相机拍照，脸部区域限制增加
                widthMinRate = 0.380f;
            } else {
                widthMinRate = 0.200f;//使用后置相机拍照，脸部区域限制减小
            }
            widthMaxRate = 0.800f;
        } else if (flag == 1) {//来自相册的图片
            positionMinRate = 0.100f;
            positionMaxRate = 0.500f;
            widthMinRate = 0.200f;
            widthMaxRate = 0.800f;
        } else {
            return false;//其他直接返回false
        }

        LoggerUtils.d(TAG, "rectWidth=" + rect.width + ",rectHeight=" + rect.height);
        if (rect.y > height * positionMaxRate
                || rect.y < height * positionMinRate) {//系数设置为0.1~0.6，执行宽松检测要求
            LoggerUtils.d(TAG, "photo position fail,required percent is [" + positionMinRate + "-"
                    + positionMaxRate + "],your result=" + rect.y * 1.0f / height);
            return false;
        }
        if (rect.y + rect.height > height * (1 - positionYBottomRate)) {//系数设置为0.1，执行宽松检测要求
            LoggerUtils.d(TAG,
                    "photo position fail,required percent is 0-" + (1 - positionYBottomRate)
                            + ",your result=" + (rect.y + rect.height) * 1.0f / height);
            return false;
        }
        boolean widthFit = rect.width < width * widthMinRate || rect.width > width * widthMaxRate;
        if (widthFit) {
            LoggerUtils.d(TAG,
                    "widthFit fail,required percent is [" + widthMinRate + "-" + widthMaxRate
                            + "],your result=" + rect.width * 1.0f / width);
            return false;
        }
        boolean heightFit = rect.height < width * widthMinRate
                || rect.height > width * widthMaxRate;
        if (heightFit) {
            LoggerUtils.d(TAG,
                    "heightFit fail,required percent is [" + widthMinRate + "-" + widthMaxRate
                            + "],your result=" + rect.height * 1.0f / width);
            return false;
        }

        LoggerUtils.d(TAG, "photo position sucess,required percent is [" + positionMinRate + "-"
                + positionMaxRate + "],your result=" + rect.y * 1.0f / height);
        LoggerUtils.d(TAG,
                "widthFit success,required percent is [" + widthMinRate + "-" + widthMaxRate
                        + "],your result=" + rect.width * 1.0f / width);
        return true;
    }

    /**
     * 如果识别出若干个人脸区域，选择区域面积最大的一个？
     */
    private Rect getAreaMaxRect(Rect[] facesArray) {
        if (facesArray == null) {
            return null;
        }
        if (facesArray.length == 1) {
            return facesArray[0];
        }
        int index = 0;
        int areaResult = 0;
        for (int i = 0; i < facesArray.length; i++) {
            int faceArea = facesArray[i].width * facesArray[i].height;
            if (areaResult < faceArea) {
                areaResult = faceArea;
                index = i;
            }
        }
        return facesArray[index];
    }

    /**
     * 显示引导页
     */
    private void showIndicator() {
//        if (!SharePreferencer.instance().isShowFaceIndicator()) { // 没有显示过引导页则，显示
//            SharePreferencer.instance().setFaceIndicator(); //设置为已经显示过
//            guideView.setVisibility(View.VISIBLE);
//            guideView.bringToFront();
//            guideView.start();
//        }
    }

    /**
     * 加载图片
     */
    private void loadAlbum(final Uri data) {
        if (data == null) {
            return;
        }
        GlideUtil.displayImage(FaceDetectionActivity.this, photoView, data.toString(),
                new RequestListener() {
                    @Override
                    public boolean onException(Exception e, Object model, Target target,
                            boolean isFirstResource) {
                        photoView.setImageURI(data);
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Object resource, Object model, Target target,
                            boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                });
    }

    private void clickToChangeCamera() {
        if (surfaceView == null) {
            return;
        }
        if (takePhotoView.getVisibility() == View.VISIBLE) {
            takePhotoView.setClickable(true);
        }
        surfaceView.toggleCamera();
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    loadSuccess();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    private final static int WHAT_LOAD_SUC = 1;

    private final static int WHAT_SHOW_FAIL_DIALOG = 2;

    private final static int WHAT_CAMERA_START = 3;

    private final static int WHAT_CAMERA_END = 4;

    private final static int WHAT_FACEFRAMEGET = 5;

    private final static int WHAT_FACEFRAME_GONE = 6;

    private final static int WHAT_FACE_START = 7;

    private final static int WHAT_TOAST = 8;

    private final static int WHAT_BACK_TO_TAKE_PHOTO_VIEW = 9;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_LOAD_SUC:
                    if (surfaceView != null) {
                        surfaceView.enableView(true);
                    }
                    break;
                case WHAT_SHOW_FAIL_DIALOG:
                    detectTipView.setVisibility(View.GONE);
                    showSelectDialog();
                    break;
                case WHAT_CAMERA_START:
                    detectTipView.setVisibility(View.VISIBLE);
                    break;
                case WHAT_CAMERA_END:
                    detectTipView.setVisibility(View.GONE);
                    break;
                case WHAT_FACEFRAMEGET:
                    faceMainGet.setVisibility(
                            faceMainGet.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                    break;
                case WHAT_FACEFRAME_GONE:
                    faceMainGet.setVisibility(View.GONE);
                    break;
                case WHAT_FACE_START:
                    requestCamera();
                    break;
                case WHAT_TOAST:
                    toastManager.showToast((String) msg.obj);
                    break;
                case WHAT_BACK_TO_TAKE_PHOTO_VIEW:
                    if (surfaceView != null) {
                        surfaceView.enableView(false);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private File mCascadeFile;

    private CascadeClassifier mJavaDetector;

    private void loadSuccess() {

        ThreadPoolFactory.instance().fixExecutor(() -> {
            try {
                // load cascade file from application resources
                InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
                File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                mCascadeFile = new File(cascadeDir, "lbpcascade.xml");
                FileOutputStream os = new FileOutputStream(mCascadeFile);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                is.close();
                os.close();

                mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                mJavaDetector.load(mCascadeFile.getAbsolutePath());
                if (mJavaDetector.empty()) {
                    mJavaDetector = null;
                } else {
                }
                cascadeDir.delete();
                mHandler.sendEmptyMessage(WHAT_LOAD_SUC);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private float mRelativeFaceSize = 0.25f;

    private int mAbsoluteFaceSize = 0;

    private Mat mGray;

    private Mat mRgba;

    private final static int SCAN_MAX_TIME = 12 * 1000;

    private long startScanTime;

    private MatOfRect facesMatOfRect;

    @Override
    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
        isCall = new AtomicBoolean();
        facesMatOfRect = new MatOfRect();
        startScanTime = System.currentTimeMillis();
        LoggerUtils.i("onCameraViewStarted w:" + width + ", h :" + height);
    }

    @Override
    public void onCameraViewStopped() {
        if (mGray != null) {
            mGray.release();
        }
        if (mRgba != null) {
            mRgba.release();
        }
        if (facesMatOfRect != null) {
            facesMatOfRect.release();
        }
        mHandler.sendEmptyMessage(WHAT_CAMERA_END);
    }

    private final static long MIN_SHAKE = 2 * 1000;

    private final static long MIN_PRECAMERFRAMETIME = 200;

    private long preShake;

    private long preCamerFrameTime;

    private static AtomicBoolean isCall;

    @Override
    public Mat onCameraFrame(FaceSurfaceView.CvCameraViewFrame inputFrame) {

        if (currentMode != MODE_CAMERA_FACE || surfaceView == null || isCall.get()) {
            return null;
        }

        if (System.currentTimeMillis() - preCamerFrameTime < MIN_PRECAMERFRAMETIME) {
            return null;
        }
        preCamerFrameTime = System.currentTimeMillis();
        Mat graySrc = inputFrame.gray();
        if (surfaceView.getCameraOrientation() == 270) {
            Core.flip(graySrc.t(), mGray, -1);
        } else if (surfaceView.getCameraOrientation() == 180) {
            // Rotate clockwise 180 degrees
            Core.flip(graySrc, mGray, -1);
        } else if (surfaceView.getCameraOrientation() == 90) {
            // Rotate clockwise 90 degrees
            Core.flip(graySrc.t(), mGray, 1);
        } else if (surfaceView.getCameraOrientation() == 0) {
            // No rotation
            mGray = graySrc;
        }
        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
        }

        if (mJavaDetector != null) {
            mJavaDetector.detectMultiScale(mGray, facesMatOfRect, 1.1, 2, 2,
                    // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                    new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        }

        Rect maxRect = getFaceRect(facesMatOfRect);
        if (maxRect != null) {
            showGetFrame();
//            setFaceRect(maxRect);
            if (isFitFaceRect(maxRect)) {
                startScanTime = System.currentTimeMillis();
                if (timeIsFit()) {
                    isCall.set(true);
                    surfaceView.autoFocus((success, camera) -> {
                        final Bitmap bitmap = surfaceView.getPreBitmap();
                        String imgPath = FileUtils
                                .getImageCachePath(System.currentTimeMillis() + "");
                        ImageUtils.saveBitmapToFile(imgPath, bitmap);
                        android.graphics.Rect faceRect = new android.graphics.Rect(maxRect.x,
                                maxRect.y, maxRect.x + maxRect.width, maxRect.y + maxRect.height);
//                        FaceDetectionPreActivity
//                                .startActivitys(this, imgPath, actionName, faceRect);
                        surfaceView.disableView();
                    });
                }
            } else {
                endFaceDetectionIfTimeOver();
            }
        } else {
            hiddenGetFrame();
//            floatView.setRect(0, 0, 0, 0);
            endFaceDetectionIfTimeOver();
        }
        return mGray;
    }

    private void setFaceRect(Rect rect) {
        if (floatView.getVisibility() == View.INVISIBLE || floatView.getVisibility() == View.GONE) {
            floatView.setVisibility(View.VISIBLE);
        }
        float radioW = surfaceView.getRadioW();
        float radioH = surfaceView.getRadioH();
        floatView.setRect(rect.x * radioW, rect.y * radioH, radioW * (rect.x + rect.width),
                radioH * (rect.y + rect.height));
    }

    /**
     * 连续时间大于MIN_SHAKE 为true 表明可以捕获大于MIN_SHAKE。
     * 表明图像比较稳定
     */
    private boolean timeIsFit() {
        if (preShake == 0) {
            preShake = System.currentTimeMillis();
            return false;
        }
        if (System.currentTimeMillis() - preShake < MIN_SHAKE) {
            return false;
        } else {
            preShake = 0;
            return true;
        }
    }

    private long preGetFrame;

    private final static int PRE_GET_FRAME = 500;

    private void showGetFrame() {
        if (System.currentTimeMillis() - preGetFrame > PRE_GET_FRAME) {
            preGetFrame = System.currentTimeMillis();
            mHandler.sendEmptyMessage(WHAT_FACEFRAMEGET);
        }

    }

    private void hiddenGetFrame() {
        mHandler.sendEmptyMessage(WHAT_FACEFRAME_GONE);
    }

    private void endFaceDetectionIfTimeOver() {
        if (System.currentTimeMillis() - startScanTime > SCAN_MAX_TIME) {
            if (surfaceView != null) {
                surfaceView.disableView();
            }
            mHandler.sendEmptyMessage(WHAT_SHOW_FAIL_DIALOG);
        }
    }

//    private SilvrrDialog mSelectDialog;

    /**
     * 显示对话框
     */
    private void showSelectDialog() {
//        if (mSelectDialog != null && mSelectDialog.isShowing()) {
//            mSelectDialog.dismiss();
//            mSelectDialog = null;
//        }
//        mSelectDialog = new SilvrrDialog.Builder(this, SilvrrDialog.STYLE_MATERIAL)
//                .setTitle(R.string.face_detetion_fail_title)
//                .setContent(getString(R.string.face_detetion_fail_content))
//                .setCancelable(false)
//                .setPositiveButton(R.string.face_detetion_takephoto, silvrrDialog -> {
//                    silvrrDialog.dismiss();
//                    mSelectDialog = null;
//                    showTakePhotoMode();
//                })
//                .setNegativeButton(R.string.cancel, (silvrrDialog) -> {
//                    silvrrDialog.dismiss();
//                    surfaceView.enableView(true);
//                    startScanTime = System.currentTimeMillis();
//                    mSelectDialog = null;
//                }).build();
//        mSelectDialog.show();
    }

    //
    private void showBackToAblumDialog() {
//        if (mSelectDialog != null && mSelectDialog.isShowing()) {
//            mSelectDialog.dismiss();
//            mSelectDialog = null;
//        }
//        floatView.setVisibility(View.INVISIBLE);
//        mSelectDialog = new SilvrrDialog.Builder(this, SilvrrDialog.STYLE_MATERIAL)
//                .setTitle(R.string.face_detetion_fail_title_of_select_picture)
//                .setContent(getString(R.string.face_detetion_fail_content_of_select_picture))
//                .setCancelable(false)
//                .setPositiveButton(R.string.face_detetion_upLoadAgain_of_select_picture,
//                        silvrrDialog -> {
//                            silvrrDialog.dismiss();
//                            mSelectDialog = null;
//                            clickToPhoto();
//                        })
//                .setNegativeButton(R.string.face_detetion_cancel_of_select_picture,
//                        (silvrrDialog) -> {
//                            silvrrDialog.dismiss();
//                            mSelectDialog = null;
//                            if (surfaceView != null) {
//                                surfaceView.disableView();
//                            }
//                            FaceDetectionActivity.this.finish();
//                        }).build();
//        mSelectDialog.show();
    }

    public void showBackToTakePhotoDialog() {
//        if (mSelectDialog != null && mSelectDialog.isShowing()) {
//            mSelectDialog.dismiss();
//            mSelectDialog = null;
//        }
//        floatView.setVisibility(View.INVISIBLE);
//
//        mSelectDialog = new SilvrrDialog.Builder(this, SilvrrDialog.STYLE_MATERIAL)
//                .setTitle(R.string.face_detetion_fail_title)
//                .setContent(getString(R.string.face_detetion_fail_content))
//                .setCancelable(false)
//                .setPositiveButton(R.string.face_detetion_takephoto, silvrrDialog -> {
//                    silvrrDialog.dismiss();
//                    mSelectDialog = null;
//                    showTakePhotoMode();
//                })
//                .setNegativeButton(R.string.cancel, (silvrrDialog) -> {
//                    silvrrDialog.dismiss();
//                    if (surfaceView != null) {
//                        surfaceView.disableView();
//                    }
//                    mSelectDialog = null;
//                    FaceDetectionActivity.this.finish();
//                }).build();
//        mSelectDialog.show();
    }

    /**
     * 显示手动拍照模式
     */
    private void showTakePhotoMode() {
        currentMode = MODE_CAMERA_TAKE;
        takePhotoView.setVisibility(View.VISIBLE);
        takePhotoView.setClickable(true);
        if (surfaceView != null) {
            surfaceView.enableView(false);
        }
        faceTips.setVisibility(View.GONE);
        faceMain.setVisibility(View.GONE);
        faceMainGet.setVisibility(View.GONE);
    }

    private final static float FACE_TL = 75;

    private final static float FACE_WIDTH = 205;

    private final static float FACE_HEIGTH = 325;

    private int faceLeft = CommonUtils.dip2px(10);

    private int faceTop = CommonUtils.dip2px(FACE_TL);

    /**
     * 脸宽
     */
    private int faceWidth = CommonUtils.dip2px(FACE_WIDTH);

    /**
     * 脸高
     */
    private int faceHeight = CommonUtils.dip2px(FACE_HEIGTH);

    private final int FACE_TOP_SPAN = CommonUtils.dip2px(FACE_TL);

    /**
     * 是否找到合适位置的人脸
     */
    private boolean isFitFaceRect(Rect rect) {
        int viewWidth = surfaceView.getWidth();
        int viewHeight = surfaceView.getHeight();
        if (showOverRect(rect, viewWidth, viewHeight)) {
            return false;
        }
        int hSpan = (int) Math.abs(rect.y * surfaceView.getRadioH() - faceTop);
        if (hSpan > FACE_TOP_SPAN || hSpan < FACE_TOP_SPAN / 2) {
            return false;
        }
        if (minRect(rect, viewWidth, viewHeight) && maxRect(rect, viewWidth, viewHeight)) {
            return true;
        }
        return false;
    }

    private final static int SHOWOVER_TIME = 12 * 1000;

    private long preShowOverRect;

    /**
     * 显示超出区域提示
     */
    private boolean showOverRect(Rect rect, int viewWidth, int viewHeight) {
        float rw = surfaceView.getRadioW();
        float rh = surfaceView.getRadioH();
        float relLX = rect.x * rw;
        float relLY = rect.y * rh;
        float relRX = (rect.x + rect.width) * rw;
        float relW = rect.width * rw;
        boolean result = false;
        int msgId = 0;
        if (relW < viewWidth / 2) { // 提示靠近一些
            msgId = R.string.face_to_near;
            result = true;
        } else if (relW > viewWidth * 0.95) { // 提示拉远一些
            msgId = R.string.face_to_far;
            result = true;
        } else if (relLY > viewHeight / 3) { // 提示上移
            msgId = R.string.face_to_up;
            result = true;
        } else if (relLY < FACE_TOP_SPAN / 2) { // 提示下移
            msgId = R.string.face_to_down;
            result = true;
        } else if (relRX > viewWidth - faceLeft) { // 提示左移
            msgId = R.string.face_to_left;
            result = true;
        } else if (relLX < faceLeft) { // 提示右移
            msgId = R.string.face_to_right;
            result = true;
        }
        if (result) {
            if (System.currentTimeMillis() - preShowOverRect < SHOWOVER_TIME) {
                return result;
            }
            preShowOverRect = System.currentTimeMillis();
            Message msg = mHandler.obtainMessage();
            msg.what = WHAT_TOAST;
            msg.obj = getString(msgId);
            mHandler.sendMessage(msg);
        }
        return result;
    }

    private boolean maxRect(Rect rect, int viewWidth, int viewHeight) {
        return rect.width * surfaceView.getRadioW() < viewWidth * 0.9
                && rect.height * surfaceView.getRadioH() < viewHeight * 0.9;
    }

    private boolean minRect(Rect rect, int viewWidth, int viewHeight) {
        return rect.width * surfaceView.getRadioW() > viewWidth / 3
                && rect.height * surfaceView.getRadioH() > viewHeight / 3;
    }

    /**
     * 获取人脸识别的第一个区域
     */
    private Rect getFaceRect(MatOfRect faces) {
        Rect[] facesArray = faces.toArray();
        if (facesArray == null || facesArray.length == 0) {
            return null;
        }
        return facesArray[0];
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                );
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (currentMode == MODE_PHOTO) { // 如果是
            return;
        }
        if (currentMode == MODE_CAMERA_TAKE) {
            mHandler.sendEmptyMessage(WHAT_BACK_TO_TAKE_PHOTO_VIEW);
            return;
        }
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (surfaceView != null) {
            surfaceView.releasCamera();
        }
//        if (mSelectDialog != null && mSelectDialog.isShowing()) {
//            mSelectDialog.dismiss();
//            mSelectDialog = null;
//        }
        MyObservable.instance().deleteObserver(observer);
    }

    @Override
    protected int initLayoutId() {
        return 0;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initDataOnCreate() {

    }

    @Override
    protected void initDataOnStart() {

    }
}
