package com.yosri.defensy.backend.modules.audit.repository;

import com.yosri.defensy.backend.modules.audit.domain.AuditLog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogMongoRepository extends MongoRepository<AuditLog, String>, ElasticsearchRepository<AuditLog, String> {
}
