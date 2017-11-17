package com.fallwater.utilslibrary.common;

import com.fallwater.utilslibrary.R;

import org.greenrobot.eventbus.EventBus;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.subjects.PublishSubject;


/**
 * @author Fallwater潘建波 on 2017/11/17
 * @mail 1667376033@qq.com
 * 功能描述:
 */
public abstract class AbsBaseActivity extends BaseToolBarActivity {

    public final PublishSubject<ActivityLifeCycleEvent> mLifecycleSubject = PublishSubject.create();

    private Unbinder mUnBinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(initLayoutId());
        initToolBar();
        registerOnCreate();
        initView(savedInstanceState);
        initListener();
        initDataOnCreate();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initDataOnStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLifecycleSubject.onNext(ActivityLifeCycleEvent.STOP);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLifecycleSubject.onNext(ActivityLifeCycleEvent.RESUME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLifecycleSubject.onNext(ActivityLifeCycleEvent.PAUSE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearOnDestroy();
    }

    private void clearOnDestroy() {
        mUnBinder.unbind();
        EventBus.getDefault().unregister(this);
        mLifecycleSubject.onNext(ActivityLifeCycleEvent.DESTROY);
        ActivityManager.getInstance().remove(this);
    }

    /**
     * 初始化一些其他其他第三方的功能，比如EventBus注册，生命周期注册等
     */
    protected void registerOnCreate() {
        mLifecycleSubject.onNext(ActivityLifeCycleEvent.CREATE);
        mUnBinder = ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        ActivityManager.getInstance().addActivity(this);
    }

    private void initToolBar() {
        setToolbarLeftDrawable(R.drawable.svg_icon_back);
        setToolbarTitleTextColorRes(android.R.color.holo_blue_dark);
        setToolbarTitleTextSize(18);
        setImmersiveStatusBar(true, ContextCompat.getColor(this, android.R.color.white));
    }

    protected abstract int initLayoutId();

    protected abstract void initView(Bundle savedInstanceState);

    protected abstract void initListener();

    protected abstract void initDataOnCreate();

    protected abstract void initDataOnStart();

}
