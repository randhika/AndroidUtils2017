package com.fallwater.utilslibrary.common;

import com.fallwater.utilslibrary.R;
import com.fallwater.utilslibrary.view.LoadingView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by cy on 17-1-18.
 */

public abstract class BaseFragment extends Fragment {

    protected FrameLayout mAddFrameLayout;

    protected View mRootView;

    protected View mNoRecordView;

    protected View mNetworkErrorView;

    protected LoadingView mLoadingView;

    protected ImageView mErrorIconIV;

    protected TextView mErrorMsgTV;

    protected TextView mRefreshBT;

    protected ImageView mNoResultIconIV;

    protected TextView mNoResultMsgTV;

    private Unbinder mUnbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d("BaseFragment", "onCreateView = " + this);
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_base_container, null);
        mAddFrameLayout = (FrameLayout) mRootView.findViewById(R.id.addFrameLayout);
        mAddFrameLayout.addView(inflater.inflate(initLayoutId(), container, false));
        mUnbinder = ButterKnife.bind(this, mRootView);
        mNoRecordView = mRootView.findViewById(R.id.no_record_view);
        mNetworkErrorView = mRootView.findViewById(R.id.network_error_view);
        mLoadingView = (LoadingView) mRootView.findViewById(R.id.loading_view);

        mNoResultIconIV = (ImageView) mNoRecordView.findViewById(R.id.no_result_icon);
        mNoResultMsgTV = (TextView) mNoRecordView.findViewById(R.id.no_result_msg);

        mErrorIconIV = (ImageView) mNetworkErrorView.findViewById(R.id.error_icon);
        mErrorMsgTV = (TextView) mNetworkErrorView.findViewById(R.id.error_tips);
        mRefreshBT = (TextView) mNetworkErrorView.findViewById(R.id.refresh_again_btn);

        mRefreshBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRefreshClick(view);
            }
        });

        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }

        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initPresenter();
        initView(view, savedInstanceState);
        initData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

    /**
     * 实例化Presenter,仅为mvp模式服务，其他情况下完全可以不用实现。
     */
    protected void initPresenter() {

    }

    protected void showLoadingView() {
        mAddFrameLayout.setVisibility(View.GONE);
        mNoRecordView.setVisibility(View.GONE);
        mNetworkErrorView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.VISIBLE);
    }

    protected void showResultView() {
        mAddFrameLayout.setVisibility(View.VISIBLE);
        mNoRecordView.setVisibility(View.GONE);
        mNetworkErrorView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
    }

    protected void showNetworkErrorView() {
        mAddFrameLayout.setVisibility(View.GONE);
        mNoRecordView.setVisibility(View.GONE);
        mNetworkErrorView.setVisibility(View.VISIBLE);
        mLoadingView.setVisibility(View.GONE);
    }

    protected void showNoResultView() {
        mAddFrameLayout.setVisibility(View.GONE);
        mNoRecordView.setVisibility(View.VISIBLE);
        mNetworkErrorView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
    }

    protected void showCustomNoResultView(int drawableId, int msgStringId) {
        mAddFrameLayout.setVisibility(View.GONE);
        mNoRecordView.setVisibility(View.VISIBLE);
        mNetworkErrorView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        mNoResultIconIV.setImageResource(drawableId);
        mNoResultMsgTV.setText(msgStringId);
    }

    protected void showErrorView(int imageRes, int stringRes) {
        mAddFrameLayout.setVisibility(View.GONE);
        mNoRecordView.setVisibility(View.GONE);
        mNetworkErrorView.setVisibility(View.VISIBLE);
        mLoadingView.setVisibility(View.GONE);
        mErrorIconIV.setImageResource(imageRes);
        mErrorMsgTV.setText(stringRes);
    }

    protected void dismissErrorView() {
        mAddFrameLayout.setVisibility(View.VISIBLE);
        mNetworkErrorView.setVisibility(View.GONE);
        mNoRecordView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
    }

    /**
     * 在baseFragment中添加一个子view
     */
    protected abstract int initLayoutId();

    protected abstract void initView(View view, Bundle savedInstanceState);

    protected abstract void initData();

    /**
     * 点击刷新按钮
     */
    protected abstract void onRefreshClick(View view);

}
