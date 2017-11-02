package com.fallwater.utilslibrary.common;

import com.fallwater.utilslibrary.R;
import com.fallwater.utilslibrary.utils.FragmentUtils;

import android.os.Bundle;



/**
 * @author fallwater on 2017/11/02.
 *         功能描述:BaseFragmentActivity
 */
public abstract class BaseFragmentActivity extends BaseActivity {

    protected abstract BaseFragment initViewAndFirstFragment(Bundle savedInstanceState);

    @Override
    protected final int initLayoutId() {
        return R.layout.fragment_container;
    }

    @Override
    protected final void initView(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            FragmentUtils
                    .showFragment(getSupportFragmentManager(),
                            FragmentUtils.getCurrentFragment(getSupportFragmentManager()), false);
        } else {
            FragmentUtils.showFragmentWithOutAnimation(getSupportFragmentManager(),
                    initViewAndFirstFragment(savedInstanceState), false);
        }
    }
}
