package com.yosri.defensy.backend.modules.wazuhconnector;

import com.yosri.defensy.backend.modules.wazuhconnector.service.WazuhConnectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/wazuh")
@RequiredArgsConstructor
public class WazuhConnectorController {

    private final WazuhConnectorService wazuhConnectorService;

    @GetMapping("/test")
    public Mono<String> test() {
        return wazuhConnectorService.testConnection();
    }

    @GetMapping("/security/events")
    public Mono<Map<String, Object>> getSecurityEvents() {
        return wazuhConnectorService.getSecurityEvents();
    }

    @GetMapping("/manager/status")
    public Mono<Map<String, Object>> getManagerStatus() {
        return wazuhConnectorService.getManagerStatus();
    }

    @GetMapping("/agents")
    public Mono<Map<String, Object>> getAgents() {
        return wazuhConnectorService.getAgents();
    }

    @GetMapping("/syscheck")
    public Mono<Map<String, Object>> getSyscheck() {
        return wazuhConnectorService.getSyscheck();
    }

    @GetMapping("/rootcheck")
    public Mono<Map<String, Object>> getRootcheck() {
        return wazuhConnectorService.getRootcheck();
    }
}
