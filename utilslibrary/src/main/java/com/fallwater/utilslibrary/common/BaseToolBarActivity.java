package com.fallwater.utilslibrary.common;

import com.fallwater.utilslibrary.utils.OsUtil;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * Created by
 *
 * @author fallwater on 2017/11/01.
 * 功能描述:BaseToolBarActivity
 */
public abstract class BaseToolBarActivity extends AppCompatActivity {

    private ToolBarWrapper mToolBarWrapper;

    private Toolbar mToolbar;

    protected Activity mActivity;

    private View mViewStatusBarPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;

        findViewById(android.R.id.content).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                v.requestFocus();
            }
        });

    }

    @Override
    public void setContentView(int layoutResID) {
        mToolBarWrapper = new ToolBarWrapper(this, layoutResID);
        mViewStatusBarPlace = mToolBarWrapper.getStatusBarPlaceView();
        ViewGroup.LayoutParams params = mViewStatusBarPlace.getLayoutParams();
        params.height = getStatusBarHeight();
        mViewStatusBarPlace.setLayoutParams(params);
        mToolbar = mToolBarWrapper.getToolBar();
        if (hasToolbar() && mToolbar != null) {
            super.setContentView(mToolBarWrapper.getContentView());
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            mToolbar.hideOverflowMenu();
        } else {
            super.setContentView(layoutResID);
        }
    }

    @Override
    public void setContentView(View view) {
        mToolBarWrapper = new ToolBarWrapper(this, view);
        mViewStatusBarPlace = mToolBarWrapper.getStatusBarPlaceView();
        ViewGroup.LayoutParams params = mViewStatusBarPlace.getLayoutParams();
        params.height = getStatusBarHeight();
        mViewStatusBarPlace.setLayoutParams(params);
        mToolbar = mToolBarWrapper.getToolBar();
        if (hasToolbar() && mToolbar != null) {
            super.setContentView(mToolBarWrapper.getContentView());
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            mToolbar.hideOverflowMenu();
        } else {
            super.setContentView(view);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                goBack();
                return true;
            default:
                break;
        }
        return false;
    }

    public void setStatusBarVisibility(int visible) {
        mViewStatusBarPlace.setVisibility(visible);
    }

    public void goBack() {
        hideKeyboard();
        FragmentManager manager = this.getSupportFragmentManager();
        if (!manager.popBackStackImmediate()) {
            this.finish();
        }
    }

    public void setToolbarBackground(Drawable drawable) {
        if (Build.VERSION.SDK_INT > 15) {
            mToolbar.setBackground(drawable);
        } else {
            mToolbar.setBackgroundDrawable(drawable);
        }
    }

    public void setToolbarBackgroundColor(int color) {
        mToolbar.setBackgroundColor(color);
    }

    public void setToolbarBackgroundResource(int resid) {
        mToolbar.setBackgroundResource(resid);
    }

    public void setToolbarTitleVisible(int visible) {
        mToolBarWrapper.getTitle().setVisibility(visible);
    }

    public void setToolbarTitleTextColor(int color) {
        mToolBarWrapper.getTitle().setTextColor(color);
    }

    public void setToolbarTitleTextColorRes(int colorRes) {
        mToolBarWrapper.getTitle().setTextColor(ContextCompat.getColor(this, colorRes));
    }

    public void setToolbarTitleTextSize(int size) {
        mToolBarWrapper.getTitle().setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    /**
     * @param isBold   粗体
     * @param isItalic 斜体
     */
    public void setToolbarTitle(String text, boolean isBold, boolean isItalic) {
        if (mToolbar != null) {
            if (TextUtils.isEmpty(text)) {
                mToolBarWrapper.getTitle().setText("");
                return;
            }
            SpannableString msp = new SpannableString(text);
            if (isBold && isItalic) {
                msp.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, text.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //粗斜体
            } else if (isBold && !isItalic) {
                msp.setSpan(new StyleSpan(Typeface.BOLD), 0, text.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //粗体
            } else if (!isBold && isItalic) {
                msp.setSpan(new StyleSpan(Typeface.ITALIC), 0, text.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //斜体
            } else {
                msp.setSpan(new StyleSpan(Typeface.NORMAL), 0, text.length() - 1,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //正常体
            }
            mToolBarWrapper.getTitle().setText(msp);
        }
    }

    /**
     * 设置title正常
     */
    public void setToolbarTitleNormal(int resId) {
        setToolbarTitleNormal(getString(resId));
    }

    public void setToolbarTitleNormal(String text) {
        setToolbarTitle(text, false, false);
    }

    /**
     * 设置title粗体
     */
    public void setToolbarTitleBold(int resId) {
        setToolbarTitleBold(getString(resId));
    }

    /**
     * 设置title粗体
     */
    public void setToolbarTitleBold(String text) {
        setToolbarTitle(text, true, false);
    }

    /**
     * 设置title粗斜体
     */
    public void setToolbarTitleBoldItalic(int resId) {
        setToolbarTitleBoldItalic(getString(resId));
    }

    /**
     * 设置title粗斜体
     */
    public void setToolbarTitleBoldItalic(String text) {
        setToolbarTitle(text, true, true);
    }

    /**
     * 设置左侧返回按钮的图标
     */
    public void setToolbarLeftDrawable(int resId) {
        if (mToolbar != null) {
            mToolbar.setNavigationIcon(resId);
        }
    }


    public void setBackClick(OnClickListener listener) {
        if (mToolbar != null) {
            mToolbar.setNavigationOnClickListener(listener);
        }
    }

    /**
     * 设置左侧返回按钮的图标
     */
    public void setToolbarLeftDrawable(Drawable drawable) {
        if (mToolbar != null) {
            mToolbar.setNavigationIcon(drawable);
        }
    }

    public void setToolbarMenuAndListener(int menuId, Toolbar.OnMenuItemClickListener listener) {
        setToolbarMenu(menuId);
        setToolbarRightMenuItemClick(listener);
    }

    public void setToolbarMenu(int menuId) {
        mToolbar.inflateMenu(menuId);
        mToolbar.showOverflowMenu();
    }

    public void setToolbarRightMenuItemClick(Toolbar.OnMenuItemClickListener listener) {
        if (mToolbar != null) {
            mToolbar.setOnMenuItemClickListener(listener);
        }
    }

    public void setToolbarGone() {
        if (mToolbar != null) {
            mToolbar.setVisibility(View.GONE);
        }
    }

    public void setToolbarVisible() {
        if (mToolbar != null) {
            mToolbar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 是否显示toolbar
     *
     * @return true显示, falase不显示
     */
    public boolean hasToolbar() {
        return true;
    }

    public boolean hasStartBarPlace() {
        return true;
    }


    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)
                this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null == imm || !imm.isActive()) {
            return;
        }
        View currentFocus = this.getCurrentFocus();
        if (null != currentFocus) {
            imm.hideSoftInputFromWindow(currentFocus.getApplicationWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            currentFocus.clearFocus();
        }
    }

    /**
     * 设置沉浸式状态栏
     *
     * @param fontIconDark 状态栏字体和图标颜色是否为深色
     */
    public void setImmersiveStatusBar(boolean fontIconDark, int statusBarPlaceColor) {
        setTranslucentStatus(statusBarPlaceColor);
        if (fontIconDark) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    || OsUtil.isMIUI()
                    || OsUtil.isFlyme()) {
                setStatusBarFontIconDark(true);
            } else {
                if (statusBarPlaceColor == Color.WHITE) {
//                    statusBarPlaceColor = 0xffcccccc;
                }
            }
        }
        setStatusBarPlaceColor(statusBarPlaceColor);
    }

    public void setImmersiveStatusBarWithoutFullScreen(boolean fontIconDark,
            int statusBarPlaceColor) {
        setTranslucentStatusWithoutFullScreen(statusBarPlaceColor);
        if (fontIconDark) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    || OsUtil.isMIUI()
                    || OsUtil.isFlyme()) {
                setStatusBarFontIconDark(true);
            } else {
                if (statusBarPlaceColor == Color.WHITE) {
//                    statusBarPlaceColor = 0xffcccccc;
                }
            }
        }
        setStatusBarPlaceColor(statusBarPlaceColor);
    }

    private void setStatusBarPlaceColor(int statusColor) {
        if (mToolBarWrapper.getStatusBarPlaceView() != null) {
            mToolBarWrapper.getStatusBarPlaceView().setBackgroundColor(statusColor);
        }
    }

    public int getStatusBarHeight() {
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    /**
     * 设置状态栏透明
     */
    private void setTranslucentStatus(int statusBarPlaceColor) {

        // 5.0以上系统状态栏透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(statusBarPlaceColor);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    private void setTranslucentStatusWithoutFullScreen(int statusBarPlaceColor) {

        // 5.0以上系统状态栏透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(statusBarPlaceColor);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 设置Android状态栏的字体颜色，状态栏为亮色的时候字体和图标是黑色，状态栏为暗色的时候字体和图标为白色
     *
     * @param dark 状态栏字体是否为深色
     */
    private void setStatusBarFontIconDark(boolean dark) {
        // 小米MIUI
        if (OsUtil.isMIUI()) {
            try {
                Window window = getWindow();
                Class clazz = getWindow().getClass();
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                int darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {    //状态栏亮色且黑色字体
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);
                } else {       //清除黑色字体
                    extraFlagField.invoke(window, 0, darkModeFlag);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 魅族FlymeUI
        if (OsUtil.isFlyme()) {
            try {
                Window window = getWindow();
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // android6.0+系统
        // 这个设置和在xml的style文件中用这个<item name="android:windowLightStatusBar">true</item>属性是一样的
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (dark) {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                int uiVisibility = getWindow().getDecorView().getSystemUiVisibility();
                getWindow().getDecorView()
                        .setSystemUiVisibility(uiVisibility);
            }
        }
    }
}
