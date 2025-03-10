package com.yosri.defensy.backend.modules.ingestion.service;

import java.nio.file.Path;

/**
 * ✅ Interface for parsing CSV files.
 * - Defines contract for parsing CSV files and handling failures.
 */
public interface CsvParserService {

    /**
     * Parses a CSV file and handles parsing failures.
     *
     * @param filePath The path of the CSV file to be parsed.
     */
    void parseCsvFile(Path filePath);

    /**
     * Moves a CSV file to a specified directory (e.g., processing or failed-parsing).
     *
     * @param filePath  The path of the file to be moved.
     * @param targetDir The directory where the file should be moved.
     * @return The new file path, or null if the move fails.
     */
    Path moveFile(Path filePath, Path targetDir);
}
