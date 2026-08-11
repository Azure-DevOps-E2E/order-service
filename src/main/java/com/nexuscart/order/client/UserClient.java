package com.nexuscart.order.client;

public interface UserClient {

    UserInfo getUser(String userId, String requestId);

    record UserInfo(String id, String name, String email) {
    }
}
