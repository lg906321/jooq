package com.test.music.common;

import com.alibaba.fastjson.JSONObject;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

public class JSONObjectMessageCodec implements MessageCodec<JSONObject, JSONObject> {
    @Override
    public void encodeToWire(Buffer buffer, JSONObject jsonObject) {

    }

    @Override
    public JSONObject decodeFromWire(int pos, Buffer buffer) {
        return null;
    }

    @Override
    public JSONObject transform(final JSONObject jsonObject) {
        return JSONObject.parseObject(jsonObject.toJSONString());
    }

    @Override
    public String name() {
        return JSONObject.class.getName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
