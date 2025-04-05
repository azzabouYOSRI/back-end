package com.yosri.defensy.backend.modules.wazuhconnector.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Service
public class WazuhConnectorService {

    private final WebClient wazuhWebClient;
    private final WazuhTokenService tokenService;

    public WazuhConnectorService(@Qualifier("wazuhWebClient") WebClient wazuhWebClient,
                                 WazuhTokenService tokenService) {
        this.wazuhWebClient = wazuhWebClient;
        this.tokenService = tokenService;
    }

    /**
     * Tests connectivity to Wazuh API and handles token refresh if needed.
     */
    public Mono<String> testConnection() {
        return sendRequest("/security/user")
                .onErrorResume(WebClientResponseException.Unauthorized.class, ex -> {
                    log.warn("🔁 Wazuh token might be expired. Refreshing and retrying...");
                    return tokenService.refreshToken().then(sendRequest("/security/user"));
                })
                .onErrorResume(ex -> {
                    log.error("❌ Wazuh connection failed: {}", ex.getMessage());
                    return Mono.just("Wazuh unreachable");
                });
    }

    /**
     * Fetches security events from Wazuh API.
     */
    public Mono<Map<String, Object>> getSecurityEvents() {
        return fetchWazuhData("/security/events", new ParameterizedTypeReference<Map<String, Object>>() {});
    }

    /**
     * Fetches manager status from Wazuh API.
     */
    public Mono<Map<String, Object>> getManagerStatus() {
        return fetchWazuhData("/manager/status", new ParameterizedTypeReference<Map<String, Object>>() {});
    }

    /**
     * Fetches agents information from Wazuh API.
     */
    public Mono<Map<String, Object>> getAgents() {
        return fetchWazuhData("/agents", new ParameterizedTypeReference<Map<String, Object>>() {});
    }

    /**
     * Fetches syscheck information from Wazuh API.
     */
    public Mono<Map<String, Object>> getSyscheck() {
        return fetchWazuhData("/syscheck", new ParameterizedTypeReference<Map<String, Object>>() {});
    }

    /**
     * Fetches rootcheck information from Wazuh API.
     */
    public Mono<Map<String, Object>> getRootcheck() {
        return fetchWazuhData("/rootcheck", new ParameterizedTypeReference<Map<String, Object>>() {});
    }

    /**
     * Generic method to fetch data from Wazuh API with error handling.
     */
    private <T> Mono<T> fetchWazuhData(String endpoint, ParameterizedTypeReference<T> typeReference) {
        return wazuhWebClient
                .get()
                .uri(endpoint)
                .retrieve()
                .bodyToMono(typeReference)
                .doOnNext(response -> log.info("✅ Successfully fetched data from Wazuh endpoint: {}", endpoint))
                .doOnError(error -> log.error("❌ Error fetching data from Wazuh endpoint {}: {}", endpoint, error.getMessage()))
                .onErrorResume(WebClientResponseException.Unauthorized.class, ex -> {
                    log.warn("🔁 Wazuh token expired while fetching from {}. Refreshing and retrying...", endpoint);
                    return tokenService.refreshToken().then(
                            wazuhWebClient
                                    .get()
                                    .uri(endpoint)
                                    .retrieve()
                                    .bodyToMono(typeReference)
                    );
                })
                .onErrorResume(ex -> {
                    log.error("❌ Failed to fetch data from Wazuh endpoint {}: {}", endpoint, ex.getMessage());
                    return Mono.empty();
                });
    }

    private Mono<String> sendRequest(String endpoint) {
        return wazuhWebClient
                .get()
                .uri(endpoint)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(response -> log.info("✅ Wazuh responded successfully from endpoint: {}", endpoint))
                .doOnError(error -> log.debug("🛑 Wazuh request error from endpoint {}: {}", endpoint, error.getMessage()));
    }
}
