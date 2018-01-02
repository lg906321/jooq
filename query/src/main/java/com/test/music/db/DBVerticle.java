package com.test.music.db;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

import java.util.Objects;

public class DBVerticle extends AbstractVerticle {

    private static JDBCClient jdbcClient = null;
    JsonObject dbConfig = null;

    public DBVerticle(JsonObject dbConfig) {
        this.dbConfig = dbConfig;
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);
        jdbcClient = JDBCClient.createShared(vertx, dbConfig);
    }

    public static JDBCClient getClient() {
        Objects.requireNonNull(jdbcClient);
        return jdbcClient;
    }
}