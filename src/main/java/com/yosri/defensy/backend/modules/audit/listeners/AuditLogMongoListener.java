package com.yosri.defensy.backend.modules.audit.listeners;

import com.yosri.defensy.backend.modules.audit.domain.AuditLogElastic;
import com.yosri.defensy.backend.modules.audit.repository.AuditLogElasticRepository;
import com.yosri.defensy.backend.modules.user.events.UserCreatedEvent;
import com.yosri.defensy.backend.modules.user.events.UserUpdatedEvent;
import com.yosri.defensy.backend.modules.user.events.UserDeletedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuditLogElasticListener {
    private final AuditLogElasticRepository auditLogRepository;

    public AuditLogElasticListener(AuditLogElasticRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @EventListener
    public void handleUserCreated(UserCreatedEvent event) {
        AuditLogElastic log = new AuditLogElastic();
        log.setEventType("USER_CREATED");
        log.setEntityId(event.getUserId());
        log.setEntityType("User");
        log.setMessage("User created: " + event.getUsername());
        log.setTimestamp(Instant.now());
        auditLogRepository.save(log);
    }

    @EventListener
    public void handleUserUpdated(UserUpdatedEvent event) {
        AuditLogElastic log = new AuditLogElastic();
        log.setEventType("USER_UPDATED");
        log.setEntityId(event.getUserId());
        log.setEntityType("User");
        log.setMessage("User updated: " + event.getUsername());
        log.setTimestamp(Instant.now());
        auditLogRepository.save(log);
    }

    @EventListener
    public void handleUserDeleted(UserDeletedEvent event) {
        AuditLogElastic log = new AuditLogElastic();
        log.setEventType("USER_DELETED");
        log.setEntityId(event.getUserId());
        log.setEntityType("User");
        log.setMessage("User deleted: " + event.getUserId());
        log.setTimestamp(Instant.now());
        auditLogRepository.save(log);
    }
}
