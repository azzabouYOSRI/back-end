package com.yosri.defensy.backend.modules.ingestion.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import java.util.Map;

@Repository
public interface CsvIngestionRepository extends ElasticsearchRepository<Map<String, String>, String> {
}
