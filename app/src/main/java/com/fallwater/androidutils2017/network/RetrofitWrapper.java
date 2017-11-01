package com.fallwater.androidutils2017.network;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.fallwater.androidutils2017.network.interceptor.CommonInterceptor;
import com.fallwater.androidutils2017.network.interceptor.LogInterceptor;
import com.fallwater.androidutils2017.network.interceptor.StatusCodeInterceptor;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.X509TrustManager;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author fallwater on 2017/11/1.
 *         功能描述:retrofit包装类
 */
public final class RetrofitWrapper {

    private static RetrofitWrapper sInstance;

    private Retrofit mRetrofit;

    private ApiService mApiService;

    private OkHttpClient mOkHttpClient;

    @NonNull
    public static RetrofitWrapper getInstance(Context context) {
        if (sInstance == null) {
            synchronized (RetrofitWrapper.class) {
                if (sInstance == null) {
                    sInstance = new RetrofitWrapper(context);
                }
            }
        }
        return sInstance;
    }

    private RetrofitWrapper(@NonNull Context context) {
        init(context);
    }

    private void init(@NonNull Context context) {
        initOkHttp(context);
        initRetrofit();
    }

    private void initRetrofit() {
        if (mRetrofit == null) {
            mRetrofit = new Retrofit.Builder()
                    .client(mOkHttpClient)
                    .baseUrl(BaseUrlConfig.url())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
    }

    private void initOkHttp(@NonNull Context context) {
        if (mOkHttpClient != null) {
            return;
        }
        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .allEnabledCipherSuites()
                .allEnabledTlsVersions()
                .build();
        mOkHttpClient = new OkHttpClient.Builder()
                .cookieJar(RetrofitSettings.getInstance(context).getJavaNetCookieJar())
                .cache(RetrofitSettings.getInstance(context).getCache())
                .connectTimeout(1000, TimeUnit.SECONDS)
                .writeTimeout(2000, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(new CommonInterceptor())
                .addInterceptor(new StatusCodeInterceptor())
                .addNetworkInterceptor(new LogInterceptor())
                //使用Stetho调试网络
                .addNetworkInterceptor(new StethoInterceptor())
                .connectionSpecs(Arrays.asList(spec, ConnectionSpec.CLEARTEXT))
                .sslSocketFactory(RetrofitSettings.getInstance(context).getSSLSocketFactory(),
                        (X509TrustManager) RetrofitSettings.getInstance(context)
                                .getTrustManagers()[0])
                .hostnameVerifier(RetrofitSettings.getInstance(context).getHostnameVerifier())
                .build();
    }

    public <T> T getApiService(@NonNull Class<T> serviceClass) {
        return mRetrofit.create(serviceClass);
    }

    public ApiService getDefaultService() {
        if (mApiService == null) {
            synchronized (this) {
                if (mApiService == null) {
                    mApiService = getApiService(ApiService.class);
                }
            }
        }
        return mApiService;
    }
}
