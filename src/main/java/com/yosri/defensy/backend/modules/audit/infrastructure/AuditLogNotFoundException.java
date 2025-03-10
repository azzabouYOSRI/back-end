package com.yosri.defensy.backend.modules.audit.infrastructure;

public class AuditLogNotFoundException extends RuntimeException {
    public AuditLogNotFoundException(String message) {
        super(message);
    }
}
