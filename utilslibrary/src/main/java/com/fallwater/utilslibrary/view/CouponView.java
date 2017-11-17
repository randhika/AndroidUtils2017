package com.fallwater.utilslibrary.view;

import com.fallwater.utilslibrary.R;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.FrameLayout;

/**
 * @author Fallwater潘建波 on 2017/11/17
 * @mail 1667376033@qq.com
 * 功能描述:
 */

public class CouponView extends FrameLayout {


    private static final int DEFAULT_SEMICIRCLE_GAP = 4;

    private static final int DEFAULT_DASH_LINE_LENGTH = 10;

    private static final int DEFAULT_SEMICIRCLE_RADIUS = 4;

    private static final int DEFAULT_SEMICIRCLE_RADIUS_NEW = 17;

    private static final int DEFAULT_SEMICIRCLE_COLOR = 0xFFFFFFFF;

    private static final int DEFAULT_DASH_LINE_HEIGHT = 1;

    private static final int DEFAULT_DASH_LINE_GAP = 5;

    private static final int DEFAULT_DASH_LINE_COLOR = 0xFFFFFFFF;

    private static final int DEFAULT_DASH_LINE_MARGIN = 10;

    private Context context;

    //半圆画笔
    private Paint semicirclePaint;

    //虚线画笔
    private Paint dashLinePaint;

    //半圆之间间距
    private float semicircleGap = DEFAULT_SEMICIRCLE_GAP;

    //半圆半径,上下
    private float semicircleRadius = DEFAULT_SEMICIRCLE_RADIUS;

    //两边圆的半径
    private float semicircleRadiusNew = DEFAULT_SEMICIRCLE_RADIUS;

    //半圆颜色
    private int semicircleColor = DEFAULT_SEMICIRCLE_COLOR;

    //半圆数量X
    private int semicircleNumX;

    //半圆数量Y
    private int semicircleNumY;

    //绘制半圆曲线后X轴剩余距离
    private int remindSemicircleX;

    //绘制半圆曲线后Y轴剩余距离
    private int remindSemicircleY;

    //虚线的长度
    private float dashLineLength = DEFAULT_DASH_LINE_LENGTH;

    //虚线的高度
    private float dashLineHeight = DEFAULT_DASH_LINE_HEIGHT;

    //虚线的间距
    private float dashLineGap = DEFAULT_DASH_LINE_GAP;

    //虚线的颜色
    private int dashLineColor = DEFAULT_DASH_LINE_COLOR;

    //绘制虚线后X轴剩余距离
    private int remindDashLineX;

    //绘制虚线后Y轴剩余距离
    private int remindDashLineY;

    //虚线数量X
    private int dashLineNumX;

    //半圆数量Y
    private int dashLineNumY;

    //开启顶部半圆曲线
    private boolean isSemicircleTop = false;

    //开启底部半圆曲线
    private boolean isSemicircleBottom = true;

    //开启左边半圆曲线
    private boolean isSemicircleLeft = true;

    //开启右边半圆曲线
    private boolean isSemicircleRight = true;

    //开启顶部虚线
    private boolean isDashLineTop = false;

    //开启底部虚线
    private boolean isDashLineBottom = false;

    //开启左边虚线
    private boolean isDashLineLeft = false;

    //开启右边虚线
    private boolean isDashLineRight = false;

    //view宽度
    private int viewWidth;

    //view的高度
    private int viewHeight;

    //顶部虚线距离View顶部的距离
    private float dashLineMarginTop = DEFAULT_DASH_LINE_MARGIN;

    //底部虚线距离View底部的距离
    private float dashLineMarginBottom = DEFAULT_DASH_LINE_MARGIN;

    //左侧虚线距离View左侧的距离
    private float dashLineMarginLeft = DEFAULT_DASH_LINE_MARGIN;

    //右侧虚线距离View右侧的距离
    private float dashLineMarginRight = DEFAULT_DASH_LINE_MARGIN;


    public CouponView(@NonNull Context context) {
        this(context, null);
    }

    public CouponView(@NonNull Context context,
            @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CouponView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        TypedArray a = context
                .obtainStyledAttributes(attrs, R.styleable.CouponView, defStyleAttr, 0);
        semicircleRadius = a.getDimensionPixelSize(R.styleable.CouponView_cv_semicircle_radius,
                dp2Px(DEFAULT_SEMICIRCLE_RADIUS));
        semicircleGap = a.getDimensionPixelSize(R.styleable.CouponView_cv_semicircle_gap,
                dp2Px(DEFAULT_SEMICIRCLE_GAP));
        semicircleColor = a
                .getColor(R.styleable.CouponView_cv_semicircle_color, DEFAULT_SEMICIRCLE_COLOR);

        dashLineGap = a.getDimensionPixelSize(R.styleable.CouponView_cv_dash_line_gap,
                dp2Px(DEFAULT_DASH_LINE_GAP));
        dashLineHeight = a.getDimensionPixelSize(R.styleable.CouponView_cv_dash_line_height,
                dp2Px(DEFAULT_DASH_LINE_HEIGHT));
        dashLineLength = a.getDimensionPixelSize(R.styleable.CouponView_cv_dash_line_length,
                dp2Px(DEFAULT_DASH_LINE_LENGTH));
        dashLineColor = a
                .getColor(R.styleable.CouponView_cv_dash_line_color, DEFAULT_DASH_LINE_COLOR);

        isSemicircleTop = a
                .getBoolean(R.styleable.CouponView_cv_is_semicircle_top, isSemicircleTop);
        isSemicircleBottom = a
                .getBoolean(R.styleable.CouponView_cv_is_semicircle_bottom, isSemicircleBottom);
        isSemicircleLeft = a
                .getBoolean(R.styleable.CouponView_cv_is_semicircle_left, isSemicircleLeft);
        isSemicircleRight = a
                .getBoolean(R.styleable.CouponView_cv_is_semicircle_right, isSemicircleRight);
        isDashLineTop = a.getBoolean(R.styleable.CouponView_cv_is_dash_line_top, isDashLineTop);
        isDashLineBottom = a
                .getBoolean(R.styleable.CouponView_cv_is_dash_line_bottom, isDashLineBottom);
        isDashLineLeft = a.getBoolean(R.styleable.CouponView_cv_is_dash_line_left, isDashLineLeft);
        isDashLineRight = a
                .getBoolean(R.styleable.CouponView_cv_is_dash_line_right, isDashLineRight);

        dashLineMarginTop = a.getDimensionPixelSize(R.styleable.CouponView_cv_dash_line_margin_top,
                dp2Px(DEFAULT_DASH_LINE_MARGIN));
        dashLineMarginBottom = a
                .getDimensionPixelSize(R.styleable.CouponView_cv_dash_line_margin_bottom,
                        dp2Px(DEFAULT_DASH_LINE_MARGIN));
        dashLineMarginLeft = a
                .getDimensionPixelSize(R.styleable.CouponView_cv_dash_line_margin_left,
                        dp2Px(DEFAULT_DASH_LINE_MARGIN));
        dashLineMarginRight = a
                .getDimensionPixelSize(R.styleable.CouponView_cv_dash_line_margin_right,
                        dp2Px(DEFAULT_DASH_LINE_MARGIN));

        a.recycle();
        init();

    }

    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);//没有这句不显示

        semicirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        semicirclePaint.setDither(true);
        semicirclePaint.setColor(semicircleColor);
        semicirclePaint.setStyle(Paint.Style.FILL);
        //semicirclePaint.setShadowLayer(1.0f,0.0f,0.0f, Color.GREEN);

        dashLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dashLinePaint.setDither(true);
        dashLinePaint.setColor(dashLineColor);
        dashLinePaint.setStyle(Paint.Style.FILL);
    }

    public void onSizeChanged(int w, int h) {
        viewWidth = w;
        viewHeight = h;
        calculate();
    }

    private void calculate() {
        if (isSemicircleTop || isSemicircleBottom) {
            remindSemicircleX = (int) ((viewWidth - semicircleGap) % (2 * semicircleRadius
                    + semicircleGap));
            semicircleNumX = (int) ((viewWidth - semicircleGap) / (2 * semicircleRadius
                    + semicircleGap));
        }

        if (isSemicircleLeft || isSemicircleRight) {
            /*remindSemicircleY = (int) ((viewHeight - semicircleGap) % (2 * semicircleRadius
                    + semicircleGap));
            semicircleNumY = (int) ((viewHeight - semicircleGap) / (2 * semicircleRadius
                    + semicircleGap));*/
            semicircleRadiusNew = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8.5f,
                    Resources.getSystem().getDisplayMetrics());//两边圆的半径，以后可以根据需求公开方法修改
            //根据项目需求作的改动
            remindSemicircleY = (int) (viewHeight - 2 * semicircleRadius);//设置两边到圆的距离
            semicircleNumY = 1;//两边圆的数量
        }

        if (isDashLineTop || isDashLineBottom) {
            remindDashLineX = (int) (
                    (viewWidth + dashLineGap - dashLineMarginLeft - dashLineMarginRight) % (
                            dashLineLength + dashLineGap));
            dashLineNumX = (int) (
                    (viewWidth + dashLineGap - dashLineMarginLeft - dashLineMarginRight) / (
                            dashLineLength + dashLineGap));
        }

        if (isDashLineLeft || isDashLineRight) {
            remindDashLineY = (int) (
                    (viewHeight + dashLineGap - dashLineMarginTop - dashLineMarginBottom) % (
                            dashLineLength + dashLineGap));
            dashLineNumY = (int) (
                    (viewHeight + dashLineGap - dashLineMarginTop - dashLineMarginBottom) / (
                            dashLineLength + dashLineGap));
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (isSemicircleTop) {
            for (int i = 0; i < semicircleNumX; i++) {
                float x = semicircleGap + semicircleRadius + remindSemicircleX / 2
                        + (semicircleGap + semicircleRadius * 2) * i;
                canvas.drawCircle(x, 0, semicircleRadius, semicirclePaint);

            }
        }
        if (isSemicircleBottom) {
            for (int i = 0; i < semicircleNumX; i++) {
                float x = semicircleGap + semicircleRadius + remindSemicircleX / 2
                        + (semicircleGap + semicircleRadius * 2) * i;
                canvas.drawCircle(x, viewHeight, semicircleRadius, semicirclePaint);
            }
        }
        if (isSemicircleLeft) {
            for (int i = 0; i < semicircleNumY; i++) {
                /*float y = semicircleGap + semicircleRadius + remindSemicircleY / 2
                        + (semicircleGap + semicircleRadius * 2) * i;*/
                float y1 = viewHeight / 2;
                canvas.drawCircle(0, y1, semicircleRadiusNew, semicirclePaint);
            }
        }
        if (isSemicircleRight) {
            for (int i = 0; i < semicircleNumY; i++) {
                /*float y = semicircleGap + semicircleRadius + remindSemicircleY / 2
                        + (semicircleGap + semicircleRadius * 2) * i;*/
                float y2 = viewHeight / 2;
                canvas.drawCircle(viewWidth, y2, semicircleRadiusNew, semicirclePaint);
            }
        }
        if (isDashLineTop) {
            for (int i = 0; i < dashLineNumX; i++) {
                float x = dashLineMarginLeft + remindDashLineX / 2
                        + (dashLineGap + dashLineLength) * i;
                canvas.drawRect(x, dashLineMarginTop, x + dashLineLength,
                        dashLineMarginTop + dashLineHeight, dashLinePaint);
            }
        }
        if (isDashLineBottom) {
            for (int i = 0; i < dashLineNumX; i++) {
                float x = dashLineMarginLeft + remindDashLineX / 2
                        + (dashLineGap + dashLineLength) * i;
                canvas.drawRect(x, viewHeight - dashLineHeight - dashLineMarginBottom,
                        x + dashLineLength, viewHeight - dashLineMarginBottom, dashLinePaint);
            }
        }
        if (isDashLineLeft) {
            for (int i = 0; i < dashLineNumY; i++) {
                float y = dashLineMarginTop + remindDashLineY / 2
                        + (dashLineGap + dashLineLength) * i;
                canvas.drawRect(dashLineMarginLeft, y, dashLineMarginLeft + dashLineHeight,
                        y + dashLineLength, dashLinePaint);
            }
        }
        if (isDashLineRight) {
            for (int i = 0; i < dashLineNumY; i++) {
                float y = dashLineMarginTop + remindDashLineY / 2
                        + (dashLineGap + dashLineLength) * i;
                canvas.drawRect(viewWidth - dashLineMarginRight - dashLineHeight, y,
                        viewWidth - dashLineMarginRight, y + dashLineLength, dashLinePaint);
            }
        }
    }

    private int dp2Px(float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    private int px2Dp(float px) {
        return (int) (px / context.getResources().getDisplayMetrics().density + 0.5f);
    }

    public float getSemicircleGap() {
        return px2Dp(semicircleGap);
    }

    public void setSemicircleGap(float semicircleGap) {
        if (this.semicircleGap != semicircleGap) {
            this.semicircleGap = semicircleGap;
            calculate();
            invalidate();
        }
    }

    public float getSemicircleRadius() {
        return px2Dp(semicircleRadius);
    }

    public void setSemicircleRadius(float semicircleRadius) {
        if (this.semicircleRadius != semicircleRadius) {
            this.semicircleRadius = semicircleRadius;
            calculate();
            invalidate();
        }
    }

    public int getSemicircleColor() {
        return semicircleColor;
    }

    public void setSemicircleColor(int semicircleColor) {
        if (this.semicircleColor != semicircleColor) {
            this.semicircleColor = semicircleColor;
            calculate();
            invalidate();
        }
    }

    public float getDashLineLength() {
        return px2Dp(dashLineLength);
    }

    public void setDashLineLength(float dashLineLength) {
        if (this.dashLineLength != dashLineLength) {
            this.dashLineLength = dashLineLength;
            calculate();
            invalidate();
        }
    }

    public float getDashLineHeight() {
        return px2Dp(dashLineHeight);
    }

    public void setDashLineHeight(float dashLineHeight) {
        if (this.dashLineHeight != dashLineHeight) {
            this.dashLineHeight = dashLineHeight;
            calculate();
            invalidate();
        }
    }

    public float getDashLineGap() {
        return px2Dp(dashLineGap);
    }

    public void setDashLineGap(float dashLineGap) {
        if (this.dashLineGap != dashLineGap) {
            this.dashLineGap = dashLineGap;
            calculate();
            invalidate();
        }
    }

    public int getDashLineColor() {
        return dashLineColor;
    }

    public void setDashLineColor(int dashLineColor) {
        if (this.dashLineColor != dashLineColor) {
            this.dashLineColor = dashLineColor;
            calculate();
            invalidate();
        }
    }

    public boolean isSemicircleTop() {
        return isSemicircleTop;
    }

    public void setSemicircleTop(boolean semicircleTop) {
        if (this.isSemicircleTop != semicircleTop) {
            isSemicircleTop = semicircleTop;
            calculate();
            invalidate();
        }
    }

    public boolean isSemicircleBottom() {
        return isSemicircleBottom;
    }

    public void setSemicircleBottom(boolean semicircleBottom) {
        if (isSemicircleBottom != semicircleBottom) {
            isSemicircleBottom = semicircleBottom;
            calculate();
            invalidate();
        }
    }

    public boolean isSemicircleLeft() {
        return isSemicircleLeft;
    }

    public void setSemicircleLeft(boolean semicircleLeft) {
        if (isSemicircleLeft != semicircleLeft) {
            isSemicircleLeft = semicircleLeft;
            calculate();
            invalidate();
        }
    }

    public boolean isSemicircleRight() {
        return isSemicircleRight;
    }

    public void setSemicircleRight(boolean semicircleRight) {
        if (isSemicircleRight != semicircleRight) {
            isSemicircleRight = semicircleRight;
            calculate();
            invalidate();
        }
    }

    public boolean isDashLineTop() {
        return isDashLineTop;
    }

    public void setDashLineTop(boolean dashLineTop) {
        if (isDashLineTop != dashLineTop) {
            isDashLineTop = dashLineTop;
            calculate();
            invalidate();
        }
    }

    public boolean isDashLineBottom() {
        return isDashLineBottom;
    }

    public void setDashLineBottom(boolean dashLineBottom) {
        if (isDashLineBottom != dashLineBottom) {
            isDashLineBottom = dashLineBottom;
            calculate();
            invalidate();
        }
    }

    public boolean isDashLineLeft() {
        return isDashLineLeft;
    }

    public void setDashLineLeft(boolean dashLineLeft) {
        if (isDashLineLeft != dashLineLeft) {
            isDashLineLeft = dashLineLeft;
            calculate();
            invalidate();
        }
    }

    public boolean isDashLineRight() {
        return isDashLineRight;
    }

    public void setDashLineRight(boolean dashLineRight) {
        if (isDashLineRight != dashLineRight) {
            isDashLineRight = dashLineRight;
            calculate();
            invalidate();
        }
    }

    public float getDashLineMarginTop() {
        return px2Dp(dashLineMarginTop);
    }

    public void setDashLineMarginTop(float dashLineMarginTop) {
        if (this.dashLineMarginTop != dashLineMarginTop) {
            this.dashLineMarginTop = dashLineMarginTop;
            calculate();
            invalidate();
        }
    }

    public float getDashLineMarginBottom() {
        return px2Dp(dashLineMarginBottom);
    }

    public void setDashLineMarginBottom(float dashLineMarginBottom) {
        if (this.dashLineMarginBottom != dashLineMarginBottom) {
            this.dashLineMarginBottom = dashLineMarginBottom;
            calculate();
            invalidate();
        }
    }

    public float getDashLineMarginLeft() {
        return px2Dp(dashLineMarginLeft);
    }

    public void setDashLineMarginLeft(float dashLineMarginLeft) {
        if (this.dashLineMarginLeft != dashLineMarginLeft) {
            this.dashLineMarginLeft = dashLineMarginLeft;
            calculate();
            invalidate();
        }
    }

    public float getDashLineMarginRight() {
        return px2Dp(dashLineMarginRight);
    }

    public void setDashLineMarginRight(float dashLineMarginRight) {
        if (this.dashLineMarginRight != dashLineMarginRight) {
            this.dashLineMarginRight = dashLineMarginRight;
            calculate();
            invalidate();
        }
    }
}
