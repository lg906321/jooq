package com.test.music;

import com.alibaba.fastjson.JSONObject;
import com.test.music.common.ContentType;
import com.test.music.common.JSONObjectMessageCodec;
import com.test.music.dao.UserDao;
import com.test.music.db.DBVerticle;
import com.test.music.router.UserRouter;
import com.test.music.service.UserService;
import com.test.music.util.ReadFileUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.VertxOptions;
import io.vertx.core.file.FileSystem;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Function;

public class MainVerticle extends AbstractVerticle {

    static final String CLASS_NAME = MainVerticle.class.getName();
    static       Logger LOGGER     = LoggerFactory.getLogger(CLASS_NAME);

    JDBCClient jdbcClient;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        vertx.eventBus().registerCodec(new JSONObjectMessageCodec());

        String path = "db.json";
        Router router = Router.router(vertx);
        router.route("/*").handler(BodyHandler.create());
        router.route("/*").handler(this::serCharsetEncoding);

        router.route("/err").handler(this::createErr);

        router.route("/*").failureHandler(this::failHandler);
        JsonObject config = config();
        LOGGER.info(config.toString());
        Integer port = config.getInteger("port", 8888);

        vertx.createHttpServer().requestHandler(router::accept).listen(port);
        UserDao userDao = new UserDao();
        ReadFileUtil.asyncReadJsonFile(vertx, path)
                .compose(res -> Future.<String>future(db -> vertx.deployVerticle(new DBVerticle(res), db)))
                .compose(res -> Future.<String>future(dao -> vertx.deployVerticle(userDao, dao)))
                .compose(res -> Future.<String>future(service -> vertx.deployVerticle(new UserService(userDao), service)))
                .compose(res -> Future.<String>future(user -> vertx.deployVerticle(new UserRouter(router), user)))
                .setHandler(res -> {
                    if (res.succeeded()) {
                        LOGGER.info("listener " + port);
                        startFuture.complete();
                        LOGGER.info("succ");
                    } else {
                        Throwable cause = res.cause();
                        cause.printStackTrace();
                        startFuture.fail(cause.getMessage());
                        LOGGER.info("err" + cause.getMessage());
                    }
                });
    }

    /**
     * 测试错误请求的响应
     *
     * @param rt
     */
    private void createErr(RoutingContext rt) {
        LOGGER.info("err...");
        int i = 1 / 0;
        rt.response().end("Hello World!!" +
                new SimpleDateFormat("yyyyMMDD HH:mm:ss").format(new Date())
        );
    }

    /**
     * 响应头添加UTF-8
     *
     * @param routingContext
     */
    private void serCharsetEncoding(RoutingContext routingContext) {
        HttpServerRequest request = routingContext.request();
        HttpServerResponse response = routingContext.response();
        LOGGER.info(request.uri());
        LOGGER.info(request.absoluteURI());
        String contextType = request.getHeader(ContentType.KEY);
        LOGGER.info(contextType);

        if (ContentType.JSON.equals(contextType) || ContentType.JSON_UTF8.equals(contextType)) {
            response.putHeader(ContentType.KEY, ContentType.JSON_UTF8);
        } else {
            response.putHeader(ContentType.KEY, ContentType.TEXT_UTF8);
        }
        routingContext.next();
    }

    /**
     * 全局的错误处理
     *
     * @param routingContext
     */
    void failHandler(RoutingContext routingContext) {
        LOGGER.info("failHandler.1..");
        MultiMap headers = routingContext.request().headers();
        Throwable failure = routingContext.failure();
        LOGGER.error(failure.getMessage());
        failure.printStackTrace();
        LOGGER.error("routingContext.failure().getMessage()：" + routingContext.failure().getMessage());
        LOGGER.info("Content-Type:" + headers.get("Content-Type"));
        routingContext.response().putHeader("Content-Type:", "text/html;charset=UTF-8");
        routingContext.response().end("Global：" + new SimpleDateFormat("yyyyMMDD HH:mm:ss").format(new Date()));
    }

}