package com.fallwater.utilslibrary.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

/**
 * 弹窗式对话框：可以设置位置，弹出动画，设置dialog的内容
 * Created by my daling on 2016/4/12.
 */
public class PopupWindowDialog extends Dialog {
    public PopupWindowDialog(Context context) {
        super(context);
        initView();
    }

    public PopupWindowDialog(Context context, int themeResId) {
        super(context, themeResId);
        initView();
    }

    protected PopupWindowDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView();
    }

    private void initView() {
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        initBottom();
    }

    /**
     * 显示在顶部（左右无空隔）
     */
    public void initTop() {
        Window win = getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        win.setGravity(Gravity.TOP); // 此处可以设置dialog显示的位置
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setAttributes(lp);
    }

    /**
     * 显示在底部(左右无空隔)
     */
    public void initBottom() {
        Window win = getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        win.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setAttributes(lp);
    }

    /**
     * 显示在中间(左右上下有一定的空隔)
     */
    public void initCenter() {
        Window win = getWindow();
        win.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setAttributes(lp);
    }

    /**
     * 显示在中间(左右上下有一定的空隔)
     */
    public void initCenterWithPadding() {
        Window win = getWindow();
        win.getDecorView().setPadding(32, 32, 32, 32);
        win.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setAttributes(lp);
    }

    public void setAnimations(@StyleRes int resId) {
        Window win = getWindow();
        win.setWindowAnimations(resId);
    }
}
