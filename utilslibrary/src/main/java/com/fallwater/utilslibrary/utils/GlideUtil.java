package com.fallwater.utilslibrary.utils;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.fallwater.utilslibrary.init.UtilLib;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

/**
 * @author fallwater on 2017/11/11
 * @mail 1667376033@qq.com
 * 功能描述:
 */
public class GlideUtil {

    /**
     * 获取图片的宽高信息
     */
    public void getBitmapFromUrl(Context context, String url, final int[] result) {
        final Bitmap bitmap;
        Glide.with(context)
                .load(url)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource,
                            GlideAnimation<? super Bitmap> glideAnimation) {

                        result[0] = resource.getWidth();
                        result[1] = resource.getHeight();
                    }
                });
    }

    /**
     * Glide 加载图片, 正在加载和加载失败，使用同一个替换图片
     */
    public static void displayImage(Activity activity, ImageView imageView, String imageUrl,
            int loadingImageId) {
        displayImage(activity, imageView, imageUrl, loadingImageId, loadingImageId, null);
    }

    /**
     * 加载图片，没有回调，加载中和加载失败图片相同
     */
    public static void displayImage(Fragment fragment, ImageView imageView, String imageUrl,
            int loadingImageId) {
        displayImageDef(fragment, imageUrl, loadingImageId, loadingImageId, null).into(imageView);
    }

    /**
     * 加载图片，有回调，加载中和加载失败图片相同
     */
    public static void displayImage(Fragment fragment, ImageView imageView, String imageUrl,
            int loadingImageId, RequestListener listener) {
        displayImageDef(fragment, imageUrl, loadingImageId, loadingImageId, listener)
                .into(imageView);
    }

    /**
     * 加载并旋转图片
     *
     * @param hightFormat true 表示高质量图片
     */
    public static void displayImageDegree(Context context, ImageView imageView, String imageUrl,
            int defaultImageId, boolean hightFormat) {
        if (!imageUrl.startsWith("http")) {
            int degree = ImageUtils.getBitmapDegree(
                    ImageUtils.getPath(UtilLib.getsDefaultInstance().getContext().getApplicationContext(),
                            Uri.parse(imageUrl)));
            if (hightFormat) {
                displayImageFmtDef(context, imageUrl, defaultImageId, defaultImageId, null)
                        .transform(new RotateTransformation(context, degree, imageUrl))
                        .into(imageView);
            } else {
                displayImageDef(context, imageUrl, defaultImageId, defaultImageId, null)
                        .transform(new RotateTransformation(context, degree, imageUrl))
                        .into(imageView);
            }
        } else {
            displayImageFmtDef(context, imageUrl, defaultImageId, defaultImageId, null)
                    .into(imageView);
        }
    }

    /**
     * 图片旋转类
     */
    public static class RotateTransformation extends BitmapTransformation {

        private float rotateRotationAngle = 0f;

        private String path;

        public RotateTransformation(Context context, float rotateRotationAngle, String path) {
            this(context, rotateRotationAngle);
            this.path = path;
        }

        public RotateTransformation(Context context, float rotateRotationAngle) {
            super(context);
            this.rotateRotationAngle = rotateRotationAngle;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth,
                int outHeight) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotateRotationAngle);
            toTransform.recycle();
            return Bitmap.createBitmap(toTransform, 0, 0, toTransform.getWidth(),
                    toTransform.getHeight(), matrix, true);
        }

        @Override
        public String getId() {
            return path + "_rotate" + rotateRotationAngle;
        }
    }

    //---------Target 回调 ------------

    /**
     * 加载图片，不需要加载中和加载失败图片
     */
    public static void displayImage(Context context, String imageUrl, SimpleTarget target) {
        displayImageBitmapDef(context, imageUrl, 0, 0).into(target);
    }

    public static void displayImage(Context context, String imageUrl, int defaultImageId,
            SimpleTarget target) {
        displayImageBitmapDef(context, imageUrl, defaultImageId, defaultImageId).into(target);
    }

    public static void displayImage(Context context, String imageUrl, int defaultImageId,
            int errorImageId, SimpleTarget target) {
        displayImageBitmapDef(context, imageUrl, defaultImageId, errorImageId).into(target);
    }
    //---------Target 回调 end ------------


    //---------没有监听方法----------------------------------
    public static void displayImage(Context context, ImageView imageView, String imageUrl) {
        displayImage(context, imageView, imageUrl, 0, 0, null);
    }

    public static void displayImage(Context context, ImageView imageView, String imageUrl,
            Drawable defaultImageId) {
        displayImageDef(context, imageUrl, defaultImageId, defaultImageId, null).into(imageView);
    }

    public static void displayImage(Context context, ImageView imageView, String imageUrl,
            int defaultImageId) {
        displayImage(context, imageView, imageUrl, defaultImageId, defaultImageId, null);
    }

    public static void displayImage(Context context, ImageView imageView, String imageUrl,
            int defaultImageId, int errorImageId) {
        displayImageDef(context, imageUrl, defaultImageId, errorImageId, null).into(imageView);
    }
    //---------没有监听方法  end -----------------------------

    //--------有监听方法----------------------------------

    /**
     * 设置 加载监听
     */
    public static void displayImage(Context context, ImageView imageView, String imageUrl,
            RequestListener listener) {
        displayImageDef(context, imageUrl, 0, 0, listener).into(imageView);
    }

    /**
     * 设置 默认图片 加载监听
     */
    public static void displayImage(Context context, ImageView imageView, String imageUrl,
            int defaultImageId, RequestListener listener) {
        displayImageDef(context, imageUrl, defaultImageId, defaultImageId, listener)
                .into(imageView);
    }

    /**
     * 设置 默认图片 加载失败图片 加载监听
     */
    public static void displayImage(Context context, ImageView imageView, String imageUrl,
            int loadingImageId, int errorImageId, RequestListener listener) {
        displayImageDef(context, imageUrl, loadingImageId, errorImageId, listener).into(imageView);
    }

    //--------有监听方法 end----------------------------------

    /**
     * 高质量图片加载
     */
    public static BitmapRequestBuilder<String, Bitmap> displayImageFmtDef(Object context,
            String imageUrl, int loadingImageId, int errorImageId, RequestListener listener) {
        return getRequestManager(context).load(imageUrl)
                .asBitmap()
                .format(DecodeFormat.PREFER_ARGB_8888)
                .placeholder(loadingImageId)
                .error(errorImageId)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .listener(listener);
    }

    /**
     * 加载bitmap 类型的默认方法
     */
    public static BitmapRequestBuilder<String, Bitmap> displayImageBitmapDef(Object context,
            String imageUrl, int loadingImageId, int errorImageId) {
        return getRequestManager(context).load(imageUrl)
                .asBitmap()
                .placeholder(loadingImageId)
                .error(errorImageId)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE);
    }

    /**
     * 图片加载的默认配置类
     */
    public static DrawableRequestBuilder displayImageDef(Object context, String imageUrl,
            Drawable loadingImageId, Drawable errorImageId, RequestListener listener) {
        return getRequestManager(context).load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(loadingImageId) // 正在加载图片
                .error(errorImageId) // 加载失败图片
                .crossFade()
                .dontAnimate()
                .listener(listener);
    }

    /**
     * 图片加载的默认配置类
     */
    public static DrawableRequestBuilder displayImageDef(Object context, String imageUrl,
            int loadingImageId, int errorImageId, RequestListener listener) {
        return getRequestManager(context).load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(loadingImageId) // 正在加载图片
                .error(errorImageId) // 加载失败图片
                .crossFade()
                .dontAnimate()
                .listener(listener);
    }

    /**
     * 获取合适的RequestManager
     */
    private static RequestManager getRequestManager(Object context) {
        RequestManager manager;
        if (context instanceof Fragment) {
            manager = Glide.with((Fragment) context);
        } else if (context instanceof android.app.Fragment) {
            manager = Glide.with((android.app.Fragment) context);
        } else if (context instanceof Activity) {
            manager = Glide.with((Activity) context);
        } else if (context instanceof Context) {
            manager = Glide.with((Context) context);
        } else {
            throw new NullPointerException("invalid context");
        }
        return manager;
    }

}
