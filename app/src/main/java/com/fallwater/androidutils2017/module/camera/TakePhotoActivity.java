package com.fallwater.androidutils2017.module.camera;

import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fallwater.androidutils2017.R;
import com.fallwater.utilslibrary.common.BaseActivity;
import com.fallwater.utilslibrary.constant.Const;
import com.fallwater.utilslibrary.utils.FileUtils;
import com.fallwater.utilslibrary.utils.GlideUtil;
import com.fallwater.utilslibrary.utils.ImageUtils;
import com.fallwater.utilslibrary.utils.PermissionChecker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;
import android.widget.Toast;

import uk.co.senab.photoview.PhotoView;

/**
 * @author fallwater on 2017/11/11
 * @mail 1667376033@qq.com
 * 功能描述:拍照
 */
public class TakePhotoActivity extends BaseActivity implements View.OnClickListener {

    private final static int REQUEST_CODE_ALBUM = 1000;

    private static int REQUEST_FILESYSTEM_PERMISSION = 2001;

    private static int REQUEST_CAMERA_PERMISSION = 2002;

    /**
     * 用于标记那个界面跳转过来的
     */
    private final static String EXTRA_ACTIONNAME = "action_name";

    private final static String TAG = "FaceDetectionActivity";

    private long mPreTakePhotoClicktime;

    private TextView albumTv;

    private TextView batalTv;

    private PhotoView photoView;

    private ViewStub surfaceViewStub;

    private FaceSurfaceView surfaceView;

    private TextView faceTips;

    private View faceMain;

    private View takePhotoView;

    private String actionName;

    static {
        try {
            System.loadLibrary("opencv_java3");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected int initLayoutId() {
        return R.layout.activity_takephoto_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setImmersiveStatusBar(true, ContextCompat.getColor(this, android.R.color.transparent));
        actionName = getIntent().getStringExtra(EXTRA_ACTIONNAME);
        albumTv = (TextView) findViewById(R.id.detection_album);
        albumTv.setOnClickListener(this);
        batalTv = (TextView) findViewById(R.id.detection_batal);
        batalTv.setOnClickListener(this);
        photoView = (PhotoView) findViewById(R.id.face_photo);
        faceTips = (TextView) findViewById(R.id.face_tips);
        faceTips.setText(getString(R.string.take_photo_tips_focus));
        faceMain = findViewById(R.id.face_position_main);
        surfaceViewStub = (ViewStub) findViewById(R.id.face_camera);
        takePhotoView = findViewById(R.id.detection_take_photo);
        takePhotoView.setVisibility(View.VISIBLE);
        takePhotoView.setOnClickListener(this);

        mHandler.sendEmptyMessage(WHAT_REQUEST_CAMERA);
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

    private void requestCamera() {
        if (PermissionChecker.checkPermission(this, Manifest.permission.CAMERA)) {
            initSurfaceView();
        } else {
            String[] INITIAL_PERMS = {Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(this, INITIAL_PERMS, REQUEST_CAMERA_PERMISSION);
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
            surfaceView.setOnClickListener(this);
        }
    }

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
    private int currentMode = MODE_CAMERA_TAKE;

    /**
     * 推动相片模式
     */
    private void toPhotoMode() {
        currentMode = MODE_PHOTO;
        if (surfaceView != null) {
            surfaceView.setVisibility(View.GONE);
            surfaceView.disableView();
        }
        takePhotoView.setVisibility(View.GONE);
        faceMain.setVisibility(View.VISIBLE);
        photoView.setVisibility(View.VISIBLE);
        albumTv.setVisibility(View.VISIBLE);
        albumTv.setText(getString(R.string.face_submit));
        faceTips.setText(getString(R.string.take_photo_drag_move));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.detection_album:
                if (currentMode != MODE_PHOTO) { //当前是相机模式，点击跳转到相册选择
                    clickToAlbum();
                } else { // 当前是相片模式，点击跳转到提交
                    clickToSubmit();
                }
                break;
            case R.id.detection_batal: // 取消界面
                if (currentMode == MODE_CAMERA_TAKE) {
                    surfaceView.disableView();
                    finish();
                } else {
                    toTakePhotoMode();
                }
                break;
            case R.id.face_camera_surfaceview:
                if (surfaceView != null) {
                    surfaceView.safeAutoFocus(null);
                }
                break;
            case R.id.detection_take_photo:
                if (System.currentTimeMillis() - mPreTakePhotoClicktime < 1000) {
                    mPreTakePhotoClicktime = System.currentTimeMillis();
                    return;
                }
                mPreTakePhotoClicktime = System.currentTimeMillis();
                clickToTakePhoto();
                break;
            default:
                break;
        }
    }


    /**
     * 点击拍照
     */
    private void clickToTakePhoto() {
        if (surfaceView == null) {
            return;
        }
        if (surfaceView.isSupportAutoFocus()) {//如果支持自动对焦，则对焦后再取照片
            surfaceView.setClickable(false);
            takePhotoView.setClickable(false);
            surfaceView.autoFocus((success, camera) -> {
                final Bitmap bitmap = surfaceView.getPreBitmap();
                runOnUIThreadDelayed(bitmap, 200);
            });
        } else {//如果相机不支持自动对焦，直接取照片
            final Bitmap bitmap = surfaceView.getPreBitmap();
            runOnUIThreadDelayed(bitmap, 200);
        }
    }

    private void runOnUIThreadDelayed(Bitmap bitmap, int time) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                photoView.setImageBitmap(bitmap);
                toPhotoMode();
            }
        }, time);
    }


    private void clickToSubmit() {
        photoView.setDrawingCacheEnabled(true);
        final Bitmap bitmap = Bitmap.createBitmap(photoView.getDrawingCache());
        photoView.setDrawingCacheEnabled(false);
        saveAndExit(ImageUtils.rotateBitmapByDegree(bitmap, -90));
        bitmap.recycle();
    }

    private void saveAndExit(final Bitmap bitmap) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                String path = FileUtils.getImageCachePath(System.currentTimeMillis() + ".jpg");
                boolean result = ImageUtils.saveBitmapToFile(path, bitmap);
                bitmap.recycle();
                if (result) {
                }
                finish();
                return null;
            }

        }.execute();
    }


    /**
     * 点击去相册选择图片
     */
    private void clickToAlbum() {
        if (surfaceView != null) {
            surfaceView.disableView();
        }
        requestFileSystemPermission();
    }

    private void requestFileSystemPermission() {
        if (PermissionChecker.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            chooseExistingPhoto();
        } else {
            String[] INITIAL_PERMS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, INITIAL_PERMS, REQUEST_FILESYSTEM_PERMISSION);
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
                String receiptImageUri = data.getDataString();
            }
        }
    }

    /**
     * 加载图片
     */
    private void loadAlbum(final Uri data) {
        if (data == null) {
            return;
        }
        GlideUtil.displayImage(TakePhotoActivity.this, photoView, data.toString(),
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

    private final static int WHAT_SHOW_TAKE_PHOTO = 0;

    private final static int WHAT_REQUEST_CAMERA = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_REQUEST_CAMERA:
                    requestCamera();
                    break;
                case WHAT_SHOW_TAKE_PHOTO:
                    toTakePhotoMode();
                    break;
                default:
                    break;
            }
        }
    };

    private void toTakePhotoMode() {
        currentMode = MODE_CAMERA_TAKE;
        if (surfaceView != null) {
            surfaceView.setVisibility(View.VISIBLE);
            surfaceView.enableView(false);//再这里内部开启preview,接着自动聚焦
            surfaceView.setClickable(true);
            /*if (surfaceView.isSupportAutoFocus()) {
                surfaceView.autoFocus(null);
            }*/

        }
        faceTips.setVisibility(View.VISIBLE);
        faceMain.setVisibility(View.VISIBLE);
        photoView.setVisibility(View.GONE);
        if (takePhotoView.getVisibility() == View.INVISIBLE ||
                takePhotoView.getVisibility() == View.GONE) {
            takePhotoView.setVisibility(View.VISIBLE);
        }
        if (!takePhotoView.isClickable()) {
            takePhotoView.setClickable(true);
        }
        albumTv.setText("Album");
        faceTips.setText(getString(R.string.take_photo_tips_focus));
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
        if (currentMode == MODE_CAMERA_TAKE) {
            if (surfaceView != null && surfaceView.getVisibility() == View.VISIBLE) {
                surfaceView.disableView();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (currentMode == MODE_PHOTO) { // 如果是
            return;
        }
        if (currentMode == MODE_CAMERA_TAKE) {
            mHandler.sendEmptyMessageDelayed(WHAT_SHOW_TAKE_PHOTO, 100);
            return;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (surfaceView != null) {
            surfaceView.releasCamera();
        }
    }

    @Override
    public void onBackPressed() {
        if (currentMode == MODE_PHOTO) {
            toTakePhotoMode();
            return;
        } else if (currentMode == MODE_CAMERA_TAKE) {
            if (surfaceView != null) {
                surfaceView.disableView();
            }
            finish();
            return;
        }
        super.onBackPressed();
    }

    public static void startActivityForResult(Activity activity, String actionName,
            int requestCode) {
        Intent intent = new Intent(activity, TakePhotoActivity.class);
        intent.putExtra(EXTRA_ACTIONNAME, actionName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void startActivity(Activity activity, String actionName) {
        Intent intent = new Intent(activity, TakePhotoActivity.class);
        intent.putExtra(EXTRA_ACTIONNAME, actionName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

}
