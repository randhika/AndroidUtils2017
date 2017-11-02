package com.fallwater.utilslibrary.rxjava;

import com.fallwater.utilslibrary.R;
import com.fallwater.utilslibrary.common.ActivityManager;
import com.fallwater.utilslibrary.utils.NetworkUtils;

import android.app.Activity;

import rx.Subscriber;

/**
 * @author fallwater on 2017/11/2
 * @mail 1667376033@qq.com
 * 功能描述:RequestSubscriber
 */
public abstract class RequestSubscriber<T> extends Subscriber<T> implements ICancelListener {

    public RequestSubscriber() {
        onStartC();
    }

    @Override
    public void onCompleted() {
        onCompletedC();
    }

    @Override
    public void onNext(T t) {
        onNextC(t);
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        if (!NetworkUtils.isNetworkAvailable(
                //这里自行替换判断网络的代码
                ActivityManager.getInstance().getCurrentActivity())) {
            Activity activity = ActivityManager.getInstance().getCurrentActivity();
            if (activity != null && !activity.isFinishing()) {
                onErrorC(String.valueOf(Integer.MIN_VALUE),
                        activity.getString(R.string.networks_unavailable), null);
            }
        } else if (e instanceof ApiException) {
            ApiException apiException = (ApiException) e;
            try {
                onErrorC(apiException.getResultCode(), apiException.getMessage(),
                        (T) (apiException.getData()));
            } catch (Exception e1) {
                e1.printStackTrace();
                onErrorC(apiException.getResultCode(), apiException.getMessage(), null);
            }
        } else {
            Activity activity = ActivityManager.getInstance().getCurrentActivity();
            if (activity != null && !activity.isFinishing()) {
                onErrorC(String.valueOf(Integer.MAX_VALUE),
                        activity.getString(R.string.network_exception), null);
            }
        }
    }


    @Override
    public void onCancel() {
        if (!this.isUnsubscribed()) {
            this.unsubscribe();
        }
    }

    protected abstract void onStartC();

    protected abstract void onNextC(T t);

    protected abstract void onCompletedC();

    protected abstract void onErrorC(String errorCode, String message, T t);
}
