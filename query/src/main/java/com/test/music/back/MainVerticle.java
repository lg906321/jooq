package com.test.music.back;

import com.alibaba.fastjson.JSONObject;
import com.test.music.db.DBVerticle;
import com.test.music.util.ReadFileUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainVerticle extends AbstractVerticle {

    static final String CLASS_NAME = MainVerticle.class.getName();
    static       Logger LOGGER     = LoggerFactory.getLogger(CLASS_NAME);

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        ReadFileUtil.asyncReadJsonFile(vertx, "db.json")
                .compose(readRes -> Future.<String>future(db -> vertx.deployVerticle(new DBVerticle(readRes), db)))
                .setHandler(res -> {
                    if (res.succeeded()) {

                        startFuture.complete();
                        LOGGER.info("succ");
                    } else {
                        Throwable cause = res.cause();
                        cause.printStackTrace();
                        startFuture.fail(cause.getMessage());
                        LOGGER.info("err" + cause.getMessage());
                    }
                });

        Router router = Router.router(vertx);
        router.route("/*").handler(BodyHandler.create());

        router.route("/*").handler(this::serCharsetEncoding);

        router.route("/user").handler(rt -> {
            LOGGER.info("user...");
            rt.response().end("Hello World中国!!" +
                    new SimpleDateFormat("yyyyMMDD HH:mm:ss").format(new Date())
            );
        });
        router.route("/err").handler(rt -> {
            LOGGER.info("user...");
            int i = 1/0;
            rt.response().end("Hello World!!" +
                    new SimpleDateFormat("yyyyMMDD HH:mm:ss").format(new Date())
            );
        });
//        router.route().handler(this::failHandler);

        router.route("/*").failureHandler(this::failHandler);

        vertx.createHttpServer().requestHandler(router::accept).listen(8081);
        LOGGER.info("listener 8081 ");
    }

    private void serCharsetEncoding(RoutingContext routingContext) {
        routingContext.response().putHeader("Content-Type:","text/html;charset=UTF-8");
        routingContext.next();
    }

    void failHandler(RoutingContext rt) {
        LOGGER.info("failHandler...");
        MultiMap headers = rt.request().headers();
//        headers.entries().forEach(System.out::println);

        LOGGER.info(JSONObject.toJSONString(headers));
        LOGGER.info("Content-Type:" + headers.get("Content-Type"));
        rt.response().putHeader("Content-Type:","text/html;charset=UTF-8");
        rt.response().end("Global：" + new SimpleDateFormat("yyyyMMDD HH:mm:ss").format(new Date()));
    }

}