package com.fallwater.androidutils2017.module.coupon;

import com.fallwater.androidutils2017.R;
import com.fallwater.utilslibrary.utils.StringUtil;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Fallwater潘建波 on 2017/11/20
 * @mail 1667376033@qq.com
 * 功能描述:
 */
public class CouponViewPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragments = new ArrayList<>();

    private CouponModel mCouponModel;

    public CouponViewPagerAdapter(FragmentManager fm,
            List<Fragment> fragmentList, CouponModel couponModel) {
        super(fm);
        mFragments = fragmentList;
        mCouponModel = couponModel;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return String.format(StringUtil.getString(R.string.coupon_title_available),
                    mCouponModel.getAvailableCoupons().size());
        } else if (position == 1) {
            return String.format(StringUtil.getString(R.string.coupon_title_used),
                    mCouponModel.getUsedCoupons().size());
        } else if (position == 2) {
            return String.format(StringUtil.getString(R.string.coupon_title_expired),
                    mCouponModel.getExpiredCoupons().size());
        } else {
            return "";
        }
    }
}
