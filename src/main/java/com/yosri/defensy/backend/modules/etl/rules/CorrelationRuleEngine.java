package com.yosri.defensy.backend.modules.etl.rules;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.Map;

@Component
public class CorrelationRuleEngine {
    public Optional<String> findMatchingRule(Map<String, String> attributes) {
        if ("login_failure".equals(attributes.get("eventType")) && "192.168.1.50".equals(attributes.get("ip"))) {
            return Optional.of("Brute Force Detected");
        }
        return Optional.empty();
    }

    public boolean isWithinTimeWindow(Instant eventTimestamp, Instant referenceTimestamp, int windowSeconds) {
        return Math.abs(eventTimestamp.getEpochSecond() - referenceTimestamp.getEpochSecond()) <= windowSeconds;
    }
}
