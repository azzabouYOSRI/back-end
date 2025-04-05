//package com.yosri.defensy.backend.modules.ingestion.service;
//
//import com.yosri.defensy.backend.modules.ingestion.domain.CsvIngestionRecord;
//import com.yosri.defensy.backend.modules.ingestion.event.CsvStorageFailedEvent;
//import com.yosri.defensy.backend.modules.ingestion.event.CsvStoredEvent;
//import com.yosri.defensy.backend.modules.ingestion.repository.CsvIngestionMongoRepository;
//import com.yosri.defensy.backend.modules.ingestion.repository.CsvIngestionElasticsearchRepository;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.context.ApplicationEventPublisher;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.*;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//
//
//// Java
//@ExtendWith(MockitoExtension.class)
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//class CsvStorageServiceTest {
//
//    private static final Logger log = LoggerFactory.getLogger(CsvStorageServiceTest.class);
//    @Mock
//    private CsvIngestionMongoRepository mongoRepository;
//
//    @Mock
//    private CsvIngestionElasticsearchRepository elasticsearchRepository;
//
//    @Mock
//    private ApplicationEventPublisher eventPublisher;
//
//    @InjectMocks
//    private CsvStorageServiceImpl storageService;
//
//    private final Path processingDir = Paths.get("csv-processing");
//    private final Path processedDir = Paths.get("csv-processed");
//    private final Path failedStorageDir = Paths.get("csv-failed-storing");
//
//    private Path validCsvFile;
//    private Path failedCsvFile;
//
//    @BeforeAll
//    void setup() throws IOException {
//        Files.createDirectories(processingDir);
//        Files.createDirectories(processedDir);
//        Files.createDirectories(failedStorageDir);
//    }
//
//    @BeforeEach
//    void generateTestFiles() throws IOException {
//        // Clean up all directories before creating test files
//        cleanDirectory(processingDir);
//        cleanDirectory(processedDir);
//        cleanDirectory(failedStorageDir);
//
//        // Create test files with unique names in processing directory
//        validCsvFile = processingDir.resolve("valid_" + UUID.randomUUID() + ".csv");
//        failedCsvFile = processingDir.resolve("failed_" + UUID.randomUUID() + ".csv");
//
//        // Write test content
//        Files.writeString(validCsvFile, "eventId,ip,eventType\n1,192.168.1.100,Login\n", StandardCharsets.UTF_8);
//        Files.writeString(failedCsvFile, "eventId,ip,eventType\n2,192.168.1.101,File_access\n", StandardCharsets.UTF_8);
//    }
//
//    @AfterEach
//    void cleanUp() {
//        cleanDirectory(processingDir);
//        cleanDirectory(processedDir);
//        cleanDirectory(failedStorageDir);
//    }
//
//    private void cleanDirectory(Path dir) {
//        if (Files.exists(dir)) {
//            try (var paths = Files.list(dir)) {
//                paths.forEach(path -> {
//                    try {
//                        Files.deleteIfExists(path);
//                    } catch (IOException e) {
//                        log.error("Failed to delete file: {}", path, e);
//                    }
//                });
//            } catch (IOException e) {
//                log.error("Failed to list files for cleanup: {}", dir, e);
//            }
//        }
//    }
//
//    @AfterAll
//    void teardown() throws IOException {
//        // Optionally list files or perform additional teardown steps
//        try (var paths = Files.list(processedDir)) {
//            paths.forEach(path -> log.info("Archived file retained: {}", path));
//        }
//    }
//
//    @Test
//    void shouldStoreCsvDataSuccessfully() {
//        List<Map<String, String>> csvData = List.of(
//            Map.of("eventId", "1", "ip", "192.168.1.100", "eventType", "Login_failure")
//        );
//
//        storageService.storeCsvData(csvData, validCsvFile);
//
//        verify(mongoRepository, times(1)).saveAll(any());
//        verify(elasticsearchRepository, times(1)).saveAll(any());
//        verify(eventPublisher, times(1)).publishEvent(any(CsvStoredEvent.class));
//
//        Path expectedProcessedPath = processedDir.resolve(validCsvFile.getFileName());
//        assertTrue(Files.exists(expectedProcessedPath), "Processed file should be moved to csv-processed!");
//    }
//
//    // Java
//@Test
//void shouldMoveFileToFailedStorageOnFailure() {
//    List<Map<String, String>> csvData = List.of(
//        Map.of("eventId", "2", "ip", "192.168.1.101", "eventType", "File_access")
//    );
//
//    // Use a generic matcher to correctly match List<CsvIngestionRecord>
//    when(mongoRepository.saveAll(Mockito.<CsvIngestionRecord>anyList()))
//        .thenThrow(new RuntimeException("MongoDB connection failed"));
//
//    storageService.storeCsvData(csvData, failedCsvFile);
//
//    verify(mongoRepository).saveAll(Mockito.<CsvIngestionRecord>anyList());
//    verify(elasticsearchRepository, never()).saveAll(any());
//    verify(eventPublisher, never()).publishEvent(any(CsvStoredEvent.class));
//    verify(eventPublisher).publishEvent(any(CsvStorageFailedEvent.class));
//
//    Path expectedFailedPath = failedStorageDir.resolve(failedCsvFile.getFileName());
//    assertTrue(Files.exists(expectedFailedPath), "File should be moved to failed-storing directory");
//    assertFalse(Files.exists(processedDir.resolve(failedCsvFile.getFileName())), "File should not be in processed directory");
//}
//
//    @Test
//    void shouldProcessMultipleFilesConcurrently() throws InterruptedException {
//        try (ExecutorService executor = Executors.newFixedThreadPool(3)) {
//            Runnable task1 = () -> storageService.storeCsvData(List.of(
//                Map.of("eventId", "6", "ip", "192.168.1.106", "eventType", "SQL_injection")
//            ), validCsvFile);
//
//            Runnable task2 = () -> storageService.storeCsvData(List.of(
//                Map.of("eventId", "7", "ip", "192.168.1.107", "eventType", "Malware_Detected")
//            ), failedCsvFile);
//
//            executor.submit(task1);
//            executor.submit(task2);
//            executor.shutdown();
//            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
//                log.warn("ExecutorService did not shut down properly!");
//            }
//        }
//
//        Path processedFile1 = processedDir.resolve(validCsvFile.getFileName());
//        Path failedFile1 = failedStorageDir.resolve(failedCsvFile.getFileName());
//
//        assertTrue(Files.exists(processedFile1) || Files.exists(failedFile1),
//            "One file should be processed successfully, the other should fail!");
//    }
//}
//
