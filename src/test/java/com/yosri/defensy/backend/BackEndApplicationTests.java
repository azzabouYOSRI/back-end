//package com.yosri.defensy.backend;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.time.Duration;
//import java.util.Map;
//import java.util.UUID;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.awaitility.Awaitility.await;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ActiveProfiles("test")
//class BackEndApplicationTests {
//
//    private static final Logger log = LoggerFactory.getLogger(BackEndApplicationTests.class);
//
//    @LocalServerPort
//    private int port;
//
//    @Autowired
//    private TestRestTemplate restTemplate;
//
//    private static final Path CSV_INBOX = Path.of("csv-inbox");
//    private static final Path CSV_PROCESSING = Path.of("csv-processing");
//    private static final Path CSV_PROCESSED = Path.of("csv-processed");
//    private static final Path CSV_FAILED_PARSING = Path.of("csv-failed-parsing");
//    private static final Path CSV_FAILED_STORING = Path.of("csv-failed-storing");
//
//    @BeforeEach
//    void setUp() throws IOException {
//        // Clean all pipeline directories
//        cleanDirectory(CSV_INBOX);
//        cleanDirectory(CSV_PROCESSING);
//        cleanDirectory(CSV_PROCESSED);
//        cleanDirectory(CSV_FAILED_PARSING);
//        cleanDirectory(CSV_FAILED_STORING);
//    }
//
//    @Test
//    void contextLoads() {
//        assertThat(restTemplate).isNotNull();
//    }
//
//@Test
//void shouldIngestProcessAndRetrieveAlerts() throws IOException {
//    // STEP 1: Create a test CSV with a unique identifier
//    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
//    String uniqueIp = "192.168." + (int)(Math.random() * 254) + "." + (int)(Math.random() * 254);
//    String csvFileName = "security-events-" + System.currentTimeMillis() + ".csv";
//    Path csvFile = CSV_INBOX.resolve(csvFileName);
//
//    Files.createDirectories(CSV_INBOX);
//    String csvContent = "eventId,sourceIp,eventType,severity,timestamp\n" +
//                       uniqueId + "," + uniqueIp + ",login_failure,high,2023-01-01T12:00:00Z";
//    Files.writeString(csvFile, csvContent, StandardCharsets.UTF_8);
//    log.info("✅ STEP 1: Created CSV file: {}", csvFile.toAbsolutePath());
//
//    // STEP 2: Wait for ingestion to move the file from inbox
//    log.info("⏳ STEP 2: Waiting for file to be moved from inbox...");
//    await().atMost(Duration.ofSeconds(30))
//           .until(() -> !Files.exists(csvFile));
//    log.info("✅ STEP 2: File moved from inbox");
//
//    // Check if file is now in processing directory
//    Path processingFile = CSV_PROCESSING.resolve(csvFileName);
//    log.info("Checking if file is in processing directory: {}", Files.exists(processingFile));
//
//    // STEP 3: Wait for ETL processing to complete
//    log.info("⏳ STEP 3: Waiting for file to be processed and stored...");
//    await().atMost(Duration.ofSeconds(30))
//           .until(() -> Files.exists(CSV_PROCESSED.resolve(csvFileName)));
//    log.info("✅ STEP 3: File successfully moved to processed directory");
//
//    // STEP 4: Poll the API until data is available
//    log.info("⏳ STEP 4: Waiting for ETL processing to make data available in API...");
//    await().atMost(Duration.ofSeconds(60)) // Increase timeout to 60 seconds
//           .pollInterval(Duration.ofSeconds(2))
//           .pollDelay(Duration.ofSeconds(2))
//           .conditionEvaluationListener(condition -> {
//               ResponseEntity<Map<String, Object>> response = fetchAlerts(0, 20);
//               int statusCode = response.getStatusCode().value();
//               log.info("API status: {} - {}", statusCode, response.getStatusCode());
//               if (statusCode == 200 && response.getBody() != null) {
//                   Map<String, Object> body = response.getBody();
//                   log.info("Response body: totalElements={}", body.get("totalElements"));
//               }
//           })
//           .until(() -> {
//               ResponseEntity<Map<String, Object>> response = fetchAlerts(0, 20);
//               return response.getStatusCode().is2xxSuccessful() &&
//                     response.getBody() != null &&
//                     (Integer)response.getBody().getOrDefault("totalElements", 0) > 0;
//           });
//
//    // Continue with the rest of the test...
//}
//    private ResponseEntity<Map<String, Object>> fetchAlerts(int page, int size) {
//        String url = "http://localhost:" + port + "/dashboard/alerts?page=" + page + "&size=" + size;
//        return restTemplate.exchange(
//                url,
//                HttpMethod.GET,
//                null,
//                new ParameterizedTypeReference<Map<String, Object>>() {}
//        );
//    }
//
//    private void cleanDirectory(Path directory) throws IOException {
//        if (Files.exists(directory)) {
//            Files.walk(directory)
//                .filter(Files::isRegularFile)
//                .forEach(path -> {
//                    try {
//                        Files.delete(path);
//                    } catch (IOException e) {
//                        log.error("Failed to delete file: {}", path, e);
//                    }
//                });
//        } else {
//            Files.createDirectories(directory);
//        }
//    }
//}
