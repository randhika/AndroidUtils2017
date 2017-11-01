package com.fallwater.androidutils2017.network;

import com.fallwater.androidutils2017.BuildConfig;


/**
 * @author fallwater on 2017/11/1.
 *         功能描述:网络地址配置
 */
public class BaseUrlConfig {

    /**
     * 开发环境
     */
    private static final int ENV_DEVELOP = 1;

    /**
     * 测试环境
     */
    private static final int ENV_CHECK = 2;

    /**
     * 生产环境
     */
    private static final int ENV_PRODUCT = 3;

    /**
     * 本地（测试）Host
     */
    private static final String BASE_URL_LOCAL
            = "";

    /**
     * 网络（测试）Host
     */
    private static final String BASE_URL_TEST
            = "";

    /**
     * 网络（正式）Host
     */
    private static final String BASE_URL
            = "";

    /**
     * 连接接的地址
     */
    private static String url = BASE_URL;

    static {
        initUrl();
    }

    /**
     * 获取本应有配置的URL
     *
     * @return url
     */
    public static String url() {
        return url;
    }

    /**
     * 获取本应有配置的URL
     *
     * @return url
     */
    private static void initUrl() {
        switch (BuildConfig.ENV_TYPE) {
            case ENV_DEVELOP:
                url = BASE_URL_TEST;
                break;
            case ENV_CHECK:
                url = BASE_URL_LOCAL;
                break;
            case ENV_PRODUCT:
                if (BuildConfig.DEBUG) {
                    url = BASE_URL_TEST;
                } else {
                    url = BASE_URL;
                }
                break;
            default:
                url = BASE_URL_TEST;
                break;
        }
    }
}
