package com.yosri.defensy.backend.modules.audit.listeners;

import com.yosri.defensy.backend.modules.audit.domain.AuditLogMongo;
import com.yosri.defensy.backend.modules.audit.repository.AuditLogMongoRepository;
import com.yosri.defensy.backend.modules.user.events.UserCreatedEvent;
import com.yosri.defensy.backend.modules.user.events.UserRetrievedAllEvent;
import com.yosri.defensy.backend.modules.user.events.UserRetrievedEvent;
import com.yosri.defensy.backend.modules.user.events.UserUpdatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class AuditLogMongoListener {

    private final AuditLogMongoRepository auditLogMongoRepository;

    public AuditLogMongoListener(AuditLogMongoRepository auditLogMongoRepository) {
        this.auditLogMongoRepository = auditLogMongoRepository;
    }

    @EventListener
    public void handleUserCreated(UserCreatedEvent event) {
        AuditLogMongo log = new AuditLogMongo();
        log.setEventType("USER_CREATED");
        log.setEntityId(event.getUserId());
        log.setEntityType("User");
        log.setMessage(String.format("User created: %s", event.getUsername()));
        log.setTimestamp(Instant.now());
        auditLogMongoRepository.save(log);
    }

    @EventListener
    public void handleUserUpdated(UserUpdatedEvent event) {
        AuditLogMongo log = new AuditLogMongo();
        log.setEventType("USER_UPDATED");
        log.setEntityId(event.getUserId());
        log.setEntityType("User");
        log.setMessage(String.format("User updated: %s", event.getUsername()));
        log.setTimestamp(Instant.now());
        auditLogMongoRepository.save(log);
    }
        @EventListener
    public void handleUserRetrieved(UserRetrievedEvent event) {
        AuditLogMongo log = new AuditLogMongo();
        log.setEventType("USER_RETRIEVED");
        log.setEntityId(event.getUserId());
        log.setEntityType("User");
        log.setMessage("User data retrieved from MongoDB");
        log.setTimestamp(Instant.now());
        auditLogMongoRepository.save(log);
    }

    @EventListener
    public void handleUserRetrievedAll(UserRetrievedAllEvent event) {
        AuditLogMongo log = new AuditLogMongo();
        log.setEventType("ALL_USERS_RETRIEVED");
        log.setEntityType("User");
        log.setMessage("All users retrieved from MongoDB");
        log.setTimestamp(Instant.now());
        auditLogMongoRepository.save(log);
    }
}
