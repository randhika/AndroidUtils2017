package com.fallwater.androidutils2017.module.coupon;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fallwater.androidutils2017.R;
import com.fallwater.utilslibrary.utils.FontUtil;
import com.fallwater.utilslibrary.view.SubscriptView;

import android.support.annotation.Nullable;

import java.util.List;

/**
 * @author Fallwater潘建波 on 2017/11/20
 * @mail 1667376033@qq.com
 * 功能描述:
 */
public abstract class BaseCouponAdapter extends BaseQuickAdapter<CouponInfo, BaseViewHolder> {

    public BaseCouponAdapter(int layoutResId,
            @Nullable List<CouponInfo> data) {
        super(R.layout.item_coupon_card, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CouponInfo item) {
        initContent(helper, item);
//        helper.setText(R.id.coupon_date,
//                String.format(mContext.getString(R.string.coupon_item_date),
//                        FormatUtils.formatDate3(item.deadline)));
    }

    private void initContent(BaseViewHolder helper, CouponInfo item) {
        setDescriptionText(helper, item.desc);
        switch (item.type) {
            case 1:
                initTypeCash(helper, item);
                helper.setText(R.id.coupon_type, "cash_coupon");
                break;
            case 2:
                initTypeInterest(helper, item);
                helper.setText(R.id.coupon_type, "interest_coupon");
                break;
            case 3:
                initTypeExpirence(helper, item);
                helper.setText(R.id.coupon_type, "experience_coupon");
                break;
            default:
                helper.setVisible(R.id.coupon_item_iv,
                        false);
                helper.setText(R.id.coupon_type, "");
                helper.setText(R.id.coupon_item_description, "");
                helper.setText(R.id.coupon_amount, "");
                helper.setText(R.id.coupon_item_description, "");
                break;
        }
    }

    private void setDescriptionText(BaseViewHolder helper, String value) {
        helper.setText(R.id.coupon_item_description, value);
    }

    public abstract void initTypeCash(BaseViewHolder helper, CouponInfo item);

    public abstract void initTypeInterest(BaseViewHolder helper, CouponInfo item);

    public abstract void initTypeExpirence(BaseViewHolder helper, CouponInfo item);

    protected void setDiscount(BaseViewHolder helper, int texColor, String value) {
        helper.setTypeface(R.id.coupon_amount, FontUtil.getDefaultTypeface(mContext));
        helper.setTextColor(R.id.coupon_amount, texColor);
        helper.setText(R.id.coupon_amount, value);
    }

    protected void setSubscriptText(BaseViewHolder helper, boolean visible, int resId) {
        if (visible) {
            helper.setVisible(R.id.coupon_subscript, true);
            SubscriptView view = helper.getView(R.id.coupon_subscript);
            view.setText(resId);
        } else {
            helper.setVisible(R.id.coupon_subscript, false);
        }
    }

    protected void setSubscriptText(BaseViewHolder helper, boolean visible, String subscriptValue) {
        if (visible) {
            helper.setVisible(R.id.coupon_subscript, true);
            SubscriptView view = helper.getView(R.id.coupon_subscript);
            view.setText(subscriptValue);
        } else {
            helper.setVisible(R.id.coupon_subscript, false);
        }
    }


    @Deprecated
    private void initDescription(BaseViewHolder helper, CouponInfo item) {
        String value = "";
        switch (item.descType) {
            case 1:
            case 5:
//                value = String.format(mContext.getString(R.string.coupon_cash_interest_tips),
//                        item.minPeriod + "",
//                        FormatUtils.getCommodityMoneyString(item.triggerAmount));
                break;
            case 2:
            case 6:
//                value = String.format(mContext.getString(R.string.coupon_cash_interest_tips1),
//                        item.maxPeriod + "",
//                        FormatUtils.getCommodityMoneyString(item.triggerAmount));
                break;
            case 3:
            case 7:
//                value = String.format(mContext.getString(R.string.coupon_cash_interest_tips3),
//                        FormatUtils.getCommodityMoneyString(item.triggerAmount));
                break;
            case 4:
            case 8:
//                value = String.format(mContext.getString(R.string.coupon_cash_interest_tips2),
//                        item.minPeriod + "", item.maxPeriod + "",
//                        FormatUtils.getCommodityMoneyString(item.triggerAmount));
                break;
            case 9:
                //3,小于限制
//                value = String.format(mContext.getString(R.string.coupon_experience_tips),
//                        item.minPeriod + "");
                break;
            case 10:
                //3,大于限制
//                value = String.format(mContext.getString(R.string.coupon_experience_tips1),
//                        item.maxPeriod + "");
//                break;
//            case 11:
//                //无限制
//                value = mContext.getString(R.string.coupon_experience_tips3);
//                break;
//            case 12:
//                //小大都有
//                value = String.format(mContext.getString(R.string.coupon_experience_tips2),
//                        item.minPeriod + "", item.maxPeriod + "");
                break;
            default:
                break;
        }
        setDescriptionText(helper, value);
    }

    @Deprecated
    private void initDescriptionOld(BaseViewHolder helper, CouponInfo item) {
        String value = "";
//        if (item.minPeriod > 0 && item.maxPeriod > 0) {
//            //大于多少小于多少
//            if (item.type == 3) {
//                value = String.format(mContext.getString(R.string.coupon_experience_tips2),
//                        item.minPeriod + "", item.maxPeriod + "");
//            } else {
//                value = String.format(mContext.getString(R.string.coupon_cash_interest_tips2),
//                        item.minPeriod + "", item.maxPeriod + "",
//                        FormatUtils.getCommodityMoneyString(item.triggerAmount));
//            }
//        } else if (item.minPeriod > 0 && item.maxPeriod == 0) {
//            //小于多少
//            if (item.type == 3) {
//                value = String.format(mContext.getString(R.string.coupon_experience_tips),
//                        item.minPeriod + "");
//            } else {
//                value = String.format(mContext.getString(R.string.coupon_cash_interest_tips),
//                        item.minPeriod + "",
//                        FormatUtils.getCommodityMoneyString(item.triggerAmount));
//            }
//        } else if (item.maxPeriod > 0 && item.minPeriod == 0) {
//            //大于多少
//            if (item.type == 3) {
//                value = String.format(mContext.getString(R.string.coupon_experience_tips1),
//                        item.maxPeriod + "");
//            } else {
//                value = String.format(mContext.getString(R.string.coupon_cash_interest_tips1),
//                        item.maxPeriod + "",
//                        FormatUtils.getCommodityMoneyString(item.triggerAmount));
//            }
//        } else {
//            //无限制
//        }
        setDescriptionText(helper, value);
    }
}
