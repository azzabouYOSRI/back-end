// Java
package com.yosri.defensy.backend.modules.etl.listener;

import com.yosri.defensy.backend.modules.etl.service.EtlProcessingService;
import com.yosri.defensy.backend.modules.ingestion.event.CsvStoredEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EtlEventListener {

    private final EtlProcessingService etlProcessingService;

    public EtlEventListener(EtlProcessingService etlProcessingService) {
        this.etlProcessingService = etlProcessingService;
    }

    /**
     * Listens for CsvStoredEvent and triggers ETL processing asynchronously.
     * Using @Order to run after storage operations.
     *
     * @param event The event containing stored raw data.
     */
    @Async
    @EventListener
    @Order(Ordered.LOWEST_PRECEDENCE)
    public void handleCsvStoredEvent(CsvStoredEvent event) {
        if (event == null || event.getStoredData() == null || event.getStoredData().isEmpty()) {
            log.warn("⚠️ Empty event received, skipping ETL processing");
            return;
        }
        log.info("🟢 ETL Processing triggered for {} records.", event.getStoredData().size());
        try {
            etlProcessingService.processEtl(event);
        } catch (Exception e) {
            log.error("❌ ETL Processing failed: {}", e.getMessage(), e);
            log.debug("ETL Processing exception details:", e);
        }
    }
}
