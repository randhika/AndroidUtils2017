package com.fallwater.utilslibrary.common;

import com.fallwater.utilslibrary.R;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Created by
 *
 * @author fallwater on 2017/11/01.
 * 功能描述:ToolBarWrapper
 */
public class ToolBarWrapper {

    private Context mContext;

    private LinearLayout mContentView;

    private View mUserView;

    private Toolbar mToolBar;

    private TextView mTitle;

    private View mViewStatusBarPlace;

    private LayoutInflater mInflater;


    public ToolBarWrapper(Context context, int layoutId) {
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
        /*初始化整个内容*/
        initContentView();
        /*初始化用户定义的布局*/
        initUserView(layoutId);
        /*初始化toolbar*/
        initToolBar();
    }

    public ToolBarWrapper(Context context, View layoutView) {
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
        /*初始化整个内容*/
        initContentView();
        /*初始化用户定义的布局*/
        initUserView(layoutView);
        /*初始化toolbar*/
        initToolBar();
    }

    private void initContentView() {
        mContentView = (LinearLayout) mInflater.inflate(R.layout.activity_base_container, null);
        mViewStatusBarPlace = mContentView.findViewById(R.id.status_bar_place);
    }

    private void initUserView(int id) {
        mInflater
                .inflate(id, ((FrameLayout) mContentView.findViewById(R.id.content_view_container)),
                        true);
    }

    private void initUserView(View view) {
        ((FrameLayout) mContentView.findViewById(R.id.content_view_container)).addView(view);
    }

    private void initToolBar() {
        /*通过inflater获取toolbar的布局文件*/
        View toolbar = mInflater.inflate(R.layout.layout_toolbar, null);
        mToolBar = (Toolbar) toolbar.findViewById(R.id.id_tool_bar);
        mTitle = (TextView) toolbar.findViewById(R.id.title);
        ((FrameLayout) mContentView.findViewById(R.id.toolbar_container)).addView(toolbar);
    }

    public LinearLayout getContentView() {
        return mContentView;
    }

    public Toolbar getToolBar() {
        return mToolBar;
    }

    public TextView getTitle() {
        return mTitle;
    }

    public View getStatusBarPlaceView() {
        return mViewStatusBarPlace;
    }

}
