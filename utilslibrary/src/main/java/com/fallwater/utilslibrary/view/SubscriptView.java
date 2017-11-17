package com.fallwater.utilslibrary.view;

import com.fallwater.utilslibrary.R;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Fallwater潘建波 on 2017/11/17
 * @mail 1667376033@qq.com
 * 功能描述:优惠券角标view
 */

public class SubscriptView extends View {

    //角标
    private Paint mPaint;

    //字体默认大小
    private float mTextSize = 24.0f;

    Rect mRect;

    //默认
    private float mWidth = 50.0f;

    private String mTextTips = "Used";

    //高度
    private float mViewHeight;

    //文字距离左边
    private float mTextLeftOffset;

    //文字距离右边
    private float mTextRightOffset;

    private Context mContext;

    public SubscriptView(@NonNull Context context) {
        this(context, null);
    }

    public SubscriptView(@NonNull Context context,
            @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SubscriptView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        mContext = context;
        TypedArray a = context
                .obtainStyledAttributes(attrs, R.styleable.SubscriptView, defStyleAttr, 0);
        mTextTips = a.getString(R.styleable.SubscriptView_Subscript_text);
        mTextSize = a.getDimensionPixelSize(R.styleable.SubscriptView_Subscript_textsize, 24);
        a.recycle();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mRect = new Rect();

        mViewHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25.0f,
                Resources.getSystem().getDisplayMetrics());
        mTextLeftOffset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20.0f,
                Resources.getSystem().getDisplayMetrics());
        mTextRightOffset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10.0f,
                Resources.getSystem().getDisplayMetrics());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获取宽-测量规则的模式和大小
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        // 获取高-测量规则的模式和大小
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        // 设置wrap_content的默认宽 / 高值
        // 默认宽/高的设定并无固定依据,根据需要灵活设置
        // 类似TextView,ImageView等针对wrap_content均在onMeasure()对设置默认宽 / 高值有特殊处理,具体读者可以自行查看
        int mWidth = 400;
        int mHeight = 400;

        // 当布局参数设置为wrap_content时，设置默认值
        if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT
                && getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mWidth, mHeight);
            // 宽 / 高任意一个布局参数为= wrap_content时，都设置默认值
        } else if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mWidth, heightSize);
        } else if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(widthSize, mHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setTextSize(mTextSize);
        mPaint.getTextBounds(mTextTips, 0, mTextTips.length(), mRect);
        float width = mPaint.measureText(mTextTips);
        int height = mRect.height();
        float viewWidth = width + mTextLeftOffset + mTextRightOffset;//整个长度
        int realWidth = getWidth();
        int realHeight = getHeight();

        Path path = new Path();
        path.moveTo(realWidth - viewWidth, 0);
        path.lineTo(realWidth, 0);
        path.lineTo(realWidth, mViewHeight);
        path.lineTo(realWidth - viewWidth, mViewHeight);
        path.lineTo(realWidth - viewWidth + mTextRightOffset, mViewHeight / 2);
        path.close();
        mPaint.setColor(getResources().getColor(R.color.coupon_subscript_common));
        mPaint.setShadowLayer(1.0f, 2.0f, 2.0f,
                getResources().getColor(R.color.coupon_subscript_common_shadow));
        canvas.drawPath(path, mPaint);
        mPaint.setColor(getResources().getColor(R.color.coupon_subscript_text));
        mPaint.clearShadowLayer();
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float y = mViewHeight / 2 + (Math.abs(fontMetrics.ascent) - fontMetrics.descent) / 2;
        canvas.drawText(mTextTips, realWidth - viewWidth + mTextLeftOffset,
                y, mPaint);
    }

    public void setText(String text){
        mTextTips = text;
        invalidate();
    }

    public void setText(int resId){
        String text = getContext().getString(resId);
        setText(text);
    }
}
