package com.fallwater.androidutils2017.network;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.File;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.JavaNetCookieJar;

/**
 * Created by
 * @author fallwater on 2017/11/1.
 *         功能描述:RetrofitSettings
 */
public class RetrofitSettings {

    private static RetrofitSettings sInstance;

    private TrustManager[] mTrustManagers;

    private Cache mCache;

    private SSLSocketFactory mSSLSocketFactory;

    private HostnameVerifier mHostnameVerifier;

    private JavaNetCookieJar mJavaNetCookieJar;

    public JavaNetCookieJar getJavaNetCookieJar() {
        return mJavaNetCookieJar;
    }
    public TrustManager[] getTrustManagers() {
        return mTrustManagers;
    }

    public Cache getCache() {
        return mCache;
    }

    public SSLSocketFactory getSSLSocketFactory() {
        return mSSLSocketFactory;
    }

    public HostnameVerifier getHostnameVerifier() {
        return mHostnameVerifier;
    }

    private RetrofitSettings(Context context) {
        init(context);
    }

    private void init(Context context) {
        initTrustManager();
        initCache(context);
        initSSLSocketFactory();
        initHostnameVerifier();
        initJavaNetCookieJar(context);
    }

    private void initHostnameVerifier() {
        mHostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
    }

    private void initSSLSocketFactory() {
        try {
            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            if (mTrustManagers == null) {
                initTrustManager();
            }
            sslContext.init(null, mTrustManagers, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            mSSLSocketFactory = sslSocketFactory;
        } catch (Exception e) {
            mSSLSocketFactory = null;
        }
    }

    private void initCache(Context context) {
        File httpCacheDirectory = new File(context.getCacheDir(), "httpCache");
        // 100 MiB
        int cacheSize = 100 * 1024 * 1024;
        mCache = new Cache(httpCacheDirectory, cacheSize);
    }

    private void initTrustManager() {
        mTrustManagers = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain,
                            String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain,
                            String authType) throws CertificateException {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };

    }

    /**
     * 初始化Cookie
     */
    @NonNull
    private void initJavaNetCookieJar(Context context) {
        CookieStore cookieStore = new PersistentCookieStore(context);
        CookieHandler cookieHandler = new CookieManager(
                cookieStore, CookiePolicy.ACCEPT_ALL);
        mJavaNetCookieJar = new JavaNetCookieJar(cookieHandler);
    }

    public static RetrofitSettings getInstance(Context context) {
        if (sInstance == null) {
            synchronized (RetrofitSettings.class) {
                if (sInstance == null) {
                    sInstance = new RetrofitSettings(context);
                }
            }
        }
        return sInstance;
    }
}
