package com.fallwater.utilslibrary.common;

import android.os.Bundle;

import butterknife.ButterKnife;


/**
 * Created by
 *
 * @author fallwater on 2017/11/01.
 * 功能描述:BaseActivity
 */
public abstract class BaseActivity extends BaseToolBarActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(initLayoutId());
        ButterKnife.bind(this);
        initBeforeView();
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
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 初始化一些其他其他第三方的功能，比如EventBus注册，生命周期注册等
     */
    protected abstract void initBeforeView();

    protected abstract int initLayoutId();

    protected abstract void initView(Bundle savedInstanceState);

    protected abstract void initListener();

    protected abstract void initDataOnCreate();

    protected abstract void initDataOnStart();

}
