//package com.yosri.defensy.backend.modules.dashboard;
//
//import com.yosri.defensy.backend.modules.dashboard.data.DashboardAlertDTO;
//import com.yosri.defensy.backend.modules.dashboard.data.DashboardAlertRepository;
//import com.yosri.defensy.backend.modules.dashboard.logic.DashboardAlertService;
//import com.yosri.defensy.backend.modules.etl.domain.EtlProcessedRecord;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class DashboardServiceTest {
//
//    @Mock
//    private DashboardAlertRepository dashboardRepository;
//
//    @InjectMocks
//    private DashboardAlertService dashboardService;
//
//    private EtlProcessedRecord sampleAlert;
//
////    @BeforeEach
////    void setUp() {
////        sampleAlert = EtlProcessedRecord.builder()
////                .id("alert-123")
////                .timestamp(Instant.now())
////                .dynamicAttributes(Map.of(
////                        "alertType", "Brute Force Detected",
////                        "details", "Failed logins detected from 192.168.1.50"
////                ))
////                .build();
////    }
//
//    @Test
//    void shouldFetchAlertById() {
//        when(dashboardRepository.findById("alert-123")).thenReturn(Optional.of(sampleAlert));
//
//        DashboardAlertDTO alert = dashboardService.getAlertById("alert-123");
//
//        assertThat(alert).isNotNull();
//        assertThat(alert.dynamicAttributes().get("alertType")).isEqualTo("Brute Force Detected");
//
//        verify(dashboardRepository, times(1)).findById("alert-123");
//    }
//
//    @Test
//    void shouldReturnEmptyIfAlertNotFound() {
//        when(dashboardRepository.findById("alert-999")).thenReturn(Optional.empty());
//
//        try {
//            dashboardService.getAlertById("alert-999");
//        } catch (RuntimeException e) {
//            assertThat(e.getMessage()).isEqualTo("Alert not found");
//        }
//
//        verify(dashboardRepository, times(1)).findById("alert-999");
//    }
//
//    @Test
//    void shouldFetchAllAlerts() {
//        Pageable pageable = PageRequest.of(0, 10);
//        Page<EtlProcessedRecord> mockPage = new PageImpl<>(List.of(sampleAlert), pageable, 1);
//
//        when(dashboardRepository.findAll(any(Pageable.class))).thenReturn(mockPage);
//
//        List<DashboardAlertDTO> alerts = dashboardService.getPaginatedAlerts(pageable).toList();
//
//        assertThat(alerts).hasSize(1);
//        assertThat(alerts.get(0).dynamicAttributes().get("alertType")).isEqualTo("Brute Force Detected");
//
//        verify(dashboardRepository, times(1)).findAll(any(Pageable.class));
//    }
//}
