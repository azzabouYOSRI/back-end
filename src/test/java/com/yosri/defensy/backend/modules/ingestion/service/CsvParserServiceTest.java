//package com.yosri.defensy.backend.modules.ingestion.service;
//
//import com.yosri.defensy.backend.modules.ingestion.event.CsvParsedEvent;
//import com.yosri.defensy.backend.modules.ingestion.event.CsvParsingFailedEvent;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Mockito;
//import org.springframework.context.ApplicationEventPublisher;
//
//import java.io.IOException;
//import java.io.UncheckedIOException;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.*;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class CsvParserServiceTest {
//
//    private CsvParserServiceImpl csvParserService;
//    private ApplicationEventPublisher eventPublisher;
//
//    private static final Path INBOX_DIR = Paths.get("csv-inbox");
//    private static final Path PROCESSING_DIR = Paths.get("csv-processing");
//    private static final Path FAILED_PARSING_DIR = Paths.get("csv-failed-parsing");
//
//    @BeforeEach
//    void setUp() throws IOException {
//        eventPublisher = mock(ApplicationEventPublisher.class);
//        csvParserService = new CsvParserServiceImpl(eventPublisher);
//
//        Files.createDirectories(INBOX_DIR);
//        Files.createDirectories(PROCESSING_DIR);
//        Files.createDirectories(FAILED_PARSING_DIR);
//    }
//
//    @Test
//    void shouldProcessValidCsvFileSuccessfully() throws IOException {
//        // Arrange: Create a valid CSV file
//        Path validCsvFile = Files.createTempFile(INBOX_DIR, "valid_", ".csv");
//        Files.write(validCsvFile, List.of("id,ip,event", "1,192.168.1.100,Login_failure"), StandardCharsets.UTF_8);
//
//        // Act: Parse the file
//        csvParserService.parseCsvFile(validCsvFile);
//
//        // Assert: File should be moved to 'csv-processing'
//        Path processedFile = PROCESSING_DIR.resolve(validCsvFile.getFileName());
//        assertTrue(Files.exists(processedFile), "Valid CSV should be moved to processing directory");
//        assertFalse(Files.exists(validCsvFile), "Original file should not exist in inbox");
//
//        // Verify event publishing
//        ArgumentCaptor<CsvParsedEvent> eventCaptor = ArgumentCaptor.forClass(CsvParsedEvent.class);
//        verify(eventPublisher).publishEvent(eventCaptor.capture());
//        CsvParsedEvent event = eventCaptor.getValue();
//        assertNotNull(event, "Parsed event should be published");
//        assertTrue(event.getFilePath().contains("csv-processing/"), "Event should reference processed file");
//    }
//
//    @Test
//    void shouldMoveMalformedCsvToFailedParsing() throws IOException {
//        // Arrange: Create a malformed CSV file
//        Path malformedCsvFile = Files.createTempFile(INBOX_DIR, "malformed_", ".csv");
//        Files.write(malformedCsvFile, List.of("invalid_content"), StandardCharsets.UTF_8);
//
//        // Act: Attempt parsing
//        csvParserService.parseCsvFile(malformedCsvFile);
//
//        // Assert: File should be moved to 'csv-failed-parsing'
//        Path failedFile = FAILED_PARSING_DIR.resolve(malformedCsvFile.getFileName());
//        assertTrue(Files.exists(failedFile), "Malformed CSV should be moved to failed parsing directory");
//        assertFalse(Files.exists(malformedCsvFile), "Original malformed file should not exist in inbox");
//
//        // Verify failure event
//        ArgumentCaptor<CsvParsingFailedEvent> eventCaptor = ArgumentCaptor.forClass(CsvParsingFailedEvent.class);
//        verify(eventPublisher).publishEvent(eventCaptor.capture());
//        CsvParsingFailedEvent event = eventCaptor.getValue();
//        assertNotNull(event, "Failed parsing event should be published");
//        assertTrue(event.getFilePath().contains("csv-failed-parsing/"), "Event should reference failed file");
//    }
//
//    @Test
//    void shouldHandleEmptyCsvFile() throws IOException {
//        // Arrange: Create an empty CSV file
//        Path emptyCsvFile = Files.createTempFile(INBOX_DIR, "empty_", ".csv");
//        Files.write(emptyCsvFile, List.of(), StandardCharsets.UTF_8);
//
//        // Act: Attempt parsing
//        csvParserService.parseCsvFile(emptyCsvFile);
//
//        // Assert: File should be moved to 'csv-failed-parsing'
//        Path failedFile = FAILED_PARSING_DIR.resolve(emptyCsvFile.getFileName());
//        assertTrue(Files.exists(failedFile), "Empty CSV should be moved to failed parsing directory");
//        assertFalse(Files.exists(emptyCsvFile), "Original empty file should not exist in inbox");
//
//        // Verify failure event
//        ArgumentCaptor<CsvParsingFailedEvent> eventCaptor = ArgumentCaptor.forClass(CsvParsingFailedEvent.class);
//        verify(eventPublisher).publishEvent(eventCaptor.capture());
//        CsvParsingFailedEvent event = eventCaptor.getValue();
//        assertNotNull(event, "Failed parsing event should be published");
//    }
//
//    @Test
//    void shouldHandleIOExceptionGracefully() throws IOException {
//        // Arrange: Spy on CsvParserServiceImpl to simulate an IOException
//        CsvParserServiceImpl spyParser = Mockito.spy(csvParserService);
//        Path faultyFile = Paths.get("nonexistent.csv");
//
//        doAnswer(invocation -> { throw new IOException("Test IOException"); })
//            .when(spyParser).parseCsvFile(any(Path.class));
//
//        // Act & Assert: Verify that an UncheckedIOException is thrown
//        assertThrows(IOException.class, () -> spyParser.parseCsvFile(faultyFile), "Expected IOException");
//
//        // Verify event publishing
//        ArgumentCaptor<CsvParsingFailedEvent> eventCaptor = ArgumentCaptor.forClass(CsvParsingFailedEvent.class);
//        verify(eventPublisher).publishEvent(eventCaptor.capture());
//        CsvParsingFailedEvent event = eventCaptor.getValue();
//        assertNotNull(event, "Failed parsing event should be published");
//    }
//}
