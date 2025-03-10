package com.yosri.defensy.backend.modules.ingestion.service;

import com.yosri.defensy.backend.modules.ingestion.event.CsvParsedEvent;
import com.yosri.defensy.backend.modules.ingestion.event.CsvParsingFailedEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * ✅ CSV Parsing Service Implementation (Sonar & Snyk Best Practices)
 * - Moves successfully parsed files from `csv-inbox/` to `csv-processing/`
 * - Moves failed files to `csv-failed-parsing/`
 * - Ensures no files remain in `csv-inbox/` after processing
 * - Uses Apache Commons CSV for strict validation
 * - Logs meaningful warnings/errors & handles unexpected failures gracefully
 */
@Slf4j
@Service
public class CsvParserServiceImpl implements CsvParserService {

    private static final Path INBOX_DIR = Paths.get("csv-inbox");
    private static final Path PROCESSING_DIR = Paths.get("csv-processing");
    private static final Path FAILED_PARSING_DIR = Paths.get("csv-failed-parsing");

    private final ApplicationEventPublisher eventPublisher;

    public CsvParserServiceImpl(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        createDirectories(); // Ensure required directories exist
    }

    @Override
    public void parseCsvFile(Path filePath) {
        log.info("📂 Parsing: {}", filePath);

        if (!Files.exists(filePath)) {
            log.warn("🚨 Skipping file (not found): {}", filePath);
            return;
        }

        try {
            List<Map<String, String>> parsedData = parse(filePath);

            if (parsedData.isEmpty()) {
                moveToFailedParsing(filePath, "Empty or invalid CSV file");
                return;
            }

            moveToProcessing(filePath);
        } catch (IOException e) {
            log.error("❌ Error reading file {}: {}", filePath, e.getMessage());
            moveToFailedParsing(filePath, "Read error");
        } catch (Exception e) {
            log.error("🚨 Unexpected error parsing file {}: {}", filePath, e.getMessage(), e);
            moveToFailedParsing(filePath, "Unexpected parsing error");
        }
    }

    private void createDirectories() {
        try {
            Files.createDirectories(INBOX_DIR);
            Files.createDirectories(PROCESSING_DIR);
            Files.createDirectories(FAILED_PARSING_DIR);
        } catch (IOException e) {
            log.error("❌ Failed to create required directories!", e);
            throw new IllegalStateException("Could not create necessary directories", e);
        }
    }

    private void moveToFailedParsing(Path filePath, String reason) {
        log.warn("❌ {}: {}", reason, filePath);
        Path movedPath = moveFile(filePath, FAILED_PARSING_DIR);
        if (movedPath != null) {
            eventPublisher.publishEvent(new CsvParsingFailedEvent(this, movedPath.toString()));
        }
    }

    private void moveToProcessing(Path filePath) {
        Path targetPath = moveFile(filePath, PROCESSING_DIR);
        if (targetPath != null) {
            eventPublisher.publishEvent(new CsvParsedEvent(this, targetPath.toString()));
            log.info("✅ CSV processed successfully: {}", targetPath);
        }
    }

    List<Map<String, String>> parse(Path filePath) throws IOException {
        List<Map<String, String>> extractedData = new ArrayList<>();

        try (Reader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader().withSkipHeaderRecord(true))) {

            if (csvParser.getHeaderMap().isEmpty()) {
                log.warn("⚠️ CSV has no headers: {}", filePath);
                return Collections.emptyList();
            }

            for (CSVRecord record : csvParser) {
                extractedData.add(new LinkedHashMap<>(record.toMap()));
            }

            log.info("✅ Extracted {} records from {}", extractedData.size(), filePath);
        }

        return extractedData;
    }

    public Path moveFile(Path filePath, Path targetDir) {
        try {
            Files.createDirectories(targetDir);
            Path targetPath = targetDir.resolve(filePath.getFileName());
            Files.move(filePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("📂 Moved file to '{}': {}", targetDir, targetPath);
            return targetPath;
        } catch (IOException e) {
            log.error("❌ Failed to move '{}' to '{}': {}", filePath, targetDir, e.getMessage());
            return null;
        }
    }
}
