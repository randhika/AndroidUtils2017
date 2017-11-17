package com.fallwater.utilslibrary.common;

import com.fallwater.utilslibrary.R;
import com.fallwater.utilslibrary.view.LoadingView;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Fallwater潘建波 on 2017/11/17
 * @mail 1667376033@qq.com
 * 功能描述:
 */
public abstract class AbsBaseEnhancedActivity extends AbsBaseActivity {

    protected FrameLayout mAddFrameLayout;

    protected View mNoRecordView;

    protected View mNetworkErrorView;

    protected LoadingView mLoadingView;

    protected ImageView mErrorIconIV;

    protected TextView mErrorMsgTV;

    protected TextView mRefreshBT;

    protected ImageView mNoResultIconIV;

    protected TextView mNoResultMsgTV;

    @Override
    protected void registerOnCreate() {
        LayoutInflater inflater = LayoutInflater.from(this);
        mAddFrameLayout = (FrameLayout) findViewById(R.id.addFrameLayout);
        mAddFrameLayout.addView(inflater.inflate(initContentLayoutId(), null, false));
        mNoRecordView = findViewById(R.id.no_record_view);
        mNetworkErrorView = findViewById(R.id.network_error_view);
        mLoadingView = (LoadingView) findViewById(R.id.loading_view);

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
        super.registerOnCreate();
    }

    @Override
    protected int initLayoutId() {
        return R.layout.fragment_base_container;
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
     * 把你的xml写在这里
     */
    protected abstract int initContentLayoutId();

    /**
     * 点击刷新按钮
     */
    protected abstract void onRefreshClick(View view);

}
