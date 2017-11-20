package com.fallwater.androidutils2017.module.coupon;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Fallwater潘建波 on 2017/11/20
 * @mail 1667376033@qq.com
 * 功能描述:
 */
class CouponInfo implements Parcelable {

    public int type;

    public String desc;

    public int descType;

    public int status;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeString(this.desc);
        dest.writeInt(this.descType);
        dest.writeInt(this.status);
    }

    public CouponInfo() {
    }

    protected CouponInfo(Parcel in) {
        this.type = in.readInt();
        this.desc = in.readString();
        this.descType = in.readInt();
        this.status = in.readInt();
    }

    public static final Parcelable.Creator<CouponInfo> CREATOR
            = new Parcelable.Creator<CouponInfo>() {
        @Override
        public CouponInfo createFromParcel(Parcel source) {
            return new CouponInfo(source);
        }

        @Override
        public CouponInfo[] newArray(int size) {
            return new CouponInfo[size];
        }
    };
}
