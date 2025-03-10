package com.yosri.defensy.backend.modules.audit.controllers;

import com.yosri.defensy.backend.modules.audit.application.AuditLogMongoService;
import com.yosri.defensy.backend.modules.audit.domain.AuditLogMongo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/audit/mongo")
public class AuditLogMongoController {
    private final AuditLogMongoService auditLogMongoService;

    public AuditLogMongoController(AuditLogMongoService auditLogMongoService) {
        this.auditLogMongoService = auditLogMongoService;
    }

    @GetMapping
    public ResponseEntity<List<AuditLogMongo>> getAllAuditLogs() {
        return ResponseEntity.ok(auditLogMongoService.getAllAuditLogs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditLogMongo> getAuditLogById(@PathVariable String id) {
        return ResponseEntity.ok(auditLogMongoService.getAuditLogById(id));
    }

    @PostMapping
    public ResponseEntity<AuditLogMongo> createAuditLog(@RequestBody AuditLogMongo log) {
        return ResponseEntity.ok(auditLogMongoService.createAuditLog(log));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuditLog(@PathVariable String id) {
        auditLogMongoService.deleteAuditLog(id);
        return ResponseEntity.noContent().build();
    }
}
