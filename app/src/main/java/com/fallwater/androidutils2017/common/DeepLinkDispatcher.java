package com.fallwater.androidutils2017.common;


import com.fallwater.utilslibrary.utils.LoggerUtils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

/**
 * @author fallwater on 2017/11/10
 * @mail 1667376033@qq.com
 * 功能描述:
 */
public class DeepLinkDispatcher {

    private static final String TAG = "DeepLinkDispatcher";

    private static volatile DeepLinkDispatcher defaultInstance;

    public static DeepLinkDispatcher getInstance() {
        if (defaultInstance == null) {
            synchronized (DeepLinkDispatcher.class) {
                if (defaultInstance == null) {
                    defaultInstance = new DeepLinkDispatcher();
                }
            }
        }
        return defaultInstance;
    }

    public void onReceiveIntent(Intent intent, Activity activity) {
        LoggerUtils.d(TAG, "onReceiveIntent");
        String dataString = intent.getDataString();
        Uri uri = intent.getData();
        if (uri != null) {
            //完整的url信息
            String url = uri.toString();
            //scheme部分
            String schemes = uri.getScheme();
            //host部分
            String host = uri.getHost();
            //port部分
            int port = uri.getPort();
            //访问路径
            String path = uri.getPath();
            //编码路径
            String path1 = uri.getEncodedPath();
            //query部分
            String queryString = uri.getQuery();
            //获取参数值
            String systemInfo = uri.getQueryParameter("system");
            String id = uri.getQueryParameter("id");
            LoggerUtils.d(TAG, "url:" + url);
            LoggerUtils.d(TAG, "schemes:" + schemes);
            LoggerUtils.d(TAG, "host:" + host);
            LoggerUtils.d(TAG, "port:" + port);
            LoggerUtils.d(TAG, "dataString:" + dataString);
            LoggerUtils.d(TAG, "id:" + id);
            LoggerUtils.d(TAG, "path:" + path);
            LoggerUtils.d(TAG, "path1:" + path1);
            LoggerUtils.d(TAG, "queryString:" + queryString);
        }
    }

}
