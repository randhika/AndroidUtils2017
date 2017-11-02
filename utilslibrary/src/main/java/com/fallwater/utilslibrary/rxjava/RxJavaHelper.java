package com.fallwater.utilslibrary.rxjava;

import com.fallwater.utilslibrary.bean.BaseBean;
import com.fallwater.utilslibrary.common.ActivityLifeCycleEvent;
import com.fallwater.utilslibrary.common.BaseActivity;
import com.orhanobut.hawk.Hawk;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * @author fallwater on 2017/11/2
 * @mail 1667376033@qq.com
 * 功能描述:RxJavaHelper
 */
public class RxJavaHelper {

    private static volatile RxJavaHelper sInstance;

    private RxJavaHelper() {

    }

    public static RxJavaHelper getInstance() {
        if (sInstance == null) {
            synchronized (RxJavaHelper.class) {
                if (sInstance == null) {
                    sInstance = new RxJavaHelper();
                }
            }
        }
        return sInstance;
    }

    /**
     * 不需要缓存，且强制刷新
     *
     * @param observable 监听
     */
    public void requestSubscribe(BaseActivity activity,
            Observable observable,
            final RequestSubscriber subscriber) {
        requestSubscribe(observable, subscriber, "", ActivityLifeCycleEvent.DESTROY,
                activity.mLifecycleSubject, false, true);
    }

    /**
     * 在基础请求上封装了在Activity的DESTROY生命周期时cancel请求
     *
     * @param observable   监听
     * @param subscriber   观察
     * @param cacheKey     缓存key
     * @param isSave       是否缓存
     * @param forceRefresh 是否强制刷新
     */
    public void requestSubscribe(BaseActivity activity,
            Observable observable,
            final RequestSubscriber subscriber,
            String cacheKey,
            boolean isSave,
            boolean forceRefresh) {
        requestSubscribe(observable, subscriber, cacheKey, ActivityLifeCycleEvent.DESTROY,
                activity.mLifecycleSubject, isSave, forceRefresh);
    }

    /**
     * 发起网络请求(基础)
     *
     * @param observable       监听
     * @param subscriber       观察
     * @param cacheKey         缓存key
     * @param event            需要在哪个生命周期cancel掉该请求
     * @param lifecycleSubject Activity 生命周期
     * @param isSave           是否缓存
     * @param forceRefresh     是否强制刷新
     */
    public void requestSubscribe(Observable observable,
            final RequestSubscriber subscriber,
            String cacheKey,
            final ActivityLifeCycleEvent event,
            final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject,
            boolean isSave,
            boolean forceRefresh) {
        //数据预处理
        Observable.Transformer<BaseBean<Object>, Object> result = handleResult(event,
                lifecycleSubject);

        Observable observable1 = observable.compose(result)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        //显示Dialog和一些其他操作
                    }
                });
        load(cacheKey, observable1, isSave, forceRefresh).subscribe(subscriber);
    }

    /**
     * 利用Observable.takeUntil()停止网络请求
     */
    @NonNull
    public <T> Observable.Transformer<T, T> bindUntilEvent(
            @NonNull final ActivityLifeCycleEvent event,
            final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject) {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> sourceObservable) {
                Observable<ActivityLifeCycleEvent> compareLifecycleObservable =
                        lifecycleSubject.takeFirst(new Func1<ActivityLifeCycleEvent, Boolean>() {
                            @Override
                            public Boolean call(ActivityLifeCycleEvent activityLifeCycleEvent) {
                                return activityLifeCycleEvent.equals(event);
                            }
                        });
                return sourceObservable.takeUntil(compareLifecycleObservable);
            }
        };
    }


    /**
     * @param <T>
     * @return
     */
    public <T> Observable.Transformer<BaseBean<T>, T> handleResult(
            final ActivityLifeCycleEvent event,
            final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject) {
        return new Observable.Transformer<BaseBean<T>, T>() {
            @Override
            public Observable<T> call(Observable<BaseBean<T>> observable) {
                Observable<ActivityLifeCycleEvent> compareLifecycleObservable =
                        lifecycleSubject.takeFirst(new Func1<ActivityLifeCycleEvent, Boolean>() {
                            @Override
                            public Boolean call(ActivityLifeCycleEvent activityLifeCycleEvent) {
                                return activityLifeCycleEvent.equals(event);
                            }
                        });
                return observable.flatMap(new Func1<BaseBean<T>, Observable<T>>() {
                    @Override
                    public Observable<T> call(BaseBean<T> result) {
                        if (result.success) {
                            return createSuccessObservable(result.data);
                        } else {
                            return createErrorObservable(result);
                        }
                    }
                }).takeUntil(compareLifecycleObservable)
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    @NonNull
    private <T> Observable<T> createErrorObservable(BaseBean<T> result) {
        return Observable.error(new ApiException(result.errCode,
                result.errMsg, result.data));
    }

    /**
     * 创建成功的数据
     */
    private <T> Observable<T> createSuccessObservable(final T data) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    subscriber.onNext(data);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });

    }

    /**
     * @param cacheKey     缓存的Key
     * @param isSave       是否缓存
     * @param forceRefresh 是否强制刷新
     */
    public <T> Observable<T> load(final String cacheKey,
            Observable<T> fromNetwork,
            boolean isSave,
            boolean forceRefresh) {
        Observable<T> fromCache = Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                if (!TextUtils.isEmpty(cacheKey)) {
                    T cache = (T) Hawk.get(cacheKey);
                    if (cache != null) {
                        subscriber.onNext(cache);
                    } else {
                        subscriber.onCompleted();
                    }
                } else {
                    subscriber.onCompleted();
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        //是否缓存
        if (isSave) {
            /**
             * 这里的fromNetwork 不需要指定Schedule,在handleRequest中已经变换了
             */
            fromNetwork = fromNetwork.map(new Func1<T, T>() {
                @Override
                public T call(T result) {
                    Hawk.put(cacheKey, result);
                    return result;
                }
            });
        }
        //强制刷新
        if (forceRefresh) {
            return fromNetwork;
        } else {
            return Observable.concat(fromCache, fromNetwork).takeFirst(new Func1<T, Boolean>() {
                @Override
                public Boolean call(T t) {
                    return t != null;
                }
            });
        }
    }
}
