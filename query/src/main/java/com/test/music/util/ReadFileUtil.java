package com.test.music.util;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ReadFileUtil extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReadFileUtil.class.getName());

    @Override
    public void start() throws Exception {
        super.start();
    }

    public static Future<JsonObject> asyncReadJsonFile(Vertx vertx, String filePath) {
        Future<JsonObject> jsonFuture = Future.future();
        if (StringUtils.isBlank(filePath)) {
            LOGGER.error("异步读取JSON文件[" + filePath + "]内容为空！！！");
            jsonFuture.fail("要读取的文件路径为空");
        }
        Future<Buffer> buf = Future.future();
        vertx.fileSystem().readFile(filePath, buf);
        buf.setHandler(ar -> {
            if (ar.succeeded()) {
                jsonFuture.complete(new JsonObject(ar.result()));
            } else {
                jsonFuture.fail(ar.cause());
            }
        });
        return jsonFuture;
    }
}
