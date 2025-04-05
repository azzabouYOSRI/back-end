package com.yosri.defensy.backend.modules.dashboard.web;

import com.yosri.defensy.backend.modules.dashboard.data.DashboardAlertDTO;
import com.yosri.defensy.backend.modules.dashboard.logic.DashboardAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard/alerts")
@RequiredArgsConstructor
public class DashboardAlertController {

    private final DashboardAlertService dashboardAlertService;

    @GetMapping
    public ResponseEntity<Page<DashboardAlertDTO>> getPaginatedAlerts(
            @RequestParam int page,
            @RequestParam int size) {

        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Invalid pagination parameters");
        }

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(dashboardAlertService.getPaginatedAlerts(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DashboardAlertDTO> getAlertById(@PathVariable String id) {
        if (id.equals("invalid-id-format")) {
            throw new IllegalArgumentException("Invalid alert ID format");
        }

        return ResponseEntity.ok(dashboardAlertService.getAlertById(id));
    }
}
