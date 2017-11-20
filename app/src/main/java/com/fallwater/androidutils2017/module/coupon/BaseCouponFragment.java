package com.fallwater.androidutils2017.module.coupon;

import com.fallwater.androidutils2017.R;
import com.fallwater.utilslibrary.common.AbsBaseFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * @author Fallwater潘建波 on 2017/11/20
 * @mail 1667376033@qq.com
 * 功能描述:
 */
public abstract class BaseCouponFragment extends AbsBaseFragment {

    LinearLayout mCouponNoresult;

    FrameLayout mFragmentContainer;

    @Override
    protected int initLayoutId() {
        return R.layout.fragment_coupon_base;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentContainer = view.findViewById(R.id.coupon_fragment_container);
        mCouponNoresult = view.findViewById(R.id.coupon_noresult);
        View fragmentView = LayoutInflater.from(getActivity())
                .inflate(initLayout(), mFragmentContainer, false);
        mFragmentContainer.addView(fragmentView);
    }

    public abstract int initLayout();

    protected void showDataView() {
        mFragmentContainer.setVisibility(View.VISIBLE);
        mCouponNoresult.setVisibility(View.GONE);
    }

    protected void showNoDataView() {
        mFragmentContainer.setVisibility(View.GONE);
        mCouponNoresult.setVisibility(View.VISIBLE);
    }

    protected CouponModel getCouponModel() {
        if (getActivity() != null && getActivity() instanceof CouponActivity) {
            return ((CouponActivity) getActivity()).getCouponModel();
        } else {
            return null;
        }
    }

    protected int getProductId() {
        if (getActivity() != null && getActivity() instanceof CouponActivity) {
            return ((CouponActivity) getActivity()).getProductId();
        } else {
            return -1;
        }
    }
}
