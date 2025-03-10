package com.yosri.defensy.backend.modules.audit.repository;

import com.yosri.defensy.backend.modules.audit.domain.AuditLogMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogMongoRepository extends MongoRepository<AuditLogMongo, String> {
}
