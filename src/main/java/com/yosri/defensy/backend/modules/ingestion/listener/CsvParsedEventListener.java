package com.yosri.defensy.backend.modules.ingestion.listener;

import com.yosri.defensy.backend.modules.ingestion.event.CsvParsedEvent;
import com.yosri.defensy.backend.modules.ingestion.service.CsvParserService;
import com.yosri.defensy.backend.modules.ingestion.service.CsvStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CsvParsedEventListener {

    private final CsvParserService csvParserService;
    private final CsvStorageService csvStorageService;

    @EventListener
    public void handleCsvParsedEvent(CsvParsedEvent event) {
        try {
            log.info("📄 Processing parsed CSV file: {}", event.getFilePath());
            Path filePath = Paths.get(event.getFilePath());
            
            // Parse the CSV file to get the data
            List<Map<String, String>> csvData = csvParserService.parse(filePath);
            
            // Store the parsed data
            csvStorageService.storeCsvData(csvData, filePath);
            
            log.info("✅ Successfully processed CSV file: {}", event.getFilePath());
        } catch (Exception e) {
            log.error("❌ Error processing parsed CSV file: {}", event.getFilePath(), e);
        }
    }
}
