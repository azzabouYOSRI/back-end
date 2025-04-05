package com.yosri.defensy.backend.modules.etl.repository;

import com.yosri.defensy.backend.modules.etl.domain.EtlProcessedRecord;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * ✅ Elasticsearch Repository for storing enriched & correlated data.
 */
@Repository
public interface EtlElasticsearchRepository extends ElasticsearchRepository<EtlProcessedRecord, String> {
}
