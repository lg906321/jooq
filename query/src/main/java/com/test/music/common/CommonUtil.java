package com.test.music.common;

import com.alibaba.fastjson.JSONObject;
import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public final class CommonUtil {

    public static final String EVENT_FORMAT = "%s:%s";
    private final static DeliveryOptions DELIVERY_OPTIONS = new DeliveryOptions();

    static {
        DELIVERY_OPTIONS.setCodecName(JSONObject.class.getName());
    }

    private static final String KEY_PAGE     = "page";
    private static final String KEY_SIZE     = "size";
    private static final String DEFAULT_PAGE = "1";
    private static final String DEFAULT_SIZE = "10";

    public static JSONObject convert(RoutingContext rc) {
        JSONObject jsonObject = new JSONObject();
        MultiMap params = rc.request().params();
        if (params.size() > 0) {
            for (String key : params.names()) {
                jsonObject.put(key, params.get(key));
            }
        }
        return jsonObject;
    }

    /**
     * 获取get请求的参数并且设置page(1)和size(10)的默认值
     *
     * @param rc
     * @return
     */
    public static JSONObject defaultGet(RoutingContext rc) {
        JSONObject params = convert(rc);
        String page = params.getString(KEY_PAGE);

        if (page == null || page.compareTo(DEFAULT_PAGE) < 0) {
            params.put(KEY_PAGE, DEFAULT_PAGE);
        }
        String size = params.getString(KEY_SIZE);
        if (size == null || size.compareTo(DEFAULT_SIZE) < 0) {
            params.put(KEY_SIZE, DEFAULT_SIZE);
        }
        return params;
    }

    public static String eventFormat(Class clazz, String str) {
        return String.format(EVENT_FORMAT, clazz.getName(), str);
    }

    public static DeliveryOptions deliveryOptions() {
        return DELIVERY_OPTIONS;
    }
}