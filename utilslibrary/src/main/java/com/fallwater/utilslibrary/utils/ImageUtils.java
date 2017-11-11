package com.fallwater.utilslibrary.utils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapEncoder;
import com.bumptech.glide.request.RequestListener;
import com.fallwater.utilslibrary.constant.Const;
import com.fallwater.utilslibrary.init.UtilLib;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageUtils {

    public static final int MAX_IMAGE_WIDTH = 512;

    public static final int MAX_IMAGE_HEIGHT = 512;

    public static final long MAX_IMAGE_FILE_SIZE = 2 * 1024 * 1024;

    private static final String TAG = "ImageUtils";

    private static final String JPEG_FILE_PREFIX = "IMG_";

    private static final String JPEG_FILE_SUFFIX = ".jpg";

    public static final String IMAGE_JPEG = "image/jpeg";// 图片类型

    public static final String IMAGE_PNG = "image/png";// 图片类型

    public static void displayImage(Fragment fragment, ImageView imageView, String url,
            int defaultImageId) {
        displayImage(fragment, imageView, url, defaultImageId, defaultImageId, null);
    }

    public static void displayImage(Fragment fragment, ImageView imageView, String url,
            int defaultImageId, int failImageId, RequestListener listener) {
        Glide.with(fragment)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .fitCenter()
                .placeholder(defaultImageId)
                .error(failImageId)
                .crossFade()
                .dontAnimate()
                .listener(listener)
                .into(imageView);
    }

    public static void displayImage(Context context, ImageView imageView, String url,
            int defaultImageId) {
        displayImage(context, imageView, url, defaultImageId, defaultImageId, null);
    }

    public static void displayImage(Context context, ImageView imageView, String url,
            int defaultImageId, int failImageId, RequestListener listener) {
//        Glide.with(context)
//                .load(url)
//                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                .fitCenter()
//                .placeholder(defaultImageId)
//                .error(failImageId)
//                .crossFade()
//                .dontAnimate()
//                .listener(listener)
//                .into(imageView);

        Glide.with(context)
                .load(url)
                .asBitmap()
                .encoder(new BitmapEncoder(Bitmap.CompressFormat.PNG, 100))
                .fitCenter()
                .placeholder(defaultImageId)
                .error(failImageId)
                .dontAnimate()
                .listener(listener)
                .into(imageView);
    }

    public static void displayImageCenterCrop(Context context, ImageView imageView, String url,
            int defaultImageId, int failImageId, RequestListener listener) {
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .centerCrop()
                .placeholder(defaultImageId)
                .error(failImageId)
                .crossFade()
                .dontAnimate()
                .listener(listener)
                .into(imageView);
    }

    public static void displayImage(Context context, ImageView imageView, byte[] imageBytes,
            int defaultImageId) {
        displayImage(context, imageView, imageBytes, defaultImageId, defaultImageId, null);
    }

    public static void displayImage(Context context, ImageView imageView, byte[] imageBytes,
            int defaultImageId, int failImageId, RequestListener listener) {
        Glide.with(context)
                .load(imageBytes)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .fitCenter()
                .placeholder(defaultImageId)
                .error(failImageId)
                .crossFade()
                .dontAnimate()
                .listener(listener)
                .into(imageView);
    }

    public static Bitmap decodeRGB565BitmapFromUri(Uri imageUri, int width, int height) {
        return decodeBitmapFromUri(imageUri, width, height, Bitmap.Config.RGB_565);
    }

    public static Bitmap decodeARGB8888BitmapFromUri(Uri imageUri, int width, int height) {
        return decodeBitmapFromUri(imageUri, width, height, Bitmap.Config.ARGB_8888);
    }

    public static Bitmap decodeBitmapFromUri(Uri imageUri, int width, int height,
            Bitmap.Config config) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            String scheme = imageUri.getScheme();
            Context context =UtilLib.getsDefaultInstance().getContext();
            if (ContentResolver.SCHEME_CONTENT.equals(scheme)
                    || ContentResolver.SCHEME_FILE.equals(scheme)) {
                ContentResolver resolver = context.getContentResolver();
                InputStream imageStream = resolver.openInputStream(imageUri);
                BitmapFactory.decodeStream(imageStream, null, options);
                imageStream.close();
            } else {
                BitmapFactory.decodeFile(imageUri.toString(), options);
            }
            if (width > 0 && height > 0) {
                if (options.outWidth > width || options.outHeight > height) {
                    options.inSampleSize = Math
                            .max(options.outWidth / width, options.outHeight / height);
                }
            }
            LoggerUtils.i(TAG, "options.inSampleSize is:" + options.inSampleSize);
            if (options.inSampleSize < 1) {
                options.inSampleSize = 1;
            }
            options.inPreferredConfig = config;
            options.inJustDecodeBounds = false;
            Bitmap bmp = null;
            if (ContentResolver.SCHEME_CONTENT.equals(scheme)
                    || ContentResolver.SCHEME_FILE.equals(scheme)) {
                ContentResolver resolver = context.getContentResolver();
                InputStream imageStream = resolver.openInputStream(imageUri);
                bmp = BitmapFactory.decodeStream(imageStream, null, options);
                if (imageStream != null) {
                    imageStream.close();
                }
            } else {
                bmp = BitmapFactory.decodeFile(imageUri.toString(), options);
            }
            return bmp;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static InputStream getInputStream4LiveChat(Context context, Uri imageUri) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            ContentResolver resolver = UtilLib.getsDefaultInstance().getContext().getContentResolver();
            InputStream imageStream = resolver.openInputStream(imageUri);
            BitmapFactory.decodeStream(imageStream, null, options);
            imageStream.close();
            options.inJustDecodeBounds = false;
            String outMimeType = options.outMimeType;
            Bitmap bmp = getBitmapARGB8888Rotation(context, imageUri);
            imageStream = getPhoto(bmp, outMimeType);
            return imageStream;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 任意Bitmap转ARGB888
     */
    public static Bitmap decodeARGB888BitmapFromBitmap(Bitmap bitmap) {
        return bitmap.copy(Bitmap.Config.ARGB_8888, false);
    }

    public static Bitmap decodeRGB565BitmapFromPath(String imagePath, int width, int height) {
        return decodeBitmapFromPath(imagePath, width, height, Bitmap.Config.RGB_565);
    }

    public static Bitmap decodeARGB888BitmapFromPath(String imagePath, int width, int height) {
        return decodeBitmapFromPath(imagePath, width, height, Bitmap.Config.ARGB_8888);
    }

    public static Bitmap decodeBitmapFromPath(String imagePath, int width, int height,
            Bitmap.Config config) {
        BitmapFactory.Options options = getImageInfo(imagePath);

        if (options.outWidth > width || options.outHeight > height) {
            options.inSampleSize = Math.max(options.outWidth / width,
                    options.outHeight / height);
        }
        if (options.inSampleSize < 1) {
            options.inSampleSize = 1;
        }
        options.inPreferredConfig = config;
        options.inJustDecodeBounds = false;
        return decodeBitmapFromPath(imagePath, options);
    }

    public static Bitmap decodeBitmapFromPath(String imagePath, BitmapFactory.Options options) {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
        return bitmap;
    }

    public static Bitmap decodeBitmapFromUri(Uri imageUri) {
        return decodeRGB565BitmapFromUri(imageUri, MAX_IMAGE_WIDTH, MAX_IMAGE_HEIGHT);
    }

    public static String getImagePath(Context context, Uri imageUri) {
        if (imageUri == null) {
            return null;
        }
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver()
                .query(imageUri, filePathColumn, null, null, null);
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndexOrThrow(filePathColumn[0]);
        String imagePath = cursor.getString(columnIndex);
        cursor.close();
        return imagePath;
    }

    public static BitmapFactory.Options getImageInfo(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        return options;
    }

    public static String scaleImageFile(String imagePath, int width, int height) {
        width = Math.min(width, MAX_IMAGE_WIDTH);
        height = Math.min(height, MAX_IMAGE_HEIGHT);

        Bitmap bitmap = decodeRGB565BitmapFromPath(imagePath, width, height);
        if (bitmap == null) {
            return null;
        }

        String destPath = saveBitmapToSDCard(imagePath, bitmap);
        bitmap.recycle();
        return destPath;
    }

    public static Bitmap getBitmapARGB8888Rotation(Context context, Uri imageUri) {
        String path = ImageUtils.getPath(context, imageUri);
        int degree = ImageUtils.getBitmapDegree(path);
        LoggerUtils.i(TAG, "mImagePath degree is:" + degree);
        Bitmap bitmap = ImageUtils.decodeARGB8888BitmapFromUri(imageUri, 0, 0);
        if (degree % 360 != 0) {
            bitmap = ImageUtils.rotateBitmapByDegree(bitmap, degree);
        }
        return bitmap;
    }

    public static Bitmap getBitmapAdjustRotation(Context context, Uri imageUri) {
        String path = ImageUtils.getPath(context, imageUri);
        int degree = ImageUtils.getBitmapDegree(path);
        Bitmap bitmap = ImageUtils.decodeBitmapFromUri(imageUri);
        if (degree % 360 != 0) {
            bitmap = ImageUtils.rotateBitmapByDegree(bitmap, degree);
        }
        return bitmap;
    }

    public static String saveBitmapToSDCard(String filename, Bitmap bitmap) {
        if (null == bitmap) {
            return null;
        }

        try {
            File file = new File(filename);
            File dir = file.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 保存图片到文件
     */
    public static boolean saveBitmapToFile(String filename, Bitmap bitmap) {
        if (null == bitmap || TextUtils.isEmpty(filename)) {
            return false;
        }

        try {
            File file = new File(filename);
            File dir = file.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static File createImageFile() throws IOException {
        // Create an image file name
        File albumF = getAlbumDir();
        if (albumF == null) {
            return null;
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp;
        return File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
    }

    private static File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = new File(Const.IMAGE_CACHE_PATH);
            if (!storageDir.exists()) {
                storageDir.mkdirs();
                if (!storageDir.exists()) {
                    LoggerUtils.d(TAG, "failed to create directory");
                    return null;
                }
            }
        } else {
            LoggerUtils.v(TAG, "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

//    public static void showImageFullScreenPreview(String imageUri, Activity activity) {
//        if (!imageUri.contains(":")) {
//            imageUri = "file://" + imageUri;
//        }
//        Intent intent = new Intent(activity, FullScreenImgPreviewActivity.class);
//        intent.putExtra(FullScreenImgPreviewActivity.IMAGE_URI, imageUri);
//        activity.startActivity(intent);
//    }

    /**
     * 将图片压缩到jpg
     *
     * @param finalWh 图片压缩之后的宽高。
     */
    public static InputStream getPhotoJPEG(String imagePath, int[] finalWh) {
        Uri imageUri = Uri.parse(imagePath);
        LoggerUtils.d(TAG, "getPhoto.imageUri.path=" + imageUri.getPath());
        Bitmap bmp = ImageUtils.decodeRGB565BitmapFromUri(imageUri,
                ImageUtils.MAX_IMAGE_WIDTH, ImageUtils.MAX_IMAGE_HEIGHT);
        if (bmp != null && finalWh != null) {
            finalWh[0] = bmp.getWidth();
            finalWh[1] = bmp.getHeight();
        }
        return getPhotoJPEG(bmp);
    }

    public static InputStream getPhotoJPEG(String imagePath) {
        Uri imageUri = Uri.parse(imagePath);
        LoggerUtils.d(TAG, "getPhoto.imageUri.path=" + imageUri.getPath());
        Bitmap bmp = ImageUtils.decodeRGB565BitmapFromUri(imageUri,
                ImageUtils.MAX_IMAGE_WIDTH, ImageUtils.MAX_IMAGE_HEIGHT);

        return getPhotoJPEG(bmp);
    }

    public static InputStream getPhoto(Bitmap bitmap, String imageType) {
        if (bitmap == null) {
            return null;
        }
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        if (IMAGE_JPEG.equals(imageType)) {
            format = Bitmap.CompressFormat.JPEG;
        } else if (IMAGE_PNG.equals(imageType)) {
            format = Bitmap.CompressFormat.PNG;
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(format, 100, os);
        bitmap.recycle();
        return new ByteArrayInputStream(os.toByteArray());
    }

    public static InputStream getPhotoJPEG(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
        bitmap.recycle();
        return new ByteArrayInputStream(os.toByteArray());
    }

    public static InputStream getPhotoPNG(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        bitmap.recycle();
        return new ByteArrayInputStream(os.toByteArray());
    }

    /**
     * 读取图片的旋转的角度
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    public static int getBitmapDegree(String path) {
        if (TextUtils.isEmpty(path)) {
            return 0;
        }

        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 将图片按照某个角度进行旋转
     *
     * @param bm     需要旋转的图片
     * @param degree 旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        if (bm == null || bm.isRecycled()) {
            return null;
        }

        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

//    public static void showSelectedImage(String imagePath, SimpleDraweeView preview) {
//        if (!imagePath.contains(":")) {
//            imagePath = "file://" + imagePath;
//        }
//        if (preview != null) {
//            preview.setImageURI(imagePath);
//        }
//
//    }

    public static Drawable bitmap2Drawable(Context context, Bitmap bitmap) {
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    /**
     * 图片的高斯模糊
     *
     * @param radius 0<radius<=25
     */

    public static Bitmap blurBitmap(Context context, Bitmap bitmap, float radius) {

        //Let's create an empty bitmap with the same size of the bitmap we want to blur
        Bitmap outBitmap = Bitmap
                .createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            //Instantiate a new Renderscript
            RenderScript rs = RenderScript.create(context.getApplicationContext());
            //Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
            Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
            Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                //Create an Intrinsic Blur Script using the Renderscript
                ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
                //Set the radius of the blur
                blurScript.setRadius(radius);
                //Perform the Renderscript
                blurScript.setInput(allIn);
                blurScript.forEach(allOut);
            }
            //Copy the final bitmap created by the out Allocation to the outBitmap
            allOut.copyTo(outBitmap);
            //recycle the original bitmap
            bitmap.recycle();
            //After finishing everything, we destroy the Renderscript.
            rs.destroy();
        }

        return outBitmap;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
            String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        //canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;

    }

    /**
     * 截取当前rootView以下的屏幕
     *
     * @param rootView 需要截取的根View
     * @return 返回得到的Bitmap截屏
     */
    public static Bitmap getScreenShot(View rootView) {
        rootView.buildDrawingCache(true);
        Bitmap bitmap = rootView.getDrawingCache(true).copy(Bitmap.Config.ARGB_8888, false);
        rootView.destroyDrawingCache();
        return bitmap;
    }

    /**
     * 添加水印
     *
     * @param context      上下文
     * @param bitmap       原图
     * @param markBitmapId 水印图片
     * @return bitmap      打了水印的图
     */
    public static Bitmap createWatermark(Context context, Bitmap bitmap, int markBitmapId) {
        if (markBitmapId == 0) {
            return bitmap;
        }
        // 获取图片的宽高
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        // 创建一个和图片一样大的背景图
        Bitmap bmp = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        // 画背景图
        canvas.drawBitmap(bitmap, 0, 0, null);
        // 载入水印图片
        Bitmap markBitmap = BitmapFactory.decodeResource(context.getResources(), markBitmapId);
        // 画图
        canvas.drawBitmap(markBitmap, 0, 0, null);
        //保存所有元素
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return bmp;
    }

//    public static void showImageWithFresco(Uri uri, SimpleDraweeView draweeView,
//            ResizeOptions resizeOptions) {
//        ImageRequestBuilder requestBuilder = ImageRequestBuilder.newBuilderWithSource(uri);
//        if (resizeOptions != null) {
//            requestBuilder.setResizeOptions(resizeOptions);
//        }
//        ImageRequest imageRequest = requestBuilder.build();
//        PipelineDraweeController controller = (PipelineDraweeController) Fresco
//                .newDraweeControllerBuilder()
//                .setOldController(draweeView.getController())
//                .setImageRequest(imageRequest)
//                .build();
//        draweeView.setController(controller);
//    }
//
//    public static void showLocalImageWithFresco(Context context, SimpleDraweeView simpleDraweeView,
//            int id) {
//        Uri uri = Uri.parse("res://" +
//                context.getPackageName() +
//                "/" + id);
//        simpleDraweeView.setImageURI(uri);
//    }

//    public static void showVectorImage(Context context, SimpleDraweeView simpleDraweeView, int id) {
//        // android.support.v4.content.ContextCompat creates your vector drawable
//        Drawable placeholderDrawable = ContextCompat.getDrawable(context, id);
//        // Set the placeholder image to the placeholder vector drawable
//        simpleDraweeView.setImageDrawable(placeholderDrawable);
//    }

    /**
     * 系统照片
     * 通过图片的Uri获取图片路径
     */
    public static Drawable getImageDrawableFromUri(Uri mUri) {
        if (null == mUri) {
            return null;
        }
        String scheme = mUri.getScheme();
        Drawable d = null;
        if (ContentResolver.SCHEME_CONTENT.equals(scheme)
                || ContentResolver.SCHEME_FILE.equals(scheme)) {
            InputStream stream = null;
            try {
                stream = UtilLib.getsDefaultInstance().getContext().getContentResolver().openInputStream(mUri);
                d = Drawable.createFromStream(stream, null);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            d = Drawable.createFromPath(mUri.toString());
        }

        return d;
    }

    public static String transferToBase64(String imagePath) {
        String base64Image;
        if (TextUtils.isEmpty(imagePath)) {
            return null;
        }
        Uri imageUri = Uri.parse(imagePath);
        Bitmap bmp = ImageUtils.decodeRGB565BitmapFromUri(imageUri,
                ImageUtils.MAX_IMAGE_WIDTH, ImageUtils.MAX_IMAGE_HEIGHT);
        if (bmp == null) {
            return null;
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, os);
        byte[] imgBytes = os.toByteArray();
        base64Image = Base64.encodeToString(imgBytes, Base64.DEFAULT);
        bmp.recycle();
        return "data:image/jpeg;base64," + base64Image;
    }

}
