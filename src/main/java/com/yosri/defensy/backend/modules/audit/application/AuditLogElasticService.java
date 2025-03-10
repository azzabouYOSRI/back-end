package com.yosri.defensy.backend.modules.audit.application;

import com.yosri.defensy.backend.modules.audit.domain.AuditLogElastic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AuditLogElasticService {
    Page<AuditLogElastic> getAllAuditLogs(Pageable pageable);
    Optional<AuditLogElastic> getAuditLogById(String id);
    AuditLogElastic saveAuditLog(AuditLogElastic log);
}
