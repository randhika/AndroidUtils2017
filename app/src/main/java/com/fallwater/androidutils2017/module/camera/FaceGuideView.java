package com.fallwater.androidutils2017.module.camera;

import com.fallwater.androidutils2017.R;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FaceGuideView extends RelativeLayout {

    private ImageView userView;

    private ImageView scaleView;

    private ImageView tranView;

    private TextView okBtn;

    public FaceGuideView(Context context) {
        super(context);
        initView();
    }

    public FaceGuideView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public FaceGuideView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.face_guide_layout, this, true);
        userView = (ImageView) findViewById(R.id.face_guide_user);
        scaleView = (ImageView) findViewById(R.id.face_guide_scale);
        tranView = (ImageView) findViewById(R.id.face_guide_tran);
        okBtn = (TextView) findViewById(R.id.face_guide_btn);
    }

    public void start() {

        ObjectAnimator scale1 = ObjectAnimator.ofFloat(scaleView, "rotation", 0f, -45f)
                .setDuration(1000);
        ObjectAnimator scale2 = ObjectAnimator.ofFloat(scaleView, "rotation", -45f, 0f)
                .setDuration(500);

        // 平移手势
        ObjectAnimator tran = ObjectAnimator.ofFloat(tranView, "translationY", -50f, 0f)
                .setDuration(1000);
        // 用户头像移动
        ObjectAnimator userScaleX = ObjectAnimator.ofFloat(userView, "scaleX", 1.2f, 1f)
                .setDuration(1000);
        ObjectAnimator userScaleY = ObjectAnimator.ofFloat(userView, "scaleY", 1.2f, 1f)
                .setDuration(1000);

        ObjectAnimator userTran = ObjectAnimator.ofFloat(userView, "translationY", -50f, 0f)
                .setDuration(1000);
        ObjectAnimator userTran1 = ObjectAnimator.ofFloat(userView, "translationY", 0f, -50f)
                .setDuration(1000);

        tranView.setVisibility(View.GONE);
        AnimatorSet tranSet = new AnimatorSet();
        AnimatorSet scaleSet = new AnimatorSet();
        tranSet.play(tran).with(userTran);
        tranSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                tranView.setVisibility(View.GONE);
                scaleView.setVisibility(View.VISIBLE);
                scaleSet.start();
            }
        });

        scaleSet.play(scale1).before(scale2).with(userScaleX).with(userScaleY).with(userTran1);
        scaleSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                tranView.setVisibility(View.VISIBLE);
                scaleView.setVisibility(View.GONE);
                tranSet.start();
            }
        });
        scaleSet.start();
    }
}
