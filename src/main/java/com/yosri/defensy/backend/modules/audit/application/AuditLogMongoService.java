package com.yosri.defensy.backend.modules.audit.application;

import com.yosri.defensy.backend.modules.audit.domain.AuditLogMongo;
import java.util.List;

public interface AuditLogMongoService {
    List<AuditLogMongo> getAllAuditLogs();
    AuditLogMongo getAuditLogById(String id);
    AuditLogMongo createAuditLog(AuditLogMongo log);
    void deleteAuditLog(String id);
}
