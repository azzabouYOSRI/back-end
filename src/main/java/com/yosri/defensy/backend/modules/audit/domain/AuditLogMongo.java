package com.yosri.defensy.backend.modules.audit.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@org.springframework.data.mongodb.core.mapping.Document(collection = "audit_logs")
@Document(indexName = "audit_logs")
public class AuditLogMongo {

    @Id
    private String id;

    private String eventType;
    private String entityId;
    private String entityType;
    private String message;
    private LocalDateTime timestamp;
}
