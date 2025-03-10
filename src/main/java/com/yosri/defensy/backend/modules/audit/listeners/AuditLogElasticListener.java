package com.yosri.defensy.backend.modules.audit.listeners;


import com.yosri.defensy.backend.modules.audit.application.AuditLogElasticService;
import com.yosri.defensy.backend.modules.audit.domain.AuditLogElastic;
import com.yosri.defensy.backend.modules.user.events.UserCreatedEvent;
import com.yosri.defensy.backend.modules.user.events.UserRetrievedAllEvent;
import com.yosri.defensy.backend.modules.user.events.UserRetrievedEvent;
import com.yosri.defensy.backend.modules.user.events.UserUpdatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j // ✅ Enables SLF4J Logger
@Component
public class AuditLogElasticListener {
    private final AuditLogElasticService auditLogElasticService;

    public AuditLogElasticListener(AuditLogElasticService auditLogElasticService) {
        this.auditLogElasticService = auditLogElasticService;
    }

    @EventListener
    public void handleUserCreated(UserCreatedEvent event) {
        AuditLogElastic logEntry = buildAuditLog("USER_CREATED", event.getUserId(), "User",
                "User created: " + event.getUsername());

        saveAndLog(logEntry);
    }

    @EventListener
    public void handleUserUpdated(UserUpdatedEvent event) {
        AuditLogElastic logEntry = buildAuditLog("USER_UPDATED", event.getUserId(), "User",
                "User updated: " + event.getUsername());

        saveAndLog(logEntry);
    }

    @EventListener
    public void handleUserRetrieved(UserRetrievedEvent event) {
        AuditLogElastic logEntry = buildAuditLog("USER_RETRIEVED", event.getUserId(), "User",
                "User data retrieved from Elasticsearch");

        saveAndLog(logEntry);
    }

    /**
     * Handles event when all users are retrieved
     */
    @EventListener
    public void handleUserRetrievedAll(UserRetrievedAllEvent event) {
        AuditLogElastic logEntry = buildAuditLog("ALL_USERS_RETRIEVED", null, "User",
                "All users retrieved from Elasticsearch");

        saveAndLog(logEntry);
    }

    /**
     * ✅ Extracted method to build `AuditLogElastic` object.
     */
    private AuditLogElastic buildAuditLog(String eventType, String entityId, String entityType, String message) {
        return AuditLogElastic.builder()
                .eventType(eventType)
                .entityId(entityId)
                .entityType(entityType)
                .message(message)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * ✅ Extracted method to log & save the audit log.
     */
    private void saveAndLog(AuditLogElastic logEntry) {
        log.info("🔍 [AUDIT] Event: {}, Entity: {}, ID: {}, Message: {}",
                logEntry.getEventType(), logEntry.getEntityType(), logEntry.getEntityId(), logEntry.getMessage());

        auditLogElasticService.saveAuditLog(logEntry);
    }
}
