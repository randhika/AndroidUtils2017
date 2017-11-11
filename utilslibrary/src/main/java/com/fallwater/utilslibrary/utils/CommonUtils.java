package com.fallwater.utilslibrary.utils;

import com.fallwater.utilslibrary.init.UtilLib;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CommonUtils {

    public static void setStatus(Activity activity, int colorRes) {
//        StatusBarCompat.setStatusBarColor(activity, android.R.color.transparent);
    }

    public static void setStatusTransparent(Activity activity) {
        setStatus(activity, android.R.color.transparent);
    }

    private static long lastClickTime;

    /**
     * 用于连续快速点击按钮，500毫秒内不能连续切换
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 300) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static boolean isEmail(String emailString) {
        String emailReg = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
        return CommonUtils.isMatchPattern(emailString, emailReg);
    }

    /**
     * 獲取應用當前版本，通過packagename獲取PackageInfo， packageinfo的versioncode<p>
     * 如果有PackageManager.NameNotFoundException，則返回-1
     *
     * @param context Application context
     * @return appVersion code
     */
    public static int getCurrentVersionCode(Context context) {
        try {
            // 获取packagemanager的实例
            PackageManager pm = context.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = pm.getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return -1;
        }
    }

    /**
     * 获取当前版本的名字
     *
     * @param context Application context
     * @return appVersion name
     */
    public static String getCurrentVersionName(Context context) {
        try {
            // 获取packagemanager的实例
            PackageManager pm = context.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = pm.getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    /**
     * 得到状态栏的高度
     *
     * @param context application context
     * @return 状态栏的高度
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources()
                .getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 显示键盘
     */
    public static void showKeyboard(Activity activity, View view) {
        if (null == activity) {
            return;
        }
        InputMethodManager imm = (InputMethodManager)
                activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * 隐藏输入法键盘
     *
     * @param activity host activity
     */
    public static void hideKeyboard(Activity activity) {
        if (null == activity) {
            return;
        }
        InputMethodManager imm = (InputMethodManager)
                activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null == imm || !imm.isActive()) {
            return;
        }
        View currentFocus = activity.getCurrentFocus();
        if (null != currentFocus) {
            imm.hideSoftInputFromWindow(currentFocus.getApplicationWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            currentFocus.clearFocus();
        }
    }

    /**
     * 清除当前占有焦点View的焦点
     */
    public static void clearCurrentFocus() {
    }

    public static void initAgreeStatementTextView(TextView textView) {
        if (textView == null) {
            return;
        }
//        textView.setText(Html.fromHtml(String.format(
////                textView.getContext().getString(R.string.agree_statement),
////                HttpReqUrl.TERM_OF_USE_URL
//        )));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }


    public static String getTrimmedText(TextView textView) {
        if (textView == null) {
            return null;
        }
        return textView.getText().toString().trim();
    }

    /**
     * 计算str的MD5值
     *
     * @param str string to calculate MD5
     * @return MD5 value of str or null
     */
    public static String getMD5(String str) {
        return encrypt(str, "MD5");
    }

    /**
     * 对字符串加密,加密算法使用MD5,SHA-1,SHA-256,默认使用SHA-256
     *
     * @param strSrc  要加密的字符串
     * @param encName 加密类型
     * @return 加盟之后的字符串
     */
    public static String encrypt(String strSrc, String encName) {
        MessageDigest md;
        String strDes;

        try {
            byte[] bt = strSrc.getBytes();
            if (encName == null || encName.equals("")) {
                encName = "SHA-256";
            }
            md = MessageDigest.getInstance(encName);
            md.update(bt);
            strDes = HexUtils.bytes2Hex(md.digest()); // to HexString
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        return strDes;
    }

    public static int dip2px(float dipValue) {
        final float scale = UtilLib.getsDefaultInstance().getContext().getResources()
                .getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int px2dip(float pxValue) {
        return px2dip(UtilLib.getsDefaultInstance().getContext(), pxValue);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    public static int sp2px(Context context, float spValue) {
        LoggerUtils.d("sp2px", "context = " + context);
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }


    public static void setMaxInputLength(TextView view, int maxInputLength) {
        if (view != null && maxInputLength > 0) {
            view.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxInputLength)});
        }
    }

    public static CharSequence removeDuplicateWhitespace(CharSequence inputStr) {
        String patternStr = "\\s+";
        String replaceStr = " ";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(inputStr);
        return matcher.replaceAll(replaceStr);
    }

    /**
     * 对密码的位数的规则
     */
    public static boolean isPasswordValid(String password) {
        return password.length() >= 8 && password.length() <= 16;
    }

    /**
     * 创建文件
     *
     * @param path 文件的存放地址
     */
    public static void makedir(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 传入文本看是否符合对应的正则规则
     *
     * @param text  传入文本
     * @param regex 正则表达式
     * @return 是否匹配规则
     */
    public static boolean isMatchPattern(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(text).matches();
    }

    /**
     * 判断包名对应的app是否安装
     *
     * @param context     上下文
     * @param packageName 包名
     * @return app是否已经安装
     */
    public static boolean isAppInstalled(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        try {
            Context outContext = context
                    .createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY);
            return outContext != null;
        } catch (PackageManager.NameNotFoundException e) {
            //do nothing
        }
        return false;
    }

    /**
     * 进入play store去下载包名对应的app
     *
     * @param packageName 包名
     * @param activity    当前的activity
     */
    public static void downloadApp(String packageName, Activity activity) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + packageName));
            intent.setPackage("com.android.vending");
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, "Please download the google play", Toast.LENGTH_LONG).show();
        }
    }

//    /**
//     * 更新View的高度
//     * @param view
//     * @param intrinsicWidth
//     * @param intrinsicHeight
//     */
//    public static void updateHeightOfView(View view, int intrinsicWidth, int intrinsicHeight) {
//        if (view != null && view.getLayoutParams() != null) {
//            int w = ScreenUtils.getScreenWidth(UtilLib.getsDefaultInstance().getContext());
//            float scale =  intrinsicWidth * 1.f / intrinsicHeight;
//            float h = w / scale;
//            view.getLayoutParams().height = (int)h;
//            view.setLayoutParams(view.getLayoutParams());
//        }
//    }
}
