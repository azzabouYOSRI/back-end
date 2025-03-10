package com.yosri.defensy.backend.modules.audit.application;

import com.yosri.defensy.backend.modules.audit.application.AuditLogMongoService;
import com.yosri.defensy.backend.modules.audit.domain.AuditLogMongo;
import com.yosri.defensy.backend.modules.audit.infrastructure.AuditLogNotFoundException;
import com.yosri.defensy.backend.modules.audit.repository.AuditLogMongoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditLogMongoServiceImpl implements AuditLogMongoService {

    private final AuditLogMongoRepository auditLogMongoRepository;

    public AuditLogMongoServiceImpl(AuditLogMongoRepository auditLogMongoRepository) {
        this.auditLogMongoRepository = auditLogMongoRepository;
    }

    @Override
    public List<AuditLogMongo> getAllAuditLogs() {
        return auditLogMongoRepository.findAll();
    }

    @Override
    public AuditLogMongo getAuditLogById(String id) {
        return auditLogMongoRepository.findById(id)
                .orElseThrow(() -> new AuditLogNotFoundException(id));
    }

    @Override
    public AuditLogMongo createAuditLog(AuditLogMongo log) {
        return auditLogMongoRepository.save(log);
    }

    @Override
    public void deleteAuditLog(String id) {
        if (!auditLogMongoRepository.existsById(id)) {
            throw new AuditLogNotFoundException(id);
        }
        auditLogMongoRepository.deleteById(id);
    }
}
