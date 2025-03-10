package com.yosri.defensy.backend.modules.ingestion.service;

import com.yosri.defensy.backend.modules.ingestion.event.CsvStorageFailedEvent;
import com.yosri.defensy.backend.modules.ingestion.repository.CsvIngestionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Map;

/**
 * ✅ CSV Storage Service Implementation
 * - Moves successfully stored files from `csv-processing/` to `csv-processed/`
 * - Moves failed storage files to `csv-failed-storing/`
 */
@Slf4j
@Service
public class CsvStorageServiceImpl implements CsvStorageService {

    private static final Path PROCESSING_DIR = Paths.get("csv-processing");
    private static final Path PROCESSED_DIR = Paths.get("csv-processed");
    private static final Path FAILED_STORAGE_DIR = Paths.get("csv-failed-storing");
    private final CsvIngestionRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public CsvStorageServiceImpl(CsvIngestionRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
        createDirectories();
    }

    @Override
    public void storeCsvData(List<Map<String, String>> csvData, Path originalFilePath) {
        log.info("📦 Storing CSV data into Elasticsearch for file: {}", originalFilePath);
        log.info("📊 Parsed data: {} records", csvData != null ? csvData.size() : 0);

        try {
            if (csvData == null || csvData.isEmpty()) {
                log.warn("⚠️ No data to store for file: {}", originalFilePath);
                moveFile(originalFilePath, FAILED_STORAGE_DIR);
                return;
            }
            if (!csvData.isEmpty()) {
                repository.saveAll(csvData);
            } else {
                log.warn("⚠️ No valid data found in file: {}", originalFilePath);
            }
            log.info("✅ Successfully stored CSV data.");

            // Move file to processed folder on success
            Path processedFile = moveFile(originalFilePath, PROCESSED_DIR);
            if (processedFile != null) {
                log.info("✅ File moved to processed: {}", processedFile);
            }

        } catch (Exception e) {
            log.error("❌ Storage failed for file {}: {}", originalFilePath, e.getMessage());
            if (!Files.exists(originalFilePath)) {
                log.error("❌ Original file not found, skipping move: {}", originalFilePath);
                return;
            }
            handleFailedStorage(originalFilePath, e.getMessage(), csvData);
        }
    }
    @Override
    public void handleFailedStorage(Path filePath, String errorMessage, List<Map<String, String>> failedData) {
        Path movedPath = moveFile(filePath, FAILED_STORAGE_DIR);
        eventPublisher.publishEvent(new CsvStorageFailedEvent(
                this,
                failedData,
                errorMessage,
                movedPath != null ? movedPath.toString() : filePath.toString()
        ));
    }

    private void createDirectories() {
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

@Override
public Path moveFailedFile(Path filePath) {
    if (!Files.exists(filePath)) {
        log.error("❌ File does not exist, cannot move: {}", filePath);
        log.warn("⚠️ Skipping move for non-existent file: {}", filePath);
        return null;
    }

    try {
        Path failedPath = FAILED_STORAGE_DIR.resolve(filePath.getFileName());
        Files.move(filePath, failedPath, StandardCopyOption.REPLACE_EXISTING);
        log.info("❌ Moved failed storing CSV to '{}': {}", FAILED_STORAGE_DIR, failedPath);
        return failedPath;
    } catch (IOException e) {
        log.error("❌ Critical error: Failed to move '{}' to '{}'", filePath, FAILED_STORAGE_DIR, e);
        return null;
    }
}


}
