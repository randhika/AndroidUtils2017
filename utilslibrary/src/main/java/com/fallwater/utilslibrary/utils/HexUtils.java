package com.fallwater.utilslibrary.utils;


import android.text.TextUtils;
import android.util.Log;


/**
 * Created by
 * @author fallwater on 2017/10/30.
 * 功能描述:进制转换工具类
 */
public class HexUtils {

    /**
     * byte数组转换成16进制字符串
     *
     * @param bts
     * @return 16进制字符串
     */
    public static String bytes2Hex(byte[] bts) {
        StringBuilder hex = new StringBuilder(bts.length * 2);
        for (byte b : bts) {
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }

    /**
     * byte数组转换成16进制字符串
     *
     * @return 16进制字符串
     */
    public static String parseBytes2Hex(byte[] bts) {
        StringBuilder hex = new StringBuilder(bts.length * 2);
        for (byte b : bts) {
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }

    /**
     * 16进制字符串转换成byte数组
     */
    public static byte[] parseHex2Byte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++) {
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
        }
        return result;
    }

    /**
     * 同parseBytes2Hex
     */
    public static String parseBytesToHexString(byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String x16 = Integer.toHexString(bytes[i]);
            if (x16.length() < 2) {
                sb.append("0" + x16);
            } else if (x16.length() > 2) {
                sb.append(x16.substring(x16.length() - 2));
            } else {
                sb.append(x16);
            }
        }
        return sb.toString();
    }

    /**
     * parseHex2Byte
     */
    public static byte[] parseHexStringToBytes(String intString) {
        if (TextUtils.isEmpty(intString)) {
            return null;
        }

        if (intString.length() % 2 == 1) {
            intString = "0" + intString;
        }
        byte[] bytes = new byte[intString.length() / 2];

        try {
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = (byte) Integer.parseInt(intString.substring(i * 2, i * 2 + 2), 16);
            }

            return bytes;
        } catch (Exception e) {
            Log.e("HexUtils", e.toString());
            return null;
        }
    }
}
