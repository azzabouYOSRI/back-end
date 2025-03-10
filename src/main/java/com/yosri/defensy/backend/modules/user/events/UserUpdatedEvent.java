package com.yosri.defensy.backend.modules.user.events;

public class UserUpdatedEvent {
    private final String userId;
    private final String username;
    private final String email;
    private final boolean isActive;

    public UserUpdatedEvent(String userId, String username, String email, boolean isActive) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.isActive = isActive;
    }

    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public boolean isActive() { return isActive; }
}
