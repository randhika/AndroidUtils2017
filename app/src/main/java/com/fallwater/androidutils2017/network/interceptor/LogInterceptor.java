package com.fallwater.androidutils2017.network.interceptor;

import com.fallwater.utilslibrary.BuildConfig;
import com.fallwater.utilslibrary.utils.LoggerUtils;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.Request;

/**
 * @author fallwater on 2017/11/1.
 *         功能描述:LogInterceptor
 */
public class LogInterceptor implements Interceptor {

    public static String TAG = "LogInterceptor";

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        okhttp3.Response response = chain.proceed(chain.request());
        if (!BuildConfig.DEBUG) {
            return response;
        }

        long startTime = System.currentTimeMillis();
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        okhttp3.MediaType mediaType = response.body().contentType();
        String content = response.body().string();
        LoggerUtils.d(TAG, "\n");
        LoggerUtils.d(TAG, "----------Start----------------");
        LoggerUtils.d(TAG, "| " + request.toString());
        String method = request.method();
        if ("POST".equals(method)) {
            StringBuilder sb = new StringBuilder();
            if (request.body() instanceof FormBody) {
                FormBody body = (FormBody) request.body();
                for (int i = 0; i < body.size(); i++) {
                    sb.append(body.encodedName(i) + "=" + body.encodedValue(i) + ",");
                }
                sb.delete(sb.length() - 1, sb.length());
                LoggerUtils.d(TAG, "| RequestParams:{" + sb.toString() + "}");
            }
        }
        LoggerUtils.d(TAG, "| Response:" + content);
        LoggerUtils.d(TAG, "----------End:" + duration + "毫秒----------");
        return response.newBuilder()
                .body(okhttp3.ResponseBody.create(mediaType, content))
                .build();
    }

}
