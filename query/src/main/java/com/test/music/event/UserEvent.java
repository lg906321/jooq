package com.test.music.event;

import com.test.music.common.CommonUtil;

public final class UserEvent {

    public final static String USER_LIST = CommonUtil.eventFormat(UserEvent.class, "userList");

    public final static String QUERY_USER_LIST = CommonUtil.eventFormat(UserEvent.class, "queryUserList");
}