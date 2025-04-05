package com.yosri.defensy.backend.modules.etl.service;

import com.yosri.defensy.backend.modules.ingestion.event.CsvStoredEvent;

/**
 * ✅ ETL Processing Service
 * - Listens for ingested raw data.
 * - Transforms, normalizes, and stores processed data in Elasticsearch.
 */
public interface EtlProcessingService {

    /**
     * ✅ Processes raw ingested data from CSV ingestion.
     * - Retrieves raw data from MongoDB.
     * - Applies transformation rules.
     * - Stores processed data in Elasticsearch.
     *
     * @param event The event containing stored raw data.
     */
    void processEtl(CsvStoredEvent event);
}
