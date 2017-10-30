package com.fallwater.utilslibrary.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Created by
 * @author fallwater on 2017/10/30.
 * 功能描述:SP存储工具类
 */
public class SharedPrefsUtils {

    private static final String SECURITY_KEY = "silvrrcy";//长度必须是8位

    /**
     * Put Value
     *
     * @param context
     * @param key
     * @param value
     * @return
     */
    public static boolean putValue(Context context, String key, Object value) {
        return putValue(context, null, key, value);
    }

    public static boolean putValue(Context context, String key, Object value, boolean[] keyValueEncrypt) {
        return putValue(context, null, key, value, keyValueEncrypt);
    }

    public static boolean putValue(Context context, String name, String key, Object value) {
        return putValue(context, name, key, value, new boolean[]{true, true});
    }

    public static boolean putValue(Context context, String name, String key, Object value, boolean[] keyValueEncrypt) {
        return putValue(context, name, key, value, Context.MODE_PRIVATE, keyValueEncrypt);
    }

    public static boolean putValue(Context context, String name, String key, Object value, int mode, boolean[] keyValueEncrypt) {
        ArrayMap<String, Object> map = new ArrayMap<String, Object>();
        map.put(key, value);
        return putValue(context, name, map, keyValueEncrypt);
    }

    public static boolean putValue(Context context, String name, Map<String, Object> map, boolean[] keyValueEncrypt) {
        return putValue(context, name, map, Context.MODE_PRIVATE, keyValueEncrypt);
    }

    @TargetApi(11)
    @SuppressWarnings("unchecked")
    public static boolean putValue(Context context, String name, Map<String, Object> map, int mode, boolean[] keyValueEncrypt) {
        SharedPreferences preferences = null;
        if (TextUtils.isEmpty(name)) {
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
        } else {
            preferences = context.getSharedPreferences(name, mode);
        }

        SharedPreferences.Editor editor = preferences.edit();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = keyValueEncrypt[0] ? SecurityUtils.MD5(entry.getKey()) : entry.getKey();
            Object value = entry.getValue();

            if (keyValueEncrypt[1] && !(value instanceof Set)) {
                editor.putString(key, SecurityUtils.DESencrypt(String.valueOf(value), SECURITY_KEY));
            } else {
                if (value instanceof Boolean) {
                    editor.putBoolean(key, Boolean.parseBoolean(String.valueOf(value)));
                } else if (value instanceof Float) {
                    editor.putFloat(key, Float.parseFloat(String.valueOf(value)));
                } else if (value instanceof Integer) {
                    editor.putInt(key, Integer.parseInt(String.valueOf(value)));
                } else if (value instanceof Long) {
                    editor.putLong(key, Long.parseLong(String.valueOf(value)));
                } else if (value instanceof String) {
                    editor.putString(key, String.valueOf(value));
                } else if (value instanceof Set) {
                    if (keyValueEncrypt[1]) {
                        Set<String> sets = (Set<String>) value;
                        Set<String> tempSets = new HashSet<String>();
                        for (String s : sets) {
                            tempSets.add(SecurityUtils.DESencrypt(String.valueOf(s), SECURITY_KEY));
                        }
                        editor.putStringSet(key, tempSets);
                    } else {
                        editor.putStringSet(key, (Set<String>) value);
                    }
                } else {
                    throw new IllegalArgumentException("Value type is not support!");
                }
            }
        }
        return editor.commit();
    }

    /**
     * Remove Key
     *
     * @param context
     * @param key
     */
    public static boolean removeKey(Context context, String key) {
        return removeKey(context, null, key);
    }

    public static boolean removeKey(Context context, String name, String key) {
        return removeKey(context, name, key, true);
    }

    public static boolean removeKey(Context context, String name, String key, boolean isKeyEncrypt) {
        return removeKey(context, name, key, Context.MODE_PRIVATE, isKeyEncrypt);
    }

    public static boolean removeKey(Context context, String name, String key, int mode, boolean isKeyEncrypt) {
        SharedPreferences preferences = null;
        if (TextUtils.isEmpty(name)) {
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
        } else {
            preferences = context.getSharedPreferences(name, mode);
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(isKeyEncrypt ? SecurityUtils.DESencrypt(key, SECURITY_KEY) : key);
        return editor.commit();
    }

    /**
     * Get String
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static String getString(Context context, String key, String defValue) {
        return getString(context, null, key, defValue);
    }

    public static String getString(Context context, String name, String key, String defValue) {
        return getString(context, name, key, defValue, Context.MODE_PRIVATE, new boolean[]{true, true});
    }

    public static String getString(Context context, String name, String key, String defValue, boolean[] keyValueEncrypt) {
        return getString(context, name, key, defValue, Context.MODE_PRIVATE, keyValueEncrypt);
    }

    public static String getString(Context context, String name, String key, String defValue, int mode, boolean[] keyValueEncrypt) {
        SharedPreferences preferences = null;
        if (TextUtils.isEmpty(name)) {
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
        } else {
            preferences = context.getSharedPreferences(name, mode);
        }

        String value = preferences.getString(keyValueEncrypt[0] ? SecurityUtils.MD5(key) : key, defValue);
        if (value.equals(defValue)) {
            return value;
        } else {
            return keyValueEncrypt[1] ? SecurityUtils.DESdecrypt(value, SECURITY_KEY) : value;
        }
    }

    /**
     * Get Int
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static int getInt(Context context, String key, int defValue) {
        return getInt(context, null, key, defValue);
    }

    public static int getInt(Context context, String name, String key, int defValue) {
        return getInt(context, name, key, defValue, new boolean[]{true, true});
    }

    public static int getInt(Context context, String name, String key, int defValue, boolean[] keyValueEncrypt) {
        return getInt(context, name, key, defValue, Context.MODE_PRIVATE, keyValueEncrypt);
    }

    public static int getInt(Context context, String name, String key, int defValue, int mode, boolean[] keyValueEncrypt) {
        SharedPreferences preferences = null;
        if (TextUtils.isEmpty(name)) {
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
        } else {
            preferences = context.getSharedPreferences(name, mode);
        }

        if (keyValueEncrypt[1]) {
            String value = getString(context, name, key, String.valueOf(defValue), mode, keyValueEncrypt);
            try {
                return Integer.valueOf(value);
            } catch (Exception e) {
                return defValue;
            }
        } else {
            return preferences.getInt(keyValueEncrypt[0] ? SecurityUtils.MD5(key) : key, defValue);
        }
    }

    /**
     * Get Long
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static long getLong(Context context, String key, long defValue) {
        return getLong(context, null, key, defValue);
    }

    public static long getLong(Context context, String name, String key, long defValue) {
        return getLong(context, name, key, defValue, new boolean[]{true, true});
    }

    public static long getLong(Context context, String name, String key, long defValue, boolean[] keyValueEncrypt) {
        return getLong(context, name, key, defValue, Context.MODE_PRIVATE, keyValueEncrypt);
    }

    public static long getLong(Context context, String name, String key, long defValue, int mode, boolean[] keyValueEncrypt) {
        SharedPreferences preferences = null;
        if (TextUtils.isEmpty(name)) {
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
        } else {
            preferences = context.getSharedPreferences(name, mode);
        }

        if (keyValueEncrypt[1]) {
            String value = getString(context, name, key, String.valueOf(defValue), mode, keyValueEncrypt);

            try {
                return Long.valueOf(value);
            } catch (Exception e) {
                return defValue;
            }
        } else {
            return preferences.getLong(keyValueEncrypt[0] ? SecurityUtils.MD5(key) : key, defValue);
        }
    }

    /**
     * Get Float
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static float getFloat(Context context, String key, float defValue) {
        return getFloat(context, null, key, defValue);
    }

    public static float getFloat(Context context, String name, String key, float defValue) {
        return getFloat(context, name, key, defValue, new boolean[]{true, true});
    }

    public static float getFloat(Context context, String name, String key, float defValue, boolean[] keyValueEncrypt) {
        return getFloat(context, name, key, defValue, Context.MODE_PRIVATE, keyValueEncrypt);
    }

    public static float getFloat(Context context, String name, String key, float defValue, int mode, boolean[] keyValueEncrypt) {
        SharedPreferences preferences = null;
        if (TextUtils.isEmpty(name)) {
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
        } else {
            preferences = context.getSharedPreferences(name, mode);
        }

        if (keyValueEncrypt[1]) {
            String value = getString(context, name, key, String.valueOf(defValue), mode, keyValueEncrypt);
            try {
                return Float.valueOf(value);
            } catch (Exception e) {
                return defValue;
            }
        } else {
            return preferences.getFloat(keyValueEncrypt[0] ? SecurityUtils.MD5(key) : key, defValue);
        }

    }

    /**
     * boolean
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static boolean getBoolean(Context context, String key, boolean defValue) {
        return getBoolean(context, null, key, defValue);
    }

    public static boolean getBoolean(Context context, String name, String key, boolean defValue) {
        return getBoolean(context, name, key, defValue, new boolean[]{true, true});
    }

    public static boolean getBoolean(Context context, String name, String key, boolean defValue, boolean[] keyValueEncrypt) {
        return getBoolean(context, name, key, defValue, Context.MODE_PRIVATE, keyValueEncrypt);
    }

    public static boolean getBoolean(Context context, String name, String key, boolean defValue, int mode, boolean[] keyValueEncrypt) {
        SharedPreferences preferences = null;
        if (TextUtils.isEmpty(name)) {
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
        } else {
            preferences = context.getSharedPreferences(name, mode);
        }

        if (keyValueEncrypt[1]) {
            String valueString = getString(context, name, key, String.valueOf(defValue), mode, keyValueEncrypt);
            try {
                return Boolean.valueOf(valueString);
            } catch (Exception e) {
                return defValue;
            }
        } else {
            return preferences.getBoolean(keyValueEncrypt[0] ? SecurityUtils.MD5(key) : key, defValue);
        }
    }

    /**
     * StringSet
     *
     * @param context
     * @param key
     * @param defValues
     * @return
     */
    public static Set<String> getStringSet(Context context, String key, Set<String> defValues) {
        return getStringSet(context, null, key, defValues);
    }

    public static Set<String> getStringSet(Context context, String name, String key, Set<String> defValues) {
        return getStringSet(context, name, key, defValues, new boolean[]{true, true});
    }

    public static Set<String> getStringSet(Context context, String name, String key, Set<String> defValues, boolean[] keyValueEncrypt) {
        return getStringSet(context, name, key, defValues, Context.MODE_PRIVATE, keyValueEncrypt);
    }

    /**
     * @param context
     * @param name
     * @param key
     * @param defValues
     * @param mode
     * @return
     */
    @TargetApi(11)
    public static Set<String> getStringSet(Context context, String name, String key, Set<String> defValues, int mode, boolean[] keyValueEncrypt) {
        SharedPreferences preferences = null;
        if (TextUtils.isEmpty(name)) {
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
        } else {
            preferences = context.getSharedPreferences(name, mode);
        }
        Set<String> valueSet = preferences.getStringSet(keyValueEncrypt[0] ? SecurityUtils.MD5(key) : key, defValues);
        Set<String> tempValueSet = new HashSet<String>();
        for (String s : valueSet) {
            tempValueSet.add(SecurityUtils.DESdecrypt(s, SECURITY_KEY));
        }
        return tempValueSet;
    }

    private SharedPrefsUtils() {/*Do not new me*/}

}
