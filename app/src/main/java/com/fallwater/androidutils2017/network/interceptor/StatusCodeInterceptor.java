package com.fallwater.androidutils2017.network.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by
 *
 * @author fallwater on 2017/11/1.
 *
 *         功能描述:
 */
public class StatusCodeInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Response response = chain.proceed(originalRequest);
        switch (response.code()) {
            default:
                break;
        }

        return response;
    }
}
