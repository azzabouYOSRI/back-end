package com.yosri.defensy.backend.modules.user.events;

public class UserRetrievedEvent {
    private final String userId;

    public UserRetrievedEvent(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
