package com.yosri.defensy.backend.modules.audit.application;

import com.yosri.defensy.backend.modules.audit.domain.AuditLogElastic;
import com.yosri.defensy.backend.modules.audit.repository.AuditLogElasticRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class AuditLogElasticServiceImpl implements AuditLogElasticService {
    private final AuditLogElasticRepository auditLogElasticRepository;

    public AuditLogElasticServiceImpl(AuditLogElasticRepository auditLogElasticRepository) {
        this.auditLogElasticRepository = auditLogElasticRepository;
    }

    @Override
    public Page<AuditLogElastic> getAllAuditLogs(Pageable pageable) {
        return auditLogElasticRepository.findAll(pageable);
    }

    @Override
   public Optional<AuditLogElastic> getAuditLogById(String id) {
    return auditLogElasticRepository.findById(id);
}
public AuditLogElastic createAuditLog(AuditLogElastic log) {
    if (log.getId() == null) {
        log.setId(UUID.randomUUID().toString()); // Ensure ID is a valid string
    }
    return auditLogElasticRepository.save(log);
    }

public AuditLogElastic saveAuditLog (AuditLogElastic log) {
    return auditLogElasticRepository.save(log);
}

}
