package com.yosri.defensy.backend.modules.correlation.service;

import java.util.List;
import java.util.Map;

/**
 * ✅ Defines the contract for correlation processing.
 * - Processes ingested CSV data against correlation rules.
 */
public interface CorrelationService {

    /**
     * Processes correlation rules on the given dataset.
     *
     * @param storedData List of parsed CSV data retrieved from Elasticsearch.
     */
    void processCorrelation(List<Map<String, String>> storedData);
}
