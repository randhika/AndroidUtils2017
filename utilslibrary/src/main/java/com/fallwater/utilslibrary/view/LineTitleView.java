package com.fallwater.utilslibrary.view;

import com.fallwater.utilslibrary.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by JMing on 2017/3/27.
 * 两边带有横线的标题 如：----- Title -----
 */
public class LineTitleView extends View {

    private final static int LINE_WIDTH_NODEF = -1;

    private String text;

    private int textColor;

    private float textSize;

    private float textMeasureWidth;

    private float textMeasureHeight;

    private Paint textPaint;

    private int lineHeight;

    private int lineTextSpan;

    private int lineWidth;

    private int lineColor;

    private Paint linePaint;

    public LineTitleView(Context context) {
        super(context);
        initView(context, null, 0);
    }

    public LineTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0);
    }

    public LineTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        if (attrs == null) {
            return;
        }
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.lineTitleView);
        text = ta.getString(R.styleable.lineTitleView_contentText);
        textColor = ta.getColor(R.styleable.lineTitleView_contentColor, 0);
        textSize = ta.getDimensionPixelSize(R.styleable.lineTitleView_contentSize, 9);
        lineHeight = ta.getDimensionPixelSize(R.styleable.lineTitleView_lineHeight, 1);
        lineTextSpan = ta.getDimensionPixelSize(R.styleable.lineTitleView_lineTextSpan, 0);
        lineColor = ta.getColor(R.styleable.lineTitleView_lineColor, 0);
        lineWidth = ta.getDimensionPixelSize(R.styleable.lineTitleView_lineWidth, LINE_WIDTH_NODEF);
        if (ta != null) {
            ta.recycle();
        }
        textPaint = new Paint();
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setAntiAlias(true);

        linePaint = new Paint();
        linePaint.setColor(lineColor);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(lineHeight);

    }

    private Rect textRect = new Rect();

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        textPaint.getTextBounds(text, 0, text.length(), textRect);
        textMeasureWidth = textRect.width();
        textMeasureHeight = textRect.height();
        if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            heightMeasureSpec = MeasureSpec
                    .makeMeasureSpec((int) textMeasureHeight + getPaddingTop() + getPaddingBottom(),
                            MeasureSpec.EXACTLY);
        }
        if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            widthMeasureSpec = MeasureSpec
                    .makeMeasureSpec((int) textMeasureWidth + getPaddingLeft() + getPaddingRight()
                            + (lineWidth == LINE_WIDTH_NODEF ? 0 : lineWidth * 2)
                            + lineTextSpan * 2, MeasureSpec.EXACTLY);
        }
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    public void setText(String text) {
        if (text == null) {
            return;
        }
        this.text = text;
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawToText(canvas);
        drawToLine(canvas);
    }

    /**
     * 画两边的线
     */
    private void drawToLine(Canvas canvas) {
        if (lineWidth == LINE_WIDTH_NODEF) {
            canvas.drawLine(getPaddingLeft(), getLineY(), getStopX(), getLineY(), linePaint);
            canvas.drawLine(getTextX() + textMeasureWidth + lineTextSpan, getLineY(),
                    getWidth() - getPaddingRight(), getLineY(), linePaint);
        } else {
            canvas.drawLine(getPaddingLeft() + getTextX() - lineTextSpan - lineWidth, getLineY(),
                    getStopX(), getLineY(), linePaint);
            canvas.drawLine(getTextX() + textMeasureWidth + lineTextSpan, getLineY(),
                    getTextX() + textMeasureWidth + lineTextSpan + lineWidth, getLineY(),
                    linePaint);
        }
    }

    public float getTextX() {
        return (getMeasuredWidth() - textMeasureWidth) / 2;
    }

    public float getTextY() {
        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        int baseline = (getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2
                - fontMetrics.top;
        return baseline;
    }

    /**
     * 画文本
     */
    private void drawToText(Canvas canvas) {
        canvas.drawText(text, getTextX(), getTextY(), textPaint);
    }

    public float getStopX() {
        return getTextX() - lineTextSpan;
    }

    public float getLineY() {
        return (float) (getHeight() / 2.0);
    }
}
