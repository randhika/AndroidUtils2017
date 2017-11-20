package com.fallwater.androidutils2017.module.coupon;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.fallwater.androidutils2017.R;

import android.content.Intent;
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
public class CouponAvailableFragment extends BaseCouponFragment
        implements BaseQuickAdapter.OnItemClickListener {

    RecyclerView mCouponAvailableRecycleview;

    private CouponAvailableAdapter mCouponAvailableAdapter;

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
        mCouponAvailableRecycleview = view.findViewById(R.id.coupon_rv);
        if (getCouponModel() == null) {
            return;
        }
        List<CouponInfo> couponInfoList = getCouponModel().getAvailableCoupons();
        if (couponInfoList.size() == 0) {
            showNoDataView();
            return;
        }
        showDataView();
        mCouponAvailableAdapter = new CouponAvailableAdapter(0,
                couponInfoList);
        mCouponAvailableRecycleview.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCouponAvailableRecycleview.setAdapter(mCouponAvailableAdapter);
        mCouponAvailableAdapter.setOnItemClickListener(this);
    }

    @Override
    protected void onRefreshClick(View view) {

    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (getProductId() == -1) {
            return;
        }
        CouponInfo couponInfo = getCouponModel().getAvailableCoupons().get(position);
        Intent intent = new Intent();
        intent.putExtra(CouponActivity.COUPON_RESULT, couponInfo);
        getActivity().setResult(CouponActivity.COUPON_RESULT_CODE, intent);
        getActivity().finish();
    }
}
