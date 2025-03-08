package com.yosri.defensy.backend.modules.audit.domain;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogElasticRepository extends ElasticsearchRepository<AuditLogElastic, String> {
}
