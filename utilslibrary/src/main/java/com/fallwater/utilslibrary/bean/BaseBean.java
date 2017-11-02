package com.fallwater.utilslibrary.bean;

/**
 * @author fallwater on 2017/11/1.
 *         功能描述:
 */
public class BaseBean<T> {

    /**
     * Indicate if the request is successfully handled.
     */
    public boolean success = false;

    /**
     * current system time
     */
    public long sysTime = 0;

    /**
     * if success is false, errCode indicates the specific error
     */
    public String errCode = "";

    /**
     * a brief description of errCode
     */
    public String errMsg = "";

    /**
     * 真正的数据
     */
    public T data;

    @Override
    public String toString() {
        return "success = " + success + ",sysTime = " + sysTime + ",errCode = " + errCode
                + ",errMsg = " + errMsg;
    }

}
