package com.yosri.defensy.backend.modules.correlation.service;

import com.yosri.defensy.backend.modules.correlation.event.CorrelationCompletedEvent;
import com.yosri.defensy.backend.modules.correlation.event.CorrelationFailedEvent;
import com.yosri.defensy.backend.modules.correlation.domain.CorrelationResult;
import com.yosri.defensy.backend.modules.correlation.repository.CorrelationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * ✅ Correlation Service Implementation
 * - Applies correlation rules to ingested data.
 * - Implements retry mechanism and event-based notifications.
 * - Dynamically stores correlation results in Elasticsearch.
 */
@Slf4j
@Service
public class CorrelationServiceImpl implements CorrelationService {

    private static final int MAX_RETRIES = 3; // 🔄 Maximum retry attempts
    private final CorrelationRepository correlationRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CorrelationServiceImpl(CorrelationRepository correlationRepository,
                                  ApplicationEventPublisher eventPublisher) {
        this.correlationRepository = correlationRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * ✅ Processes correlation for new ingested data.
     * - Iterates over all stored records and applies correlation rules.
     * - Implements retry logic for failed correlation attempts.
     *
     * @param storedData Parsed event data from CSV ingestion.
     */
    @Override
    public void processCorrelation(List<Map<String, String>> storedData) {
        log.info("🔍 Starting correlation for {} records...", storedData.size());

        for (Map<String, String> record : storedData) {
            boolean correlationSuccess = attemptCorrelation(record);

            if (!correlationSuccess) {
                log.error("❌ Correlation failed after retries for record: {}", record);
                eventPublisher.publishEvent(new CorrelationFailedEvent(this, record));
            }
        }

        log.info("✅ Correlation process completed.");
    }

    /**
     * ✅ Attempts correlation with retry handling.
     * - Uses a retry mechanism to handle transient failures.
     * - Logs success and failure cases properly.
     *
     * @param record Single ingested data record.
     * @return boolean indicating success/failure.
     */
    private boolean attemptCorrelation(Map<String, String> record) {
        int attempts = 0;

        while (attempts < MAX_RETRIES) {
            attempts++;

            try {
                CorrelationResult result = applyCorrelationRules(record);
                correlationRepository.save(result);

                // ✅ Publish correlation success event
                eventPublisher.publishEvent(new CorrelationCompletedEvent(this, result));

                log.info("✅ Correlation successful for record: {}", record);
                return true;

            } catch (Exception e) {
                log.warn("⚠️ Attempt {}/{} failed for correlation: {}", attempts, MAX_RETRIES, e.getMessage());
            }
        }

        return false; // ❌ Failed after max retries
    }

    /**
     * ✅ Applies correlation rules to a given record.
     * - Checks for blacklisted IPs or suspicious login behavior.
     *
     * @param record The stored event record.
     * @return CorrelationResult containing details.
     */
    private CorrelationResult applyCorrelationRules(Map<String, String> record) {
        String eventId = record.get("eventId");
        String ip = record.get("ip");
        String eventType = record.get("eventType");

        // 🔹 Example: Check if IP exists in a known blacklist
        boolean isBlacklisted = checkIPBlacklist(ip);
        boolean isSuspiciousLogin = checkRepeatedLoginFailures(ip, eventType);

        boolean isAlert = isBlacklisted || isSuspiciousLogin;
        String alertReason = isBlacklisted ? "Blacklisted IP detected" :
                (isSuspiciousLogin ? "Suspicious repeated logins detected" : "No correlation alert");

        return CorrelationResult.builder()
                .id(UUID.randomUUID().toString()) // Unique correlation ID
                .eventId(eventId)
                .ip(ip)
                .eventType(eventType)
                .isAlert(isAlert)
                .alertReason(alertReason)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * ✅ Checks if an IP address is blacklisted.
     * - Placeholder logic for future integration with a threat intelligence feed.
     *
     * @param ip The IP address to check.
     * @return True if the IP is flagged as malicious.
     */
    private boolean checkIPBlacklist(String ip) {
        // 🚀 Future: Integrate with a Threat Intelligence Feed
        return "192.168.1.100".equals(ip); // Example: Hardcoded blacklisted IP
    }

    /**
     * ✅ Checks for repeated failed logins from the same IP.
     * - Example rule: Flag repeated login failures as suspicious.
     *
     * @param ip        The IP address involved.
     * @param eventType The type of event.
     * @return True if an alert should be triggered.
     */
    private boolean checkRepeatedLoginFailures(String ip, String eventType) {
        // 🚀 Future: Query Elasticsearch for repeated login failures
        return "login_failure".equals(eventType); // Placeholder rule
    }
}
