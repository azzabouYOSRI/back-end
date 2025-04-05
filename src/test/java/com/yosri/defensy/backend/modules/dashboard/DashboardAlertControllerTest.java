//package com.yosri.defensy.backend.modules.dashboard;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.yosri.defensy.backend.modules.dashboard.data.DashboardAlertDTO;
//import com.yosri.defensy.backend.modules.dashboard.exceptions.DashboardExceptionHandler;
//import com.yosri.defensy.backend.modules.dashboard.logic.DashboardAlertService;
//import com.yosri.defensy.backend.modules.dashboard.web.DashboardAlertController;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.time.Instant;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@ExtendWith(MockitoExtension.class)
//class DashboardAlertControllerTest {
//
//    @Mock
//    private DashboardAlertService dashboardAlertService;
//
//    @InjectMocks
//    private DashboardAlertController dashboardAlertController;
//
//    private MockMvc mockMvc;
//    private DashboardAlertDTO sampleAlert;
//
//    @BeforeEach
//    void setUp() {
//        // Configure ObjectMapper with proper modules for date/time
//        ObjectMapper objectMapper = new ObjectMapper()
//                .registerModule(new JavaTimeModule());
//
//        MappingJackson2HttpMessageConverter converter =
//                new MappingJackson2HttpMessageConverter(objectMapper);
//
//        mockMvc = MockMvcBuilders
//                .standaloneSetup(dashboardAlertController)
//                .setControllerAdvice(new DashboardExceptionHandler())
//                .setMessageConverters(converter)
//                .build();
//
//        Map<String, String> attributes = new HashMap<>();
//        attributes.put("alertType", "Brute Force Detected");
//        attributes.put("details", "Failed logins detected from 192.168.1.50");
//
//        sampleAlert = new DashboardAlertDTO(
//                "alert-123",
//                Instant.now(),
//                attributes
//        );
//    }
//
//    @Test
//    void shouldFetchPaginatedAlerts() throws Exception {
//        // Create a real PageImpl with actual ArrayList to avoid UnsupportedOperationException
//        List<DashboardAlertDTO> alertList = new ArrayList<>();
//        alertList.add(sampleAlert);
//        PageImpl<DashboardAlertDTO> paginatedAlerts = new PageImpl<>(
//                alertList,
//                PageRequest.of(0, 10),
//                1);
//
//        when(dashboardAlertService.getPaginatedAlerts(any(Pageable.class)))
//                .thenReturn(paginatedAlerts);
//
//        mockMvc.perform(get("/dashboard/alerts")
//                        .param("page", "0")
//                        .param("size", "10"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content[0].id").value("alert-123"));
//    }
//
//    @Test
//    void shouldFetchAlertById() throws Exception {
//        when(dashboardAlertService.getAlertById("alert-123"))
//                .thenReturn(sampleAlert);
//
//        mockMvc.perform(get("/dashboard/alerts/alert-123"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value("alert-123"));
//    }
//
//    @Test
//    void shouldReturn404WhenAlertNotFound() throws Exception {
//        when(dashboardAlertService.getAlertById("non-existent-id"))
//                .thenThrow(new RuntimeException("Alert not found"));
//
//        mockMvc.perform(get("/dashboard/alerts/non-existent-id"))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.message").value("Alert not found"));
//    }
//
//    @Test
//    void shouldReturnBadRequestForInvalidPaginationParameters() throws Exception {
//        mockMvc.perform(get("/dashboard/alerts")
//                        .param("page", "-1")
//                        .param("size", "10"))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message").value("Invalid pagination parameters"));
//    }
//
//    @Test
//    void shouldReturnEmptyResultsWhenNoAlertsFound() throws Exception {
//        PageImpl<DashboardAlertDTO> emptyPage = new PageImpl<>(
//                new ArrayList<>(),
//                PageRequest.of(0, 10),
//                0);
//
//        when(dashboardAlertService.getPaginatedAlerts(any(Pageable.class)))
//                .thenReturn(emptyPage);
//
//        mockMvc.perform(get("/dashboard/alerts")
//                        .param("page", "0")
//                        .param("size", "10"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content").isArray())
//                .andExpect(jsonPath("$.content").isEmpty());
//    }
//
//    @Test
//    void shouldHandleInternalServerError() throws Exception {
//        doThrow(new RuntimeException("Internal server error"))
//                .when(dashboardAlertService).getPaginatedAlerts(any(Pageable.class));
//
//        // Note: RuntimeExceptions are mapped to 404 in your handler
//        mockMvc.perform(get("/dashboard/alerts")
//                        .param("page", "0")
//                        .param("size", "10"))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.message").value("Internal server error"));
//    }
//
//    @Test
//    void shouldReturnBadRequestForInvalidAlertIdFormat() throws Exception {
//        mockMvc.perform(get("/dashboard/alerts/invalid-id-format"))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message").value("Invalid alert ID format"));
//    }
//
//    @Test
//    void shouldHandleLargePageSize() throws Exception {
//        List<DashboardAlertDTO> alertList = new ArrayList<>();
//        alertList.add(sampleAlert);
//        PageImpl<DashboardAlertDTO> largePage = new PageImpl<>(
//                alertList,
//                PageRequest.of(0, 1000),
//                1);
//
//        when(dashboardAlertService.getPaginatedAlerts(any(Pageable.class)))
//                .thenReturn(largePage);
//
//        mockMvc.perform(get("/dashboard/alerts")
//                        .param("page", "0")
//                        .param("size", "1000"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content").isArray());
//    }
//
//    @Test
//    void shouldReturnBadRequestForMissingRequiredParameters() throws Exception {
//        mockMvc.perform(get("/dashboard/alerts"))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message").exists());
//    }
//}
