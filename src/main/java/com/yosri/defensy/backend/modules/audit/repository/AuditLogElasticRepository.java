package com.yosri.defensy.backend.modules.audit.repository;

import com.yosri.defensy.backend.modules.audit.domain.AuditLogElastic;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogElasticRepository extends ElasticsearchRepository<AuditLogElastic, String> {
}
