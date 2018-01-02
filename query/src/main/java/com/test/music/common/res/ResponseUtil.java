package com.test.music.common.res;

import com.alibaba.fastjson.JSONObject;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;

public final class ResponseUtil {
    /**
     * 200: OK. 一切正常。
     * 201: POST 请求创建资源成功， Location 头包含URL指向这个新资源。
     * 204: 请求成功，应答不包含body内容（比如 DELETE 请求）。
     * 304: 资源未更改。你可以使用缓存版本。
     * 400: 错误请求。这通常是用户侧的错误，比如无效的JSON数据，无效动作参数，等等。
     * 401: 认证失败。
     * 403: 认证用户不允许访问该API终端。
     * 404: 请求资源不存在。
     * 405: 方法未许可。请检查 Allow 头中许可的 HTTP 方法。
     * 415: 不支持的媒体类型。请求内容类型或版本号无效。
     * 422: 数据验证失败（比如对于一个 POST 请求）。请检查应答body中的错误详细描述。
     * 429: 请求过多。请求因超出速率限制而被拒绝。
     * 500: 内部错误。这通常是服务器程序内部错误。
     */

    private final static int STATUS_200 = 200;
    private final static int STATUS_201 = 201;
    private final static int STATUS_204 = 204;
    private final static int STATUS_400 = 400;
    private final static int STATUS_401 = 401;
    private final static int STATUS_403 = 403;
    private final static int STATUS_404 = 404;
    private final static int STATUS_422 = 422;
    private final static int STATUS_500 = 500;
    private final static String MSG_500 = "内部错误";

    public static void ok(RoutingContext rc) {
        end(rc, STATUS_200);
    }

    public static void ok(RoutingContext rc, JsonObject obj) {
        end(rc, STATUS_200, obj.toString());
    }

    public static void ok(RoutingContext rc, JSONObject obj) {
        end(rc, STATUS_200, obj.toJSONString());
    }

    public static void err(RoutingContext rc) {
        end(rc, STATUS_500, MSG_500);
    }

    public static void err(RoutingContext rc, String msg) {
        end(rc, STATUS_500, msg);
    }

    public static void err(RoutingContext rc, Throwable e) {
        end(rc, STATUS_500, e.getCause().getMessage());
    }

    private static void end(RoutingContext rc, int code) {
        end(rc, code);
    }

    private static void end(RoutingContext rc, int code, String msg) {
        if (StringUtils.isNoneBlank(msg)) {
            rc.response().setStatusCode(code).end(msg);
        } else {
            rc.response().setStatusCode(code).end();
        }
    }

}