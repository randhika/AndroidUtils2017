package com.fallwater.androidutils2017.module.coupon;

import com.fallwater.utilslibrary.common.AbsBaseActivity;
import com.fallwater.utilslibrary.rxjava.RequestSubscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Fallwater潘建波 on 2017/11/20
 * @mail 1667376033@qq.com
 * 功能描述:
 */
class CouponModel {

    private List<CouponInfo> mAvailableCoupons = new ArrayList<>();

    private List<CouponInfo> mUsedCoupons = new ArrayList<>();

    private List<CouponInfo> mExpiredCoupons = new ArrayList<>();

    public List<CouponInfo> getAvailableCoupons() {
        return mAvailableCoupons;
    }

    public void setAvailableCoupons(List<CouponInfo> availableCoupons) {
        mAvailableCoupons = availableCoupons;
    }

    public List<CouponInfo> getUsedCoupons() {
        return mUsedCoupons;
    }

    public void setUsedCoupons(List<CouponInfo> usedCoupons) {
        mUsedCoupons = usedCoupons;
    }

    public List<CouponInfo> getExpiredCoupons() {
        return mExpiredCoupons;
    }

    public void setExpiredCoupons(List<CouponInfo> expiredCoupons) {
        mExpiredCoupons = expiredCoupons;
    }

    public static void getCouponList(AbsBaseActivity activity, Map map,
            RequestSubscriber requestSubscriber) {
//        Observable observable = RxRetrofitClient.create(ApiService.class)
//                .getCouponList(map);
//        HttpManager.getInstance().requestSubscribe(activity, observable, requestSubscriber);
    }

}
