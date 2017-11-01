package com.fallwater.androidutils2017.network.interceptor;

import com.fallwater.androidutils2017.MyApplication;
import com.fallwater.androidutils2017.network.BaseUrlConfig;
import com.fallwater.utilslibrary.utils.DeviceUtils;

import android.text.TextUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author fallwater on 2017/11/1.
 *         功能描述:CommonInterceptor
 */
public class CommonInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request.Builder builder = chain.request().newBuilder();
        String token = "";
        if (!TextUtils.isEmpty(token)) {
            builder.addHeader("Authorization", token);
        }
        builder
                .addHeader("versionCode",
                        "")
                .addHeader("versionName", "")
                .addHeader("countryId", "1")
                .addHeader("languageId", "1")
                .addHeader("deviceType", DeviceUtils.getDeviceType())
                .addHeader("deviceId", DeviceUtils
                        .getUniquePsuedoID(MyApplication.getInstance().getBaseContext()));
        String firebaseToken = "";
        if (!TextUtils.isEmpty(firebaseToken)) {
            builder.addHeader("deviceToken", firebaseToken).build();
        }
        Request request = builder.build();

        if (!request.url().toString().startsWith(BaseUrlConfig.url())) {
            request = request.newBuilder()
                    .removeHeader("Authorization")
                    .removeHeader("versionCode")
                    .removeHeader("versionName")
                    .removeHeader("countryId")
                    .removeHeader("languageId")
                    .removeHeader("deviceType")
                    .removeHeader("deviceId")
                    .removeHeader("deviceToken")
                    .build();
        }
        return chain.proceed(request);
    }
}
