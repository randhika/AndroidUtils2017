package com.fallwater.utilslibrary.utils;


import com.fallwater.utilslibrary.BuildConfig;

import android.util.Log;

/**
 * @author fallwater on 2017/11/1.
 *         功能描述:LoggerUtils
 */
public class LoggerUtils {

    public final static boolean DEBUG = BuildConfig.DEBUG;

    private final static int MESSAGE_MAX_LENGTH = 4000;

    /**
     * 根据type输出日志消息，包括方法名，方法行数，Message
     */
    private static void log(int type, String message) {
        StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[4];
        String className = stackTrace.getClassName();
        String tag = className.substring(className.lastIndexOf('.') + 1);
        message = stackTrace.getMethodName() + "#" + stackTrace.getLineNumber() + " [" + message
                + "]";
        switch (type) {
            case Log.DEBUG:
                Log.d(tag, message);
                break;
            case Log.INFO:
                Log.i(tag, message);
                break;
            case Log.WARN:
                Log.w(tag, message);
                break;
            case Log.ERROR:
                Log.e(tag, message);
                break;
            case Log.VERBOSE:
                Log.v(tag, message);
                break;
            default:
                break;
        }
    }

    /**
     * 根据type输出日志消息，包括方法名，方法行数，Message，Throwable异常消息
     */
    private static void log(int type, String message, Throwable tr) {
        StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[4];
        String className = stackTrace.getClassName();
        String tag = className.substring(className.lastIndexOf('.') + 1);
        message = stackTrace.getMethodName() + "#" + stackTrace.getLineNumber() + " [" + message
                + "]";
        switch (type) {
            case Log.DEBUG:
                Log.d(tag, message, tr);
                break;
            case Log.INFO:
                Log.i(tag, message, tr);
                break;
            case Log.WARN:
                Log.w(tag, message, tr);
                break;
            case Log.ERROR:
                Log.e(tag, message, tr);
                break;
            case Log.VERBOSE:
                Log.v(tag, message, tr);
                break;
            default:
                break;
        }
    }

    public static void d(String tag, String message) {
        if (DEBUG) {
            Log.d(tag, message);
        }
    }

    public static void i(String tag, String message) {
        if (DEBUG) {
            Log.i(tag, message);
        }
    }

    public static void w(String tag, String message) {
        if (DEBUG) {
            Log.w(tag, message);
        }
    }

    public static void e(String tag, String message) {
        if (DEBUG) {
            Log.e(tag, message);
        }
    }

    public static void v(String tag, String message) {
        if (DEBUG) {
            Log.v(tag, message);
        }
    }

    public static void d(String message) {
        if (DEBUG) {
            log(Log.DEBUG, message);
        }
    }

    public static void i(String message) {
        if (DEBUG) {
            log(Log.INFO, message);
        }
    }

    public static void w(String message) {
        if (DEBUG) {
            log(Log.WARN, message);
        }
    }

    public static void e(String message) {
        if (DEBUG) {
            log(Log.ERROR, message);
        }
    }

    public static void v(String message) {
        if (DEBUG) {
            log(Log.VERBOSE, message);
        }
    }

    public static void d(String message, Throwable tr) {
        if (DEBUG) {
            log(Log.DEBUG, message, tr);
        }
    }

    public static void i(String message, Throwable tr) {
        if (DEBUG) {
            log(Log.INFO, message, tr);
        }
    }

    public static void w(String message, Throwable tr) {
        if (DEBUG) {
            log(Log.WARN, message, tr);
        }
    }

    public static void e(String message, Throwable tr) {
        if (DEBUG) {
            log(Log.ERROR, message, tr);
        }
    }

    public static void v(String message, Throwable tr) {
        if (DEBUG) {
            log(Log.VERBOSE, message, tr);
        }
    }

    public static void i_muptile(String tag, String message) {
        if (message.length() > MESSAGE_MAX_LENGTH) {
            for (int i = 0; i < message.length(); i += MESSAGE_MAX_LENGTH) {
                if (i + MESSAGE_MAX_LENGTH < message.length()) {
                    Log.i(tag, message.substring(i, i + MESSAGE_MAX_LENGTH));
                } else {
                    Log.i(tag, message.substring(i, message.length()));
                }
            }
        } else {
            Log.i(tag, message);
        }
    }
}
