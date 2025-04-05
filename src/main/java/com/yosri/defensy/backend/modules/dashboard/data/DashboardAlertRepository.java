package com.yosri.defensy.backend.modules.dashboard.data;

import com.yosri.defensy.backend.modules.etl.domain.EtlProcessedRecord;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * ✅ Queries processed records from Elasticsearch.
 */
@Repository
public interface DashboardAlertRepository extends ElasticsearchRepository<EtlProcessedRecord, String> {
}
