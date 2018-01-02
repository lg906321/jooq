package com.test.music.common;

import com.alibaba.fastjson.JSONObject;
import io.vertx.core.json.JsonArray;
import org.apache.commons.lang3.StringUtils;

public final class SQLUtil {

    private final static String _blank = " ";
    private final static String _like = "LIKE ";
    private final static String _wen = "? ";
    private final static String _eq = "= ";
    private final static String _and = "AND ";
    private final static String _or = "OR ";
    private final static String _IN = "in ";


    public void eqInteger(StringBuilder where, JsonArray array, JSONObject inParam, String key) {
        eqInteger(where, array, inParam, key, key);
    }

    public void eqInteger(StringBuilder where, JsonArray array, JSONObject inParam, String paramKey, String sqlKey) {
        eq(where, array, inParam, paramKey, sqlKey, Integer.class);
    }

    public void eqString(StringBuilder where, JsonArray array, JSONObject inParam, String key) {
        eqString(where, array, inParam, key, key);
    }

    public void eqString(StringBuilder where, JsonArray array, JSONObject inParam, String paramKey, String sqlKey) {
        eq(where, array, inParam, paramKey, sqlKey, String.class);
    }


    public void like(StringBuilder where, JsonArray array, JSONObject inParam, String paramKey) {
        like(where, array, inParam, paramKey, paramKey);
    }

    public void like(StringBuilder where, JsonArray array, JSONObject inParam, String paramKey, String sqlKey) {
        if (StringUtils.isBlank(paramKey) || inParam.isEmpty() || !inParam.containsKey(paramKey)) {
            return;
        }
        where.append(_and).append(sqlKey).append(_like).append(_wen).append(_blank);
        array.add(inParam.getString(paramKey));
    }

    public void eq(StringBuilder where, JsonArray array, JSONObject inParam, String paramKey, String sqlKey, Class clazz) {
        if (StringUtils.isBlank(paramKey) || inParam.isEmpty() || clazz == null || !inParam.containsKey(paramKey) || StringUtils.isBlank(sqlKey)) {
            return;
        }
        where.append(_and).append(sqlKey).append(_eq).append(_wen).append(_blank);
        if (Integer.class.equals(clazz)) {
            array.add(inParam.getInteger(paramKey));
        } else if (String.class.equals(clazz)) {
            array.add(inParam.getString(paramKey));
        }
    }
}