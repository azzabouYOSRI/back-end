package com.yosri.defensy.backend.modules.audit.controllers;

import com.yosri.defensy.backend.modules.audit.domain.AuditLogMongo;
import com.yosri.defensy.backend.modules.audit.repository.AuditLogMongoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/audit-logs")
public class AuditLogMongoController {

    private final AuditLogMongoRepository auditLogRepository;

    public AuditLogMongoController(AuditLogMongoRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping
    public List<AuditLogMongo> getAllLogs() {
        return auditLogRepository.findAll();
    }

    @GetMapping("/{id}")
    public AuditLogMongo getLogById(@PathVariable String id) {
        return auditLogRepository.findById(id).orElseThrow(() -> new RuntimeException("Log not found"));
    }
}
