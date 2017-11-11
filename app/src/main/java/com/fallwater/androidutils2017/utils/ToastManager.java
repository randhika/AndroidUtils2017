package com.fallwater.androidutils2017.utils;

import com.fallwater.androidutils2017.MyApplication;
import com.fallwater.androidutils2017.R;
import com.fallwater.utilslibrary.utils.ColorUtils;
import com.fallwater.utilslibrary.utils.CommonUtils;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ToastManager {

    public final static int SHOW_TIME_SPAN = 20;

    private Context mContext;

    private Toast mToast;

    public ToastManager(Context context) {
        mContext = context;
    }

    private long preTime;

    private int preId;

    /**
     * 显示toast的并考虑时间间隔，防止抖动
     */
    public void showToast(int id, long time) {
        if (id == preId && Math.abs(System.currentTimeMillis() - preTime) < time) {
            return;
        }
        preTime = System.currentTimeMillis();
        preId = id;
        showToast(id);
    }

    private int textColor = -1;

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public synchronized void showToastByDefaultTime(int id) {
        showToast(id, SHOW_TIME_SPAN);
    }

    public void showToast(int id) {
        showToast(MyApplication.getInstance().getString(id));
    }

    public void showToast(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
            ViewGroup rootView = (ViewGroup) mToast.getView();
            rootView.setVisibility(View.VISIBLE);
            setRootBackground(rootView);
            mToast.setGravity(Gravity.CENTER, 0, 0);
            setToastAnimation(mToast);
        } else {
            mToast.setDuration(Toast.LENGTH_SHORT);
            mToast.setText(text);
        }
        mToast.show();
    }

    public void cancel() {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
    }

    /**
     * 设置toast动画
     */
    public void setToastAnimation(Toast toast) {
        try {
            Class tc = toast.getClass();
            Field mTN = tc.getDeclaredField("mTN");
            mTN.setAccessible(true);
            Object mTNObject = mTN.get(toast);
            Class tnClass = mTNObject.getClass();
            Field mParams = tnClass.getDeclaredField("mParams");
            mParams.setAccessible(true);
            WindowManager.LayoutParams wlp = (WindowManager.LayoutParams) mParams.get(mTNObject);
            wlp.windowAnimations = getCustemerAnimation();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {

        }

    }

    public void setAnimation(Toast toast) {
        try {
            Class tc = toast.getClass();
            Method wMethod = tc.getMethod("getWindowParams");
            wMethod.setAccessible(true);
            WindowManager.LayoutParams wlp = (WindowManager.LayoutParams) wMethod.invoke(toast);
            wlp.windowAnimations = getCustemerAnimation();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {

        }
    }

    /**
     * 获取自定义动画
     */
    public int getCustemerAnimation() {
        return R.style.ToastAnim;
    }

    /**
     * 自定义Toast背景
     */
    public void setRootBackground(View rootView) {
        rootView.setBackgroundResource(R.drawable.toast_background);
        rootView.setPadding(CommonUtils.dip2px(15), 0, CommonUtils.dip2px(15), 0);
        TextView tv = (TextView) rootView.findViewById(android.R.id.message);
        if (tv != null) {
            if (tv.getLayoutParams() != null) {
//                tv.getLayoutParams().height = CommonUtils.dip2px(36); // 换行时会截掉文字
                tv.setPadding(tv.getPaddingLeft(), CommonUtils.dip2px(11), tv.getPaddingRight(),
                        CommonUtils.dip2px(11));
            }
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(13);
            if (textColor != -1) {
                tv.setTextColor(ColorUtils.getColor(textColor));
            } else {
                tv.setTextColor(ColorUtils.getColor(android.R.color.white));
            }
        }
    }
}
