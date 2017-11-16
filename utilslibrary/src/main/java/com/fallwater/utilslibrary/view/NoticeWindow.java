package com.fallwater.utilslibrary.view;

import com.fallwater.utilslibrary.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by cyp on 2016/1/26.
 */

public class NoticeWindow {

    private static Dialog progressWindow = null;

    private static int progressWindowReferredCount = 0;

    /**
     * 没有网络
     */
    public static void showNoNetworkMessage(Activity activity) {
        showAlertMessage(activity, R.string.networks_unavailable);
    }

    /**
     * 网络不好再请求
     */
    public static void showNetworkErrorMessage(Activity activity) {
        showAlertMessage(activity, R.string.home_net_work_try_again);
    }

    public static void showAlertMessage(Activity activity, int stringId) {
        showAlertMessage(activity, stringId, null);
    }

    public static void showAlertMessage(Activity activity, String message) {
        showAlertMessage(activity, message, null);
    }

    public static void showAlertMessage(Activity activity, int stringId, Runnable dismissCallback) {
        showMessage(activity, stringId, dismissCallback, R.mipmap.message_icon);
    }

    public static void showAlertMessage(Activity activity, String message,
            Runnable dismissCallback) {
        showMessage(activity, message, dismissCallback, R.mipmap.message_icon);
    }

    public static void showSuccessMessage(Activity activity, String message) {
        showSuccessMessage(activity, message, null);
    }

    public static void showSuccessMessage(Activity activity, String message,
            Runnable dismissCallback) {
        showMessage(activity, message, dismissCallback, R.mipmap.success_message_icon);
    }

    public static void showSuccessMessage(Activity activity, int stringId) {
        showSuccessMessage(activity, stringId, null);
    }

    public static void showSuccessMessage(Activity activity, int stringId,
            Runnable dismissCallback) {
        showMessage(activity, stringId, dismissCallback, R.mipmap.success_message_icon);
    }

    public static void showNormalMessage(Activity activity, int stringId) {
        showNormalMessage(activity, stringId, null);
    }

    public static void showNormalMessage(Activity activity, String message) {
        showNormalMessage(activity, message, null);
    }

    public static void showNormalMessage(Activity activity, int stringId,
            Runnable dismissCallback) {
        showMessage(activity, stringId, dismissCallback, 0);
    }

    public static void showNormalMessage(Activity activity, String message,
            Runnable dismissCallback) {
        showMessage(activity, message, dismissCallback, 0);
    }

    public static void showMessage(Activity activity, int stringId, Runnable dismissCallback,
            int iconResId) {
        if (stringId > 0 && activity != null) {
            String message = activity.getString(stringId);
            showMessage(activity, message, dismissCallback, iconResId);
        }
    }

    public static void showMessage(Activity activity, String message,
            final Runnable dismissCallback, int noticeIconResource) {
//        if (activity != ActivityManager.getInstance().getCurrentActivity()) {
//            return;
//        }
        dismissProgressWin();
        LayoutInflater inflater = LayoutInflater.from(activity);
        View rootView = inflater.inflate(R.layout.notice_window, null);
        TextView msgView = (TextView) rootView.findViewById(R.id.message);
        msgView.setText(message);
        if (noticeIconResource != 0) {
            msgView.setCompoundDrawablesWithIntrinsicBounds(0, noticeIconResource, 0, 0);
        }
        final Dialog dialog = new Dialog(activity, R.style.NoticeWindowStyle);
        dialog.setContentView(rootView);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (dismissCallback != null) {
                    dismissCallback.run();
                }
            }
        });
        dialog.show();

        int showTime = (int) (Math.min(((float) message.length()) * 0.06 + 0.5, 5.0) * 1000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog != null && dialog.isShowing()) {
                    try {
                        dialog.dismiss();
                    } catch (Exception e) {
                        //do nothing
                    }
                }
            }
        }, showTime);
    }

    public static void showProgressWindow(Activity activity) {
        showProgressWindow(activity, 0);
    }

    public static void showProgressWindow(Activity activity, int msgResId) {
        ++progressWindowReferredCount;
        if (progressWindow != null && progressWindow.isShowing()) {
            return;
        }

        if (activity == null || activity.isFinishing()) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
                && activity.isDestroyed()) {
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(activity);
        View contentView = inflater.inflate(R.layout.progress_window, null);
        if (msgResId != 0) {
            TextView msgView = (TextView) contentView.findViewById(R.id.msg);
            msgView.setText(msgResId);
            msgView.setVisibility(View.VISIBLE);
        }
        progressWindow = new Dialog(activity, R.style.NoticeWindowStyle);
        progressWindow.setContentView(contentView);
        progressWindow.setCancelable(false);
        progressWindow.setCanceledOnTouchOutside(false);
        progressWindow.show();
    }

    public static void dismissProgressWindow() {
        --progressWindowReferredCount;
        if (progressWindowReferredCount < 0) {
            progressWindowReferredCount = 0;
        }
        if (progressWindowReferredCount == 0) {
            dismissProgressWin();
        }
    }

    private static void dismissProgressWin() {
        if (progressWindow != null && progressWindow.isShowing()) {
            try {
                progressWindow.hide();
                progressWindow.dismiss();
                progressWindow = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        progressWindowReferredCount = 0;
    }


}
