package com.yosri.defensy.backend.modules.correlation.listener;

import com.yosri.defensy.backend.modules.correlation.service.CorrelationService;
import com.yosri.defensy.backend.modules.ingestion.event.CsvStoredEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * ✅ Correlation Event Listener
 * - Listens for CSV stored event.
 * - Triggers correlation processing via CorrelationService.
 */
@Slf4j
@Component
public class CorrelationEventListener {

    private final CorrelationService correlationService;

    public CorrelationEventListener(CorrelationService correlationService) {
        this.correlationService = correlationService;
    }

    /**
     * ✅ Listens for `CsvStoredEvent` and triggers correlation.
     *
     * @param event The event containing stored CSV data.
     */
    @EventListener
    public void handleCsvStoredEvent(CsvStoredEvent event) {
        List<Map<String, String>> storedData = event.getStoredData();

        log.info("🔍 Correlation triggered for {} records", storedData.size());

        correlationService.processCorrelation(storedData);
    }
}
