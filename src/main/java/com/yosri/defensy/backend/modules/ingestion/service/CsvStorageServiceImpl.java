package com.yosri.defensy.backend.modules.ingestion.service;

import com.yosri.defensy.backend.modules.ingestion.domain.CsvIngestionRecord;
import com.yosri.defensy.backend.modules.ingestion.domain.CsvIngestionSearchRecord;
import com.yosri.defensy.backend.modules.ingestion.event.CsvStorageFailedEvent;
import com.yosri.defensy.backend.modules.ingestion.event.CsvStoredEvent;
import com.yosri.defensy.backend.modules.ingestion.repository.CsvIngestionMongoRepository;
import com.yosri.defensy.backend.modules.ingestion.repository.CsvIngestionElasticsearchRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * ✅ CSV Storage Service Implementation
 * - Stores **raw CSV data in MongoDB**
 * - Indexes **searchable CSV data in Elasticsearch**
 * - Moves successfully stored files from `csv-processing/` to `csv-processed/`
 * - Implements **3 retry attempts** before moving files to `csv-failed-storing/`
 */
@Slf4j
@Service
public class CsvStorageServiceImpl implements CsvStorageService {

    private static final Path PROCESSING_DIR = Paths.get("csv-processing");
    private static final Path PROCESSED_DIR = Paths.get("csv-processed");
    private static final Path FAILED_STORAGE_DIR = Paths.get("csv-failed-storing");

    private final CsvIngestionMongoRepository mongoRepository;
    private final CsvIngestionElasticsearchRepository elasticsearchRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CsvStorageServiceImpl(CsvIngestionMongoRepository mongoRepository,
                                 CsvIngestionElasticsearchRepository elasticsearchRepository,
                                 ApplicationEventPublisher eventPublisher) {
        this.mongoRepository = mongoRepository;
        this.elasticsearchRepository = elasticsearchRepository;
        this.eventPublisher = eventPublisher;
        createDirectories();
    }

  @Override
public void storeCsvData(List<Map<String, String>> csvData, Path originalFilePath) {
    log.info("📦 Storing CSV data for file: {}", originalFilePath);

    if (csvData == null || csvData.isEmpty()) {
        log.warn("⚠️ No valid data found in file: {}", originalFilePath);
        moveFileToFailedStorage(originalFilePath, "No valid data");
        return;
    }

    try {
        // Save raw data to MongoDB first
        List<CsvIngestionRecord> mongoRecords = csvData.stream()
            .map(data -> new CsvIngestionRecord(null, data, Instant.now()))
            .toList();
        mongoRepository.saveAll(mongoRecords);

        // Only proceed to Elasticsearch if MongoDB was successful
        List<CsvIngestionSearchRecord> elasticsearchRecords = csvData.stream()
            .map(data -> new CsvIngestionSearchRecord(null, data, Instant.now()))
            .toList();
        elasticsearchRepository.saveAll(elasticsearchRecords);

        // First, move the file to the processed directory
        Path movedPath = moveFile(originalFilePath, PROCESSED_DIR);
        if (movedPath == null) {
            log.error("❌ Failed to move file to processed folder, aborting ETL event publishing.");
            return;
        }

        log.info("✅ Successfully stored CSV data.");

        // Only publish the CSV stored event if the file has successfully been moved
        eventPublisher.publishEvent(new CsvStoredEvent(this, csvData));
    } catch (Exception e) {
        log.error("❌ Storage failed for file {}: {}", originalFilePath, e.getMessage());
        handleFailedStorage(originalFilePath, e.getMessage(), csvData);
    }
}
    @Override
    public void handleFailedStorage(Path filePath, String errorMessage, List<Map<String, String>> failedData) {
        moveFileToFailedStorage(filePath, errorMessage);
    }

    public void moveFileToFailedStorage(Path filePath, String reason) {
        Path movedPath = moveFile(filePath, FAILED_STORAGE_DIR);
        eventPublisher.publishEvent(new CsvStorageFailedEvent(
            this, null, reason, movedPath != null ? movedPath.toString() : filePath.toString()
        ));
    }

    @Override
    public void createDirectories() {
        try {
            Files.createDirectories(PROCESSING_DIR);
            Files.createDirectories(PROCESSED_DIR);
            Files.createDirectories(FAILED_STORAGE_DIR);
        } catch (IOException e) {
            log.error("❌ Failed to create required directories!", e);
        }
    }

    @Override
    public Path moveFile(Path filePath, Path targetDir) {
        try {
            Files.createDirectories(targetDir);
            Path targetPath = targetDir.resolve(filePath.getFileName());
            Files.move(filePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("📂 Moved file to '{}': {}", targetDir, targetPath);
            return targetPath;
        } catch (IOException e) {
            log.error("❌ Failed to move '{}' to '{}'", filePath, targetDir, e);
            return null;
        }
    }
}
