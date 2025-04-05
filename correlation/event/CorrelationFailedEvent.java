package com.yosri.defensy.backend.modules.correlation.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Map;

/**
 * ✅ Event triggered when correlation fails after retries.
 */
@Getter
public class CorrelationFailedEvent extends ApplicationEvent {

    private final Map<String, String> failedRecord;

    public CorrelationFailedEvent(Object source, Map<String, String> failedRecord) {
        super(source);
        this.failedRecord = failedRecord;
    }
}
