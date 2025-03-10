package com.yosri.defensy.backend.modules.ingestion.service;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * ✅ Interface for storing parsed CSV data.
 * - Defines contract for storing data into Elasticsearch.
 * - Includes failure handling mechanisms.
 * - Ensures proper event notification for failed storage attempts.
 */
public interface CsvStorageService {

    /**
     * ✅ Stores parsed CSV data into Elasticsearch while ensuring system continuity.
     *
     * @param csvData          The parsed data to store.
     * @param originalFilePath The original file path of the CSV.
     */
    void storeCsvData(List<Map<String, String>> csvData, Path originalFilePath);

    void handleFailedStorage(Path filePath, String errorMessage, List<Map<String, String>> failedData);

    /**
     * Moves a CSV file to a specified directory (e.g., processed or failed-storing).
     *
     * @param filePath  The path of the file to be moved.
     * @param targetDir The directory where the file should be moved.
     * @return The new file path, or null if the move fails.
     */
    Path moveFile(Path filePath, Path targetDir);

    Path moveFailedFile(Path filePath);
}
