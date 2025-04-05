package com.yosri.defensy.backend.modules.etl.service;

import com.yosri.defensy.backend.modules.etl.domain.EtlProcessedRecord;
import com.yosri.defensy.backend.modules.etl.event.EtlProcessingFailedEvent;
import com.yosri.defensy.backend.modules.etl.rules.CorrelationRuleEngine;
import com.yosri.defensy.backend.modules.ingestion.event.CsvStoredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EtlProcessingServiceImpl implements EtlProcessingService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final ApplicationEventPublisher eventPublisher;
    private final CorrelationRuleEngine ruleEngine;

@Override
public void processEtl(CsvStoredEvent event) {
    if (event == null || event.getStoredData() == null || event.getStoredData().isEmpty()) {
        log.warn("⚠️ No data to process in event");
        return;
    }

    log.info("✅ ETL Processing started for {} records", event.getStoredData().size());
    List<EtlProcessedRecord> processedRecords = new ArrayList<>();
    int failedRecords = 0;

    for (Map<String, String> rawDataRecord : event.getStoredData()) {
        try {
            EtlProcessedRecord processedRecord = transformRecord(rawDataRecord);

            // Save individual records to Elasticsearch
            elasticsearchOperations.save(processedRecord);
            processedRecords.add(processedRecord);

            // Check for any correlation rules that might apply
            ruleEngine.findMatchingRule(rawDataRecord)
                .ifPresent(alertRule -> log.info("🚨 Correlation rule triggered: {} for record: {}", alertRule, rawDataRecord));

        } catch (Exception e) {
            failedRecords++;
            log.error("❌ Failed to process rawDataRecord: {}", rawDataRecord, e);
            // Publish event but continue processing other records
            eventPublisher.publishEvent(new EtlProcessingFailedEvent(this, rawDataRecord, e.getMessage()));
        }
    }

    if (failedRecords > 0) {
        log.warn("⚠️ ETL Processing completed with errors: {} records failed, {} records successful",
                failedRecords, processedRecords.size());
    } else {
        log.info("✅ ETL Processing Completed: {} records transformed.", processedRecords.size());
    }
}
private EtlProcessedRecord transformRecord(Map<String, String> rawRecord) {
    Map<String, String> dynamicMap = new HashMap<>(rawRecord);
    String eventType = rawRecord.getOrDefault("eventType", "unknown_event");

    // Get source timestamp if available, otherwise use current time
    long timestamp;
    try {
        if (rawRecord.containsKey("timestamp")) {
            Instant sourceTimestamp = Instant.parse(rawRecord.get("timestamp"));
            timestamp = sourceTimestamp.toEpochMilli();
        } else {
            timestamp = Instant.now().toEpochMilli();
        }
    } catch (Exception e) {
        // If timestamp parsing fails, use current time
        log.warn("⚠️ Failed to parse timestamp: {}, using current time instead", rawRecord.get("timestamp"));
        timestamp = Instant.now().toEpochMilli();
    }

    dynamicMap.put("riskLevel", classifyRiskLevel(eventType));
    dynamicMap.put("processed_timestamp", String.valueOf(Instant.now().toEpochMilli()));
    dynamicMap.put("severity", getSeverity(eventType));

    // Add additional enrichment fields
    enrichRecord(dynamicMap, eventType);

    return EtlProcessedRecord.builder()
            .dynamicAttributes(dynamicMap)
            .timestamp(timestamp)
            .build();
}
  private void enrichRecord(Map<String, String> record, String eventType) {
    // Add additional context based on event type
    if ("login_failure".equalsIgnoreCase(eventType)) {
        record.put("auth_result", "failure");
        record.put("requires_investigation", "true");
    } else if ("normal_login".equalsIgnoreCase(eventType)) {
        record.put("auth_result", "success");
    }

    // Add standard ETL fields - use epoch millis for consistency
    record.put("etl_timestamp", String.valueOf(Instant.now().toEpochMilli()));
    record.put("etl_version", "1.0");
}

    private String classifyRiskLevel(String eventType) {
        return switch (eventType.toLowerCase()) {
            case "login_failure", "unauthorized_access", "ddos_attack", "port_scan" -> "High";
            case "malware_detected", "suspicious_activity", "rdp_connection" -> "Medium";
            case "file_access", "normal_login" -> "Low";
            default -> "Unknown";
        };
    }

    /**
     * Determine security event severity based on event type.
     *
     * @param eventType The type of security event
     * @return Severity level as a string: "High", "Medium", "Low", or "Informational"
     */
    public String getSeverity(String eventType) {
        if (eventType == null) {
            return "Informational";
        }

        return switch (eventType.toLowerCase()) {
            case "login_failure", "unauthorized_access", "ddos_attack", "port_scan" -> "High";
            case "malware_detected", "suspicious_activity", "rdp_connection" -> "Medium";
            case "file_access", "normal_login" -> "Low";
            default -> "Informational";
        };
    }
}
