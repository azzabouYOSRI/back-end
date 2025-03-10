package com.yosri.defensy.backend.modules.ingestion.service;

import com.yosri.defensy.backend.modules.ingestion.event.CsvStorageFailedEvent;
import com.yosri.defensy.backend.modules.ingestion.repository.CsvIngestionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CsvStorageServiceTest {

    @Mock
    private CsvIngestionRepository repository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private CsvStorageServiceImpl storageService;

    // Using actual project directories instead of TempDir
    private final Path processingDir = Paths.get("csv-processing");
    private final Path processedDir = Paths.get("csv-processed");
    private final Path failedStorageDir = Paths.get("csv-failed-storing");

    private Path validCsvFile;
    private Path failedCsvFile;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        storageService = new CsvStorageServiceImpl(repository, eventPublisher);

        // Ensure test directories exist
        Files.createDirectories(processingDir);
        Files.createDirectories(processedDir);
        Files.createDirectories(failedStorageDir);

        // Generate unique test CSV filenames
        validCsvFile = processingDir.resolve("valid_" + UUID.randomUUID() + ".csv");
        failedCsvFile = processingDir.resolve("failed_" + UUID.randomUUID() + ".csv");

        // Generate test CSV files
        generateTestCsvFile(validCsvFile, List.of("eventId,ip,eventType", "1,192.168.1.100,Login_failure"));
        generateTestCsvFile(failedCsvFile, List.of("eventId,ip,eventType", "2,192.168.1.101,File_access"));
    }

    @AfterEach
    void tearDown() throws IOException {
        // Ensure processing directory is clean but do not delete processed/failed files
        Files.list(processingDir).forEach(path -> {
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    void shouldStoreCsvDataSuccessfully() {
        List<Map<String, String>> csvData = List.of(
                Map.of("eventId", "1", "ip", "192.168.1.100", "eventType", "Login_failure")
        );

        storageService.storeCsvData(csvData, validCsvFile);

        verify(repository, times(1)).saveAll(csvData);
        verify(eventPublisher, never()).publishEvent(any(CsvStorageFailedEvent.class));

        // ✅ Check if the file moved to "processed"
        Path expectedProcessedPath = processedDir.resolve(validCsvFile.getFileName());
        assertTrue(Files.exists(expectedProcessedPath), "✅ Processed file should be moved to csv-processed!");
    }

    @Test
    void shouldMoveFileToFailedStorageOnFailure() {
        List<Map<String, String>> csvData = List.of(
                Map.of("eventId", "2", "ip", "192.168.1.101", "eventType", "File_access")
        );

        doThrow(new RuntimeException("Elasticsearch is down")).when(repository).saveAll(csvData);

        storageService.storeCsvData(csvData, failedCsvFile);

        verify(repository, times(1)).saveAll(csvData);
        Path expectedFailedPath = failedStorageDir.resolve(failedCsvFile.getFileName());
        assertTrue(Files.exists(expectedFailedPath), "❌ Failed file should be moved to csv-failed-storing!");
        verify(eventPublisher, times(1)).publishEvent(any(CsvStorageFailedEvent.class));
    }

@Test
void shouldNotMoveNonExistentFileOnFailure() {
    List<Map<String, String>> csvData = List.of(
            Map.of("eventId", "3", "ip", "192.168.1.102", "eventType", "Failed_login")
    );

    doThrow(new RuntimeException("Storage failure")).when(repository).saveAll(csvData);

    // Act: Try storing a file that doesn’t exist in processing
    storageService.storeCsvData(csvData, failedCsvFile);

    // Assert: Verify that repository tried saving the data
    verify(repository, times(1)).saveAll(csvData);

    // ✅ Check the failed file **should be moved** to `csv-failed-storing/`
    Path expectedFailedPath = failedStorageDir.resolve(failedCsvFile.getFileName());
    assertTrue(Files.exists(expectedFailedPath), "✅ Failed file should be in csv-failed-storing!");

    // ✅ Ensure the failure event was published
    verify(eventPublisher, times(1)).publishEvent(any(CsvStorageFailedEvent.class));
}

    private void generateTestCsvFile(Path filePath, List<String> lines) throws IOException {
        Files.createFile(filePath);
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }
}
