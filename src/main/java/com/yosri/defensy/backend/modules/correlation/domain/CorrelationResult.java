package com.yosri.defensy.backend.modules.correlation.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.Instant;

/**
 * ✅ Correlation Result Entity
 * - Stores correlation outcomes in Elasticsearch.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "correlation_results")
public class CorrelationResult {

    @Id
    private String id;  // Unique document ID

    private String eventId;  // ID of the ingested event (from CSV ingestion)
    private String ip;  // Involved IP (if applicable)
    private Instant timestamp;  // Event timestamp
    private String eventType;  // Type of correlation (e.g., repeated login failures)
    private boolean isAlert;  // True if it triggered an alert
    private String alertReason;  // Explanation for alert
}
