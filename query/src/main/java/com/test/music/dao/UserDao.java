package com.test.music.dao;

import com.alibaba.fastjson.JSONObject;
import com.test.music.db.DBVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserDao extends AbstractVerticle {

    JDBCClient jdbcClient = null;
    private Logger logger = LoggerFactory.getLogger(UserDao.class);

    @Override
    public void start() throws Exception {
        jdbcClient = DBVerticle.getClient();
        logger.info("UserDao init...");
    }

    public Future<Integer> queryTotalRecordUserList(JSONObject param) {
        logger.debug("queryTotalRecordUserList");
        JsonArray params = new JsonArray().add(Integer.valueOf(param.getString("page")))
                .add(Integer.valueOf(param.getString("size")));
        Future<Integer> retFuture = Future.future();

        String sql = "select COUNT(*) from 1artist";
        Future<SQLConnection> connFuture = Future.future();
        jdbcClient.getConnection(connFuture.completer());
        connFuture.compose(ar ->
                Future.<ResultSet>future(rs -> ar.query(sql, rs.completer()))
        ).compose(
                ar -> Future.<Integer>future(rs -> {
                    JsonArray objects = ar.getResults().get(0);
                    Integer integer = objects.getInteger(0);
                    rs.complete(integer);
                })
        ).setHandler(rs -> {
            if (rs.succeeded()) {
                retFuture.complete(rs.result());
            } else {
                retFuture.fail(rs.cause());
            }
        });
        return retFuture;
    }

    public Future<List<JsonObject>> queryUserList(JSONObject param) {
        JsonArray params = new JsonArray().add(param.getInteger("page")).add(param.getInteger("size"));
        return Future.<List<JsonObject>>future(ar ->
                jdbcClient.getConnection(getConnAR -> {
                    if (getConnAR.succeeded()) {
                        SQLConnection conn = getConnAR.result();
                        conn.queryWithParams("select * from artist limit ?,?", params, queryAr -> {
                            if (queryAr.succeeded()) {
                                List<JsonObject> rows = queryAr.result().getRows();
                                ar.complete(rows);
                            } else {
                                logger.error("查询失败:" + queryAr.cause().getMessage());
                                ar.fail("查询失败:" + queryAr.cause().getMessage());
                            }
                        });
                    } else {
                        logger.info("获取数据库链接失败:" + ar.cause().getMessage());
                        ar.fail("获取数据库链接失败:" + ar.cause().getMessage());
                    }
                }));

    }

}

