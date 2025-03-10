package com.yosri.defensy.backend.modules.correlation.repository;

import com.yosri.defensy.backend.modules.correlation.domain.CorrelationResult;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ✅ Correlation Repository
 * - Handles Elasticsearch persistence for correlation results.
 * - Abstracts direct ES queries for better maintainability.
 */
@Repository
public interface CorrelationRepository extends ElasticsearchRepository<CorrelationResult, String> {

    /**
     * Finds correlation results based on event ID.
     *
     * @param eventId The ID of the ingested event.
     * @return List of correlation results.
     */
    List<CorrelationResult> findByEventId(String eventId);

    /**
     * Finds all alerts triggered within a specific timeframe.
     *
     * @param isAlert Whether the event was flagged as an alert.
     * @return List of correlation alerts.
     */
    List<CorrelationResult> findByIsAlert(boolean isAlert);
}
