package com.yosri.defensy.backend.modules.user.events;

public class UserCreatedEvent {
    private final String userId;
    private final String username;
    private final String email;

    public UserCreatedEvent(String userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
    }

    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
}
