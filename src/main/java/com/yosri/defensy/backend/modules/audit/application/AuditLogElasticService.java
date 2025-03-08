package com.yosri.defensy.backend.modules.audit.application;

import com.yosri.defensy.backend.modules.audit.domain.AuditLogElastic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditLogElasticService {
    Page<AuditLogElastic> getAllAuditLogs(Pageable pageable);
    AuditLogElastic getAuditLogById(String id);
    AuditLogElastic saveAuditLog(AuditLogElastic log);
    void deleteAuditLog(String id);
}
