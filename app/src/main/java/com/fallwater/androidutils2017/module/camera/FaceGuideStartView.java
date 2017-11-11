package com.fallwater.androidutils2017.module.camera;

import com.fallwater.androidutils2017.R;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FaceGuideStartView extends LinearLayout {

    private final static long TOTAL_TIME = 3 * 1000;

    private final static long TIME_SPAN = 100;

    private TextView startView;

    private CountDownTimer timer;

    public FaceGuideStartView(Context context) {
        super(context);
        initView();
    }

    public FaceGuideStartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public FaceGuideStartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext())
                .inflate(R.layout.face_guide_start_view_layout, this, true);
        startView = (TextView) findViewById(R.id.face_guide_btn);
    }

    /**
     * 开始倒计时
     */
    public void startCountTime() {
        timer = new DownTimer(TOTAL_TIME, TIME_SPAN);
        timer.start();
    }

    public void setStartText(CharSequence text) {
        startView.setText(text);
    }

    public void setOnStartClickListener(OnClickListener l) {
        startView.setOnClickListener(l);
    }

    public void timerFinish() {
        if (timer != null) {
            timer.cancel();
        }
    }

    private DownTimerListener tickListener;

    public void setTimeTickListener(DownTimerListener listener) {
        tickListener = listener;
    }

    public class DownTimer extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link
         *                          #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public DownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            startView.setText(getContext().getString(R.string.btn_start,
                    (millisUntilFinished / 1000) + (millisUntilFinished % 1000 != 0 ? 1 : 0) + ""));
        }

        @Override
        public void onFinish() {
            if (tickListener != null) {
                tickListener.onFinish();
            }
        }
    }

    public interface DownTimerListener {

        void onFinish();
    }

}
