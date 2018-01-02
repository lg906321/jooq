package com.test.music.router;

import com.alibaba.fastjson.JSONObject;
import com.test.music.common.CommonUtil;
import com.test.music.common.ContentType;
import com.test.music.common.res.ResponseUtil;
import com.test.music.dao.UserDao;
import com.test.music.event.UserEvent;
import com.test.music.service.UserService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserRouter extends AbstractVerticle {

    private static Logger logger = LoggerFactory.getLogger(UserRouter.class);

    private Router userRouter;
    private UserService userService;

    public UserRouter(Router rootRouter) {
        userRouter = Router.router(vertx);
        userRouter.get("/users").produces(ContentType.JSON_UTF8).handler(this::getUserList);
        logger.info("UserRouter init");
        rootRouter.mountSubRouter("/user", userRouter);
    }

    private void getUserList(RoutingContext rc) {
        Vertx vertx = rc.vertx();
        EventBus eventBus = vertx.eventBus();
        logger.info("getUserList");
        final JSONObject params = CommonUtil.defaultGet(rc);
        logger.info("params:" + params.toString());
        eventBus.<JSONObject>send(UserEvent.USER_LIST, params,CommonUtil.deliveryOptions(), ar -> {
            logger.info("ar:" + ar.succeeded());
            if (ar.succeeded()) {
                logger.info("success");
                ResponseUtil.ok(rc, ar.result().body());
            } else {
                logger.error("errrrrr");
                ResponseUtil.err(rc, "sendErr");
            }
        });
    }
}
