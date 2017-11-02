package com.fallwater.utilslibrary.rxjava;

/**
 * @author fallwater on 2017/11/2
 * @mail 1667376033@qq.com
 * 功能描述:ApiException
 */
public class ApiException extends Throwable {

    private String errorCode;

    private String message;

    public Object getData() {
        return data;
    }

    private Object data;

    public ApiException(String errorCode, String errorMsg, Object data) {
        super(errorMsg);
        this.message = getApiExceptionMessage(errorCode, errorMsg);
        this.errorCode = errorCode;
        this.data = data;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getResultCode() {
        return errorCode;
    }

    /**
     * 由于服务器传递过来的错误信息直接给用户看的话，用户未必能够理解
     * 需要根据错误码对错误信息进行一个转换，在显示给用户
     */
    private static String getApiExceptionMessage(String code, String errorMsg) {
        String message;
        switch (code) {
            default:
                message = errorMsg;
        }
        return message;
    }
}

