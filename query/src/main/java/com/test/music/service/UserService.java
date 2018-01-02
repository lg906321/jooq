package com.test.music.service;

import com.alibaba.fastjson.JSONObject;
import com.test.music.common.CommonUtil;
import com.test.music.dao.UserDao;
import com.test.music.event.UserEvent;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserService extends AbstractVerticle {

    Logger logger = LoggerFactory.getLogger(UserService.class);

    private UserDao userDao;

    public UserService(UserDao userDao){
        this.userDao = userDao;
    }

    @Override
    public void start() throws Exception {
        EventBus eventBus = vertx.eventBus();
        eventBus.consumer(UserEvent.USER_LIST, this::getUserList);
        logger.info("UserService init");
    }

    private void getUserList(Message<JSONObject> msg) {
        val retJson = new JSONObject();

        val params = msg.body();
        logger.info("getUserList:" + params.toString());
        Future<List<JsonObject>> listFuture = userDao.queryUserList(params);
        listFuture.compose(ar -> {
            retJson.put("rows",ar);
            return userDao.queryTotalRecordUserList(params);
        }).setHandler(ar -> {
            logger.debug("list:" + ar.cause());
            if(ar.succeeded()){
                retJson.put("total",ar.result());
            } else {
                retJson.clear();
                retJson.put("err" , ar.cause().toString());
            }
            msg.reply(retJson, CommonUtil.deliveryOptions());
        });
    }

}