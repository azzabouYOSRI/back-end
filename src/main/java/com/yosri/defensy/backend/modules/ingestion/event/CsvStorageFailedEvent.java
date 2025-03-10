package com.yosri.defensy.backend.modules.ingestion.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.Map;

/**
 * ✅ Event triggered when CSV storage fails.
 * - Notifies SOC Analysts via the dedicated notification module.
 */
@Getter
public class CsvStorageFailedEvent extends ApplicationEvent {
    private final List<Map<String, String>> failedData;
    private final String errorMessage;
    private final String failedFilePath;

    public CsvStorageFailedEvent(Object source, List<Map<String, String>> failedData, String errorMessage, String failedFilePath) {
        super(source);
        this.failedData = failedData;
        this.errorMessage = errorMessage;
        this.failedFilePath = failedFilePath;
    }

    public String getFailedFilePath() {
        return failedFilePath;
    }
}
