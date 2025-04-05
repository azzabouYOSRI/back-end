package com.yosri.defensy.backend.modules.dashboard.data;

import java.time.Instant;
import java.util.Map;

/**
 * ✅ Dynamic DTO for dashboard alerts.
 */
public record DashboardAlertDTO(
    String id,
    Instant timestamp,
    Map<String, String> dynamicAttributes
) {}
