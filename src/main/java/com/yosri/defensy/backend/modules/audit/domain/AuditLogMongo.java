package com.yosri.defensy.backend.modules.audit.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "audit_logs")
public class AuditLogMongo {

    @Id
    private String id;

    private String eventType;
    private String entityId;
    private String entityType;
    private String message;
    private Instant timestamp;
}
