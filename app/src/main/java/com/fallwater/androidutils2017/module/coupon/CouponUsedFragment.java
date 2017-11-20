package com.fallwater.androidutils2017.module.coupon;

import com.fallwater.androidutils2017.R;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

/**
 * @author Fallwater潘建波 on 2017/11/20
 * @mail 1667376033@qq.com
 * 功能描述:
 */
public class CouponUsedFragment extends BaseCouponFragment {

    RecyclerView mCouponUsedRv;

    private CouponUsedAdapter mCouponUsedAdapter;

    @Override
    public int initLayout() {
        return R.layout.fragment_coupon;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCouponUsedRv = view.findViewById(R.id.coupon_rv);
        if (getCouponModel() == null) {
            return;
        }
        List<CouponInfo> couponInfoList = getCouponModel().getUsedCoupons();
        if(couponInfoList.size()==0){
            showNoDataView();
            return;
        }
        showDataView();
        mCouponUsedAdapter = new CouponUsedAdapter(0, couponInfoList);
        mCouponUsedRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCouponUsedRv.setAdapter(mCouponUsedAdapter);
    }

    @Override
    protected void onRefreshClick(View view) {

    }
}
