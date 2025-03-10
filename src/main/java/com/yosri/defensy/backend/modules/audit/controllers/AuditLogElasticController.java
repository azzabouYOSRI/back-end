package com.yosri.defensy.backend.modules.audit.controllers;

import com.yosri.defensy.backend.modules.audit.application.AuditLogElasticService;
import com.yosri.defensy.backend.modules.audit.domain.AuditLogElastic;
import com.yosri.defensy.backend.modules.audit.infrastructure.AuditLogNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/audit/elastic")
public class AuditLogElasticController {
    private final AuditLogElasticService auditLogElasticService;

    public AuditLogElasticController(AuditLogElasticService auditLogElasticService) {
        this.auditLogElasticService = auditLogElasticService;
    }

    /**
     * ✅ Get all audit logs with pagination support
     */
    @GetMapping
    public ResponseEntity<Page<AuditLogElastic>> getAllAuditLogs(Pageable pageable) {
        Page<AuditLogElastic> logs = auditLogElasticService.getAllAuditLogs(pageable);
        return ResponseEntity.ok(logs);
    }

    /**
     * ✅ Get audit log by ID (Handles NotFoundException)
     */
@GetMapping("/{id}")
public ResponseEntity<AuditLogElastic> getAuditLogById(@PathVariable String id) {
    AuditLogElastic log = auditLogElasticService.getAuditLogById(id)
            .orElseThrow(() -> new AuditLogNotFoundException(id));
    return ResponseEntity.ok(log);
}

    /**
     * ✅ Create a new audit log entry
     */
    @PostMapping
    public ResponseEntity<AuditLogElastic> createAuditLog(@RequestBody AuditLogElastic log) {
        AuditLogElastic createdLog = auditLogElasticService.saveAuditLog(log);
        return ResponseEntity.ok(createdLog);
    }

    /**
     * ✅ Delete audit log entry by ID
     */

}
