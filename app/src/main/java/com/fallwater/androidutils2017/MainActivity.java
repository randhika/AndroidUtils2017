package com.fallwater.androidutils2017;

import com.fallwater.androidutils2017.common.DeepLinkDispatcher;
import com.fallwater.utilslibrary.common.BaseActivity;

import android.os.Bundle;


/**
 * Created by
 * @author fallwater on 2017/10/30.
 * 功能描述:mainactivity
 */
public class MainActivity extends BaseActivity {

    @Override
    protected int initLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initDataOnCreate() {
        DeepLinkDispatcher.getInstance().onReceiveIntent(getIntent(),this);
    }

    @Override
    protected void initDataOnStart() {

    }
}
