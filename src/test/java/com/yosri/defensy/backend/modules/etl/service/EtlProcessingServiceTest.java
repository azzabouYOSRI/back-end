//package com.yosri.defensy.backend.modules.etl.service;
//
//import com.yosri.defensy.backend.modules.etl.domain.EtlProcessedRecord;
//import com.yosri.defensy.backend.modules.etl.event.CorrelationEvent;
//import com.yosri.defensy.backend.modules.etl.event.EtlProcessingFailedEvent;
//import com.yosri.defensy.backend.modules.etl.repository.EtlElasticsearchRepository;
//import com.yosri.defensy.backend.modules.etl.rules.CorrelationRuleEngine;
//import com.yosri.defensy.backend.modules.ingestion.event.CsvStoredEvent;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.Arguments;
//import org.junit.jupiter.params.provider.MethodSource;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.context.ApplicationEventPublisher;
//
//import java.time.Instant;
//import java.util.*;
//import java.util.concurrent.*;
//import java.util.stream.Stream;
//
//import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class EtlProcessingServiceTest {
//
//    @Mock
//    private EtlElasticsearchRepository elasticsearchRepository;
//
//    @Mock
//    private CorrelationRuleEngine ruleEngine;
//
//    @Mock
//    private ApplicationEventPublisher eventPublisher;
//
//    @InjectMocks
//    private EtlProcessingServiceImpl etlProcessingService;
//
//    private List<Map<String, String>> rawEventData;
//
//    @BeforeEach
//    void setUp() {
//        rawEventData = List.of(
//            Map.of("eventId", "EVT123", "eventType", "login_failure", "ip", "192.168.1.50", "timestamp", Instant.now().toString()),
//            Map.of("eventId", "EVT124", "eventType", "login_failure", "ip", "192.168.1.50", "timestamp", Instant.now().minusSeconds(30).toString()),
//            Map.of("eventId", "EVT125", "eventType", "rdp_connection", "ip", "192.168.1.50", "timestamp", Instant.now().toString()),
//            Map.of("eventId", "EVT126", "eventType", "port_scan", "ip", "192.168.1.100", "timestamp", Instant.now().toString())
//        );
//    }
//
//
//
//    @Test
//    void shouldApplyTimeWindowCorrelation() {
//        when(ruleEngine.findMatchingRule(any())).thenReturn(Optional.of("Time Window Correlation"));
//
//        CsvStoredEvent storedEvent = new CsvStoredEvent(this, rawEventData);
//        etlProcessingService.processEtl(storedEvent);
//
//        verify(eventPublisher, times(4)).publishEvent(any(CorrelationEvent.class));
//    }
//
//
//
//    @Test
//    void shouldHandleProcessingFailure() {
//        doThrow(new RuntimeException("Elasticsearch failure")).when(elasticsearchRepository).save(any());
//
//        CsvStoredEvent storedEvent = new CsvStoredEvent(this, rawEventData);
//        etlProcessingService.processEtl(storedEvent);
//
//        verify(eventPublisher, times(4)).publishEvent(any(EtlProcessingFailedEvent.class));
//    }
//
//
// @Test
//void shouldSaveProcessedRecordToElasticsearch() {
//    ArgumentCaptor<EtlProcessedRecord> recordCaptor = ArgumentCaptor.forClass(EtlProcessedRecord.class);
//
//    CsvStoredEvent storedEvent = new CsvStoredEvent(this, List.of(
//        Map.of("eventId", "TEST001", "eventType", "login_failure", "ip", "192.168.1.50", "timestamp", Instant.now().toString())
//    ));
//
//    etlProcessingService.processEtl(storedEvent);
//
//    verify(elasticsearchRepository).save(recordCaptor.capture());
//    EtlProcessedRecord savedRecord = recordCaptor.getValue();
//
//    assertThat(savedRecord.getDynamicAttributes())
//        .containsEntry("eventId", "TEST001")
//        .containsEntry("eventType", "login_failure")
//        .containsEntry("riskLevel", "High")
//        .containsKey("processed_timestamp")
//        .isNotNull();
//}
//
//@Test
//void shouldHandleElasticsearchConnectionFailure() {
//    doThrow(new RuntimeException("Connection refused")).when(elasticsearchRepository).save(any());
//
//    CsvStoredEvent storedEvent = new CsvStoredEvent(this, rawEventData);
//    etlProcessingService.processEtl(storedEvent);
//
//    verify(eventPublisher, times(4)).publishEvent(argThat(event ->
//        event instanceof EtlProcessingFailedEvent &&
//        ((EtlProcessingFailedEvent) event).getError().contains("Connection refused")
//    ));
//}
//
//    @Test
//    void shouldProcessDifferentEventTypesWithCorrectRiskLevels() {
//        List<Map<String, String>> mixedEvents = List.of(
//            Map.of("eventId", "EVT1", "eventType", "login_failure"),
//            Map.of("eventId", "EVT2", "eventType", "malware_detected"),
//            Map.of("eventId", "EVT3", "eventType", "file_access"),
//            Map.of("eventId", "EVT4", "eventType", "unknown_event")
//        );
//
//        ArgumentCaptor<EtlProcessedRecord> recordCaptor = ArgumentCaptor.forClass(EtlProcessedRecord.class);
//        CsvStoredEvent storedEvent = new CsvStoredEvent(this, mixedEvents);
//
//        etlProcessingService.processEtl(storedEvent);
//
//        verify(elasticsearchRepository, times(4)).save(recordCaptor.capture());
//        List<EtlProcessedRecord> savedRecords = recordCaptor.getAllValues();
//
//        assertThat(savedRecords).hasSize(4);
//        assertThat(savedRecords.get(0).getDynamicAttributes()).containsEntry("riskLevel", "High");
//        assertThat(savedRecords.get(1).getDynamicAttributes()).containsEntry("riskLevel", "Medium");
//        assertThat(savedRecords.get(2).getDynamicAttributes()).containsEntry("riskLevel", "Low");
//        assertThat(savedRecords.get(3).getDynamicAttributes()).containsEntry("riskLevel", "Informational");
//    }
//
//    @Test
//    void shouldHandleEmptyEventList() {
//        CsvStoredEvent storedEvent = new CsvStoredEvent(this, Collections.emptyList());
//        etlProcessingService.processEtl(storedEvent);
//
//        verify(elasticsearchRepository, never()).save(any());
//        verify(eventPublisher, never()).publishEvent(any());
//    }
//
//@Test
//void shouldHandleMalformedEventData() {
//    List<Map<String, String>> malformedEvents = List.of(
//        Map.of("eventId", "EVT1"), // Missing required fields
//        new HashMap<>() // Empty map
//    );
//
//    // Simulate save failure for malformed data
//    doThrow(new RuntimeException("Invalid record")).when(elasticsearchRepository).save(any());
//
//    CsvStoredEvent storedEvent = new CsvStoredEvent(this, malformedEvents);
//    etlProcessingService.processEtl(storedEvent);
//
//    verify(eventPublisher, times(2)).publishEvent(any(EtlProcessingFailedEvent.class));
//}
//
//    @Test
//    void shouldPreserveOriginalTimestamp() {
//        String originalTimestamp = Instant.now().minusSeconds(3600).toString();
//        List<Map<String, String>> events = List.of(
//            Map.of("eventId", "EVT1", "eventType", "login_failure", "timestamp", originalTimestamp)
//        );
//
//        ArgumentCaptor<EtlProcessedRecord> recordCaptor = ArgumentCaptor.forClass(EtlProcessedRecord.class);
//        CsvStoredEvent storedEvent = new CsvStoredEvent(this, events);
//
//        etlProcessingService.processEtl(storedEvent);
//
//        verify(elasticsearchRepository).save(recordCaptor.capture());
//        assertThat(recordCaptor.getValue().getDynamicAttributes())
//            .containsEntry("timestamp", originalTimestamp);
//    }
//
//
//
//
//    @ParameterizedTest
//    @MethodSource("elasticsearchFailureTestCases")
//    void shouldHandleElasticsearchFailures(String errorMessage, Map<String, String> eventData, int expectedFailures) {
//        doThrow(new RuntimeException(errorMessage)).when(elasticsearchRepository).save(any());
//
//        CsvStoredEvent storedEvent = new CsvStoredEvent(this, List.of(eventData));
//        etlProcessingService.processEtl(storedEvent);
//
//        verify(eventPublisher, times(expectedFailures)).publishEvent(argThat(event ->
//            event instanceof EtlProcessingFailedEvent &&
//            ((EtlProcessingFailedEvent) event).getError().contains(errorMessage)
//        ));
//    }
//
//    private static Stream<Arguments> elasticsearchFailureTestCases() {
//        return Stream.of(
//            Arguments.of(
//                "Connection refused",
//                Map.of("eventId", "EVT1", "eventType", "login_failure"),
//                1
//            ),
//            Arguments.of(
//                "Elasticsearch timeout",
//                Map.of("eventId", "EVT2", "eventType", "malware_detected"),
//                1
//            ),
//            Arguments.of(
//                "Index not found",
//                Map.of("eventId", "EVT3", "eventType", "file_access"),
//                1
//            )
//        );
//    }
//
//    @Test
//void shouldHandleBulkProcessingEfficiently() throws InterruptedException {
//    List<Map<String, String>> bulkEvents = new ArrayList<>();
//    for (int i = 0; i < 10000; i++) {
//        bulkEvents.add(Map.of(
//            "eventId", "EVT" + i,
//            "eventType", "ddos_attack",
//            "ip", "10.0.0." + i,
//            "timestamp", Instant.now().toString()
//        ));
//    }
//    when(ruleEngine.findMatchingRule(any())).thenReturn(Optional.of("DDoS Attack"));
//
//    CsvStoredEvent storedEvent = new CsvStoredEvent(this, bulkEvents);
//
//    try (ExecutorService executor = Executors.newFixedThreadPool(10)) {
//        Future<?> future = executor.submit(() -> etlProcessingService.processEtl(storedEvent));
//        executor.shutdown(); // Initiate shutdown after submitting task
//
//        if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
//            executor.shutdownNow();
//            throw new AssertionError("Processing timeout");
//        }
//
//        future.get(); // Wait for completion without additional timeout
//    } catch (ExecutionException e) {
//        throw new AssertionError("Processing failed", e);
//    }
//
//    verify(eventPublisher, times(10000)).publishEvent(any(CorrelationEvent.class));
//}
//
//  @Test
//void shouldHandleConcurrentProcessing() throws InterruptedException {
//    int numThreads = 5;
//    int eventsPerThread = 1000;
//    CountDownLatch latch = new CountDownLatch(numThreads);
//    List<Exception> exceptions = new CopyOnWriteArrayList<>();
//
//    try (ExecutorService executor = Executors.newFixedThreadPool(numThreads)) {
//        for (int i = 0; i < numThreads; i++) {
//            List<Map<String, String>> threadEvents = new ArrayList<>();
//            for (int j = 0; j < eventsPerThread; j++) {
//                threadEvents.add(Map.of(
//                    "eventId", "EVT-" + i + "-" + j,
//                    "eventType", "login_failure",
//                    "timestamp", Instant.now().toString()
//                ));
//            }
//
//            executor.execute(() -> {
//                try {
//                    CsvStoredEvent event = new CsvStoredEvent(this, threadEvents);
//                    etlProcessingService.processEtl(event);
//                } catch (Exception e) {
//                    exceptions.add(e);
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//
//        if (!latch.await(30, TimeUnit.SECONDS)) {
//            throw new AssertionError("Processing timeout");
//        }
//
//        executor.shutdown();
//        if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
//            executor.shutdownNow();
//            throw new AssertionError("Executor did not terminate");
//        }
//    }
//
//    if (!exceptions.isEmpty()) {
//        throw new AssertionError("Processing errors occurred: " + exceptions);
//    }
//
//    verify(elasticsearchRepository, times(numThreads * eventsPerThread)).save(any());
//}
//@ParameterizedTest(name = "{0}")
//@MethodSource("correlationTestCases")
//void shouldHandleCorrelationScenarios(
//    @SuppressWarnings("unused") String testCase, // Used for test display name
//    String ruleResult,
//    int expectedEventCount
//) {
//    // Setup rule engine response
//    when(ruleEngine.findMatchingRule(any())).thenReturn(
//        ruleResult == null ? Optional.empty() : Optional.of(ruleResult)
//    );
//
//    // Process test events
//    CsvStoredEvent storedEvent = new CsvStoredEvent(this, rawEventData);
//    etlProcessingService.processEtl(storedEvent);
//
//    // Verify correlation events
//    if (expectedEventCount == 0) {
//        verify(eventPublisher, never()).publishEvent(any(CorrelationEvent.class));
//    } else {
//        verify(eventPublisher, times(expectedEventCount)).publishEvent(any(CorrelationEvent.class));
//    }
//}
//
//private static Stream<Arguments> correlationTestCases() {
//    return Stream.of(
//        Arguments.of(
//            "Should detect brute force attack",
//            "Brute Force Detected",
//            4
//        ),
//        Arguments.of(
//            "Should detect lateral movement",
//            "Lateral Movement Detected",
//            4
//        ),
//        Arguments.of(
//            "Should not trigger correlation for unrelated events",
//            null,
//            0
//        )
//    );
//}
//}
