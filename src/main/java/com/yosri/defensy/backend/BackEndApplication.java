package com.yosri.defensy.backend;

import com.yosri.defensy.backend.modules.wazuhconnector.config.WazuhConnectorProperties;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Instant;
import java.util.UUID;

/**
 * 🏁 Main entry point for Defensy Back-End.
 * Handles app initialization, async/scheduled execution, and environment variable loading.
 */
@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableConfigurationProperties(WazuhConnectorProperties.class)
public class BackEndApplication {

    static {
        try {
            Dotenv dotenv = Dotenv.configure()
                .directory(".")
                .ignoreIfMissing()
                .load();

            dotenv.entries().forEach(e -> System.setProperty(e.getKey(), e.getValue()));
        } catch (Exception ignored) {
            System.out.println("⚠️ No .env file found — falling back to system properties.");
        }
    }

    public static void main(String[] args) {
        var context = SpringApplication.run(BackEndApplication.class, args);

        // Optional CSV file generation on startup
        context.getBean(BackEndApplication.class).generateRandomCsvFiles(5);
    }

    /**
     * 📁 Generates sample CSV files in the `csv-inbox/` directory for testing.
     * @param numberOfFiles Number of files to generate
     */
    public void generateRandomCsvFiles(int numberOfFiles) {
        Path inboxDir = Paths.get("csv-inbox");

        try {
            if (Files.notExists(inboxDir)) {
                Files.createDirectories(inboxDir);
            }

            for (int i = 0; i < numberOfFiles; i++) {
                String fileName = String.format("security-events-%d-%s.csv",
                        System.currentTimeMillis(),
                        UUID.randomUUID().toString().substring(0, 8)
                );

                Path csvFile = inboxDir.resolve(fileName);
                String header = "eventId,sourceIp,eventType,severity,timestamp\n";
                String eventId = UUID.randomUUID().toString().substring(0, 8);
                String sourceIp = "192.168." + ((int) (Math.random() * 255)) + "." + ((int) (Math.random() * 255));
                String eventType = "login_failure";
                String severity = "high";
                String timestamp = Instant.now().toString();
                String record = String.join(",", eventId, sourceIp, eventType, severity, timestamp);
                String csvContent = header + record;

                Files.writeString(csvFile, csvContent, StandardCharsets.UTF_8);
                System.out.println("✅ Generated CSV file: " + csvFile.toAbsolutePath());

                Thread.sleep(1000); // Tiny delay to make file timestamps unique
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("❌ Failed to generate CSV files: " + e.getMessage());
            Thread.currentThread().interrupt(); // Restore interrupt status if needed
        }
    }
}
