package com.yosri.defensy.backend.modules.ingestion.repository;

import com.yosri.defensy.backend.modules.ingestion.domain.CsvIngestionSearchRecord;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CsvIngestionElasticsearchRepository extends ElasticsearchRepository<CsvIngestionSearchRecord, String> {
}
