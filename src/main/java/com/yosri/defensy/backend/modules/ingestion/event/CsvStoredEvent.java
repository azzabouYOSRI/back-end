package com.yosri.defensy.backend.modules.ingestion.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import java.util.List;
import java.util.Map;

/**
 * ✅ Event triggered AFTER CSV data is successfully stored.
 * - This informs the Correlation Module to start correlation.
 */
@Getter
public class CsvStoredEvent extends ApplicationEvent {
    private final List<Map<String, String>> storedData;

    public CsvStoredEvent(Object source, List<Map<String, String>> storedData) {
        super(source);
        this.storedData = storedData;
    }
}
