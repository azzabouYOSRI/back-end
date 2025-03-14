package com.yosri.defensy.backend.modules.audit.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(indexName = "audit_logs")
public class AuditLogElastic {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String eventType;

    @Field(type = FieldType.Keyword)
    private String entityId;

    @Field(type = FieldType.Keyword)
    private String entityType;

    @Field(type = FieldType.Text)
    private String message;

    @Field(type = FieldType.Date)
    private Instant timestamp;
}
