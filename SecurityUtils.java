package org.cy.uilibrary.utils;

import org.cy.uilibrary.BuildConfig;

import android.text.TextUtils;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


/**
 * Created by cy on 2017/9/6.
 * <p/>
 * 功能描述:加密工具类,有条件做成.so库
 */
public class SecurityUtils {

    private static final String DES = "DES";

    private static final String AES = "AES";

    private static final String CRYPT_KEY = "4700d67e6057c77641e81bf67c1a4679";

    /**
     * MD5
     */
    public static String MD5(String srcString) {
        if (TextUtils.isEmpty(srcString)) {
            throw new IllegalArgumentException("srcString cannot be null!");
        }

        try {
            return MD5(srcString.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    public static String MD5(byte[] srcBytes) {
        if (srcBytes == null) {
            throw new IllegalArgumentException("bytes cannot be null!");
        }

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(srcBytes);
            byte[] bytes = md.digest();
            int i = 0;
            StringBuffer buffer = new StringBuffer("");
            for (int offset = 0; offset < bytes.length; offset++) {
                i = bytes[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buffer.append("0");
                }
                buffer.append(Integer.toHexString(i));
            }
            return buffer.toString();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.e("SecurityUtils", e.getMessage());
            }
        }
        return "";
    }

    /**
     * @param dataSource
     * @param password
     * @return
     */
    public static String DESencrypt(String dataSource, String password) {
        try {
            return HexUtils.parseBytesToHexString(DESencrypt(dataSource.getBytes(), password));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param data
     * @param password
     * @return
     */
    public static String DESdecrypt(String data, String password) {
        byte[] bytes;
        try {
            bytes = DESdecrypt(HexUtils.parseHexStringToBytes(data), password);
            if (bytes == null) {
                return "";
            }
            return new String(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加密
     */
    public static byte[] DESencrypt(byte[] src, String key) throws Exception {
        Cipher cipher = Cipher.getInstance(DES);
        SecretKeySpec securekey = new SecretKeySpec(key.getBytes(), DES);
        cipher.init(Cipher.ENCRYPT_MODE, securekey);// 设置密钥和加密形式
        return cipher.doFinal(src);
    }

    /**
     * 解密
     */
    public static byte[] DESdecrypt(byte[] src, String key) throws Exception {
        Cipher cipher = Cipher.getInstance(DES);
        SecretKeySpec securekey = new SecretKeySpec(key.getBytes(), DES);// 设置加密Key
        cipher.init(Cipher.DECRYPT_MODE, securekey);// 设置密钥和解密形式
        return cipher.doFinal(src);
    }

    /**
     * 加密
     */
    public static byte[] AESencrypt(byte[] src, String key) throws Exception {
        Cipher cipher = Cipher.getInstance(AES);
        SecretKeySpec securekey = new SecretKeySpec(key.getBytes(), AES);
        cipher.init(Cipher.ENCRYPT_MODE, securekey);// 设置密钥和加密形式
        return cipher.doFinal(src);
    }

    /**
     * 解密
     */
    public static byte[] AESdecrypt(byte[] src, String key) throws Exception {
        Cipher cipher = Cipher.getInstance(AES);
        SecretKeySpec securekey = new SecretKeySpec(key.getBytes(), AES);// 设置加密Key
        cipher.init(Cipher.DECRYPT_MODE, securekey);// 设置密钥和解密形式
        return cipher.doFinal(src);
    }


    private SecurityUtils() {/*Do not new me*/}

}
