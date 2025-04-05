package com.yosri.defensy.backend.modules.dashboard.logic;

import com.yosri.defensy.backend.modules.dashboard.data.DashboardAlertDTO;
import com.yosri.defensy.backend.modules.dashboard.data.DashboardAlertRepository;
import com.yosri.defensy.backend.modules.etl.domain.EtlProcessedRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * ✅ Service to fetch and process alerts from Elasticsearch.
 */
@Service
@RequiredArgsConstructor
public class DashboardAlertService {

    private final DashboardAlertRepository dashboardAlertRepository;

    public Page<DashboardAlertDTO> getPaginatedAlerts(Pageable pageable) {
        return dashboardAlertRepository.findAll(pageable)
            .map(this::convertToDTO);
    }

    public DashboardAlertDTO getAlertById(String id) {
        return dashboardAlertRepository.findById(id)
            .map(this::convertToDTO)
            .orElseThrow(() -> new RuntimeException("Alert not found"));
    }

 private DashboardAlertDTO convertToDTO(EtlProcessedRecord alertRecord) {
    return new DashboardAlertDTO(
        alertRecord.getId(),
        Instant.ofEpochMilli(alertRecord.getTimestamp()),
        alertRecord.getDynamicAttributes()
    );
}
}

