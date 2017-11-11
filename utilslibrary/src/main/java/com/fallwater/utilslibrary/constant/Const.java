package com.fallwater.utilslibrary.constant;

import android.os.Environment;


public class Const {

    public static final String SSL_STORE_PWD = "dontchange";

    public static final int DEFAULT_HTTP_TIMEOUT = 15 * 1000; // 15 seconds

    public static final String IMAGE_UNSPECIFIED = "image/*";

    public static final String INSTALLMENT_PATH = Environment
            .getExternalStorageDirectory().getPath() + "/fallwater/cache";

    public static final String IMAGE_CACHE_PATH = INSTALLMENT_PATH + "/images";

    public static final String DOWNLOAD_APK_PATH = INSTALLMENT_PATH + "/apk";

    public static final String SHARE_IMAGE_CACHE_PATH = INSTALLMENT_PATH + "/share/images";
}
