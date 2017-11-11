package com.fallwater.androidutils2017.module.camera;

import com.fallwater.androidutils2017.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.util.AttributeSet;
import android.view.View;


public class FaceDetectionFloatView extends View {

    private Drawable vectorDrawable;

    public FaceDetectionFloatView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public FaceDetectionFloatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public FaceDetectionFloatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    private Paint rectPaint;

    /**
     * 初始化
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        vectorDrawable = VectorDrawableCompat.create(getResources(), R.drawable.faceframe, null);
//        PathParser.PathDataNode[] d = PathParser.createNodesFromPathData("");
//        PathParser.PathDataNode x = d[1];
//        PathMeasure a;
//
        if (attrs != null) {
            TypedArray type = context.obtainStyledAttributes(attrs, R.styleable.FaceDetection);
            float left = type.getDimension(R.styleable.FaceDetection_textLeft, 0);
            float top = type.getDimension(R.styleable.FaceDetection_textTop, 0);
            String text = type.getString(R.styleable.FaceDetection_content);
            int color = type.getColor(R.styleable.FaceDetection_textColor, 0);
            int size = (int) type.getDimension(R.styleable.FaceDetection_textSize, 0);
            if (type != null) {
                type.recycle();
            }
        }
        rectPaint = new Paint();
        rectPaint.setColor(Color.RED);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(3);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        vectorDrawable.draw(canvas);
        canvas.drawRect(left, right, top, bottom, rectPaint);
    }

    private int left, right, top, bottom;

    public void setRect(double x, double y, double x1, double y1) {
        left = (int) x;
        right = (int) y;
        top = (int) x1;
        bottom = (int) y1;
        postInvalidate();
    }
}
