package com.yosri.defensy.backend.modules.ingestion.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public interface CsvParserService {
    void parseCsvFile(Path filePath);
    List<Map<String, String>> parse(Path filePath) throws IOException;
}
