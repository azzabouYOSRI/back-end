package com.yosri.defensy.backend.modules.ingestion.service;

import com.yosri.defensy.backend.modules.ingestion.event.CsvFileDetectedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class CsvFileWatcherService {
    private static final String INBOX_DIR = "csv-inbox";
    private static final String PROCESSING_DIR = "csv-processing";
    private static final String PROCESSED_DIR = "csv-processed";
    private static final String FAILED_DIR = "csv-failed";

    private final ApplicationEventPublisher eventPublisher;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public CsvFileWatcherService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        createDirectories(); // Ensure required directories exist
        startWatcher(); // Start watching for new files
    }

    private void startWatcher() {
        executorService.submit(this::watchFolder);
    }

    private void watchFolder() {
        Path inboxPath = Paths.get(INBOX_DIR);

        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            inboxPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
            log.info("📂 Watching directory for new CSV files: {}", INBOX_DIR);

            while (!Thread.currentThread().isInterrupted()) {
                WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    Path filePath = inboxPath.resolve((Path) event.context());
                    if (filePath.toString().endsWith(".csv")) {
                        log.info("🆕 New CSV file detected: {}", filePath);
                        processFile(filePath);
                    }
                }
                key.reset();
            }
        } catch (IOException | InterruptedException e) {
            log.error("❌ Error watching folder: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private void processFile(Path filePath) {
        Path processingPath = Paths.get(PROCESSING_DIR, filePath.getFileName().toString());
        try {
            Files.move(filePath, processingPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("📂 Moved to processing folder: {}", processingPath);

            // Publish an event for processing
            eventPublisher.publishEvent(new CsvFileDetectedEvent(this, processingPath.toString()));

        } catch (IOException e) {
            log.error("❌ Error moving file: {}", e.getMessage());
            moveToFailed(filePath);
        }
    }

    public void moveToProcessed(Path filePath) {
        try {
            Path processedPath = Paths.get(PROCESSED_DIR, filePath.getFileName().toString());
            Files.move(filePath, processedPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("✅ Moved file to processed folder: {}", processedPath);
        } catch (IOException e) {
            log.error("❌ Error moving file to processed folder: {}", e.getMessage());
            moveToFailed(filePath);
        }
    }

    private void moveToFailed(Path filePath) {
        try {
            Path failedPath = Paths.get(FAILED_DIR, filePath.getFileName().toString());
            Files.move(filePath, failedPath, StandardCopyOption.REPLACE_EXISTING);
            log.error("🚨 Moved file to failed folder: {}", failedPath);
        } catch (IOException ex) {
            log.error("❌ Critical Error: Unable to move file to failed directory: {}", ex.getMessage());
        }
    }

    private void createDirectories() {
        try {
            Files.createDirectories(Paths.get(INBOX_DIR));
            Files.createDirectories(Paths.get(PROCESSING_DIR));
            Files.createDirectories(Paths.get(PROCESSED_DIR));
            Files.createDirectories(Paths.get(FAILED_DIR));
        } catch (IOException e) {
            log.error("❌ Error creating directories: {}", e.getMessage());
        }
    }

    @PreDestroy
    public void shutdown() {
        log.info("🛑 Shutting down CSV File Watcher...");
        executorService.shutdownNow();
    }
}
