package com.test.music.common;

import io.vertx.core.json.JsonObject;

public class Ret {
    private static final String KEY_CODE = "CODE";
    private static final String KEY_MSG  = "MSG";
    private static final String KEY_DATA = "DATA";

    public static String ret(int code, String msg, Object data) {
        return new JsonObject()
                .put(KEY_CODE, code)
                .put(KEY_MSG, msg)
                .put(KEY_DATA, data)
                .toString();
    }

    public static String ret(int code, String msg) {
        return ret(code, msg);
    }

    public static String err() {
        return ret(RetCons.CODE_ERR, RetCons.MSG_ERR);
    }

    public static String succ() {
        return ret(RetCons.CODE_SUCC, RetCons.MSG_SUCC);
    }

    public static String succ(Object data) {
        return ret(RetCons.CODE_SUCC, RetCons.MSG_SUCC, data);
    }
}