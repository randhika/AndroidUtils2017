package com.fallwater.androidutils2017.module.coupon;

import com.androidkun.xtablayout.XTabLayout;
import com.fallwater.androidutils2017.R;
import com.fallwater.utilslibrary.common.AbsBaseActivity;
import com.fallwater.utilslibrary.rxjava.RequestSubscriber;
import com.fallwater.utilslibrary.view.NoticeWindow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * @author Fallwater潘建波 on 2017/11/20
 * @mail 1667376033@qq.com
 * 功能描述:
 */
public class CouponActivity extends AbsBaseActivity {

    @BindView(R.id.coupon_viewpager)
    ViewPager mCouponViewpager;

    @BindView(R.id.coupon_tablayout)
    XTabLayout mCouponTablayoutWithline;

    List<Fragment> mFragmentList = new ArrayList<>();

    @BindView(R.id.coupon_progress)
    ProgressBar mCouponProgress;

    private CouponViewPagerAdapter mCouponViewPagerAdapter;

    private CouponModel mCouponModel;

    /*
    * 购买流程
    **/
    private static final String COUPON_PRODUCT_ID = "product_id";

    public static final String COUPON_RESULT = "coupon_result";

    public static final int COUPON_REQUEST_CODE = 1;

    public static final int COUPON_RESULT_CODE = 2;

    private int mProductId = -1;

    @Override
    protected int initLayoutId() {
        return R.layout.activity_coupon;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setToolbarTitleNormal(R.string.coupon_title_coupons);
        mCouponModel = new CouponModel();
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initDataOnCreate() {
        mCouponProgress.setVisibility(View.VISIBLE);
        listCouponInfo();
    }

    @Override
    protected void initDataOnStart() {

    }

    private void listCouponInfo() {
        Map<String, Object> map = new HashMap<>();
        if (mProductId != -1) {
            map.put("productId", mProductId);
        }
        CouponModel.getCouponList(this, map, new RequestSubscriber<List<CouponInfo>>() {
            @Override
            protected void onStartC() {

            }

            @Override
            protected void onNextC(List<CouponInfo> couponInfos) {
                mCouponProgress.setVisibility(View.GONE);
                for (CouponInfo couponInfo : couponInfos) {
                    if (couponInfo.status == 1) {
                        mCouponModel.getAvailableCoupons().add(couponInfo);
                    } else if (couponInfo.status == 2) {
                        mCouponModel.getExpiredCoupons().add(couponInfo);
                    } else if (couponInfo.status == 3) {
                        mCouponModel.getUsedCoupons().add(couponInfo);
                    } else {

                    }
                }
                initFragment();
            }

            @Override
            protected void onCompletedC() {

            }

            @Override
            protected void onErrorC(String errorCode, String message,
                    List<CouponInfo> couponInfos) {
                mCouponProgress.setVisibility(View.GONE);
                NoticeWindow.showAlertMessage(CouponActivity.this, message);

            }
        });
    }

    public CouponModel getCouponModel() {
        return mCouponModel;
    }

    private void initFragment() {
        mFragmentList.add(new CouponAvailableFragment());
        mFragmentList.add(new CouponUsedFragment());
        mFragmentList.add(new CouponExpiredFragment());
        mCouponViewPagerAdapter = new CouponViewPagerAdapter(getSupportFragmentManager(),
                mFragmentList, mCouponModel);
        mCouponViewpager.setOffscreenPageLimit(3);
        mCouponViewpager.setAdapter(mCouponViewPagerAdapter);
        mCouponTablayoutWithline.setupWithViewPager(mCouponViewpager);
    }

    public int getProductId() {
        return mProductId;
    }


    public static void newInstance(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, CouponActivity.class);
        activity.startActivity(intent);
    }

    public static void lanchForResult(Fragment fragment, int productId) {
        Intent intent = new Intent();
        intent.setClass(fragment.getActivity(), CouponActivity.class);
        intent.putExtra(COUPON_PRODUCT_ID, productId);
        fragment.startActivityForResult(intent, COUPON_REQUEST_CODE);
    }
}
