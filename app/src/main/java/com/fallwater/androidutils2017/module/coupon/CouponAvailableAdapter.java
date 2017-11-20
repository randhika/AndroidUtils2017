package com.fallwater.androidutils2017.module.coupon;

import com.chad.library.adapter.base.BaseViewHolder;

import android.support.annotation.Nullable;

import java.util.List;

/**
 * @author Fallwater潘建波 on 2017/11/20
 * @mail 1667376033@qq.com
 * 功能描述:
 */
class CouponAvailableAdapter extends BaseCouponAdapter{

    public CouponAvailableAdapter(int layoutResId,
            @Nullable List<CouponInfo> data) {
        super(layoutResId, data);
    }

    @Override
    public void initTypeCash(BaseViewHolder helper, CouponInfo item) {

    }

    @Override
    public void initTypeInterest(BaseViewHolder helper, CouponInfo item) {

    }

    @Override
    public void initTypeExpirence(BaseViewHolder helper, CouponInfo item) {

    }
}
