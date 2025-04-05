//package com.yosri.defensy.backend.modules.wazuhconnector.service;
//
//import com.yosri.defensy.backend.modules.wazuhconnector.config.WazuhConnectorProperties;
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.stereotype.Service;
//import reactor.core.publisher.Flux;
//import reactor.core.scheduler.Schedulers;
//
//import java.time.Duration;
//
///**
// * 🕒 Polling service to continuously fetch data from Wazuh.
// */
//@Service
//@RequiredArgsConstructor
//@Slf4j
//@EnableScheduling
//public class WazuhPollingService {
//
//    private final WazuhApiClient wazuhApiClient;
//    private final WazuhConnectorProperties properties;
//
//    @PostConstruct
//    public void schedulePolling() {
//        Duration interval = properties.getPolling().getInterval();
//        log.info("🚀 Wazuh Polling Service initialized. Interval: {}", interval);
//
//        Flux.interval(interval)
//            .flatMap(tick -> wazuhApiClient.getAgentStatusSummary())
//            .subscribeOn(Schedulers.parallel())
//            .subscribe(); // ✅ No arguments required
//    }
//}
