package com.yosri.defensy.backend.modules.wazuhconnector.service;

import com.yosri.defensy.backend.modules.wazuhconnector.config.WazuhConnectorProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class WazuhTokenService {

    private final WebClient.Builder webClientBuilder;
    private final WazuhConnectorProperties properties;

    private final AtomicReference<String> cachedToken = new AtomicReference<>();
    private final AtomicReference<Instant> tokenExpiry = new AtomicReference<>(Instant.EPOCH);

    public WazuhTokenService(
            WebClient.Builder webClientBuilder,
            @Qualifier("wazuh-com.yosri.defensy.backend.modules.wazuhconnector.config.WazuhConnectorProperties")
            WazuhConnectorProperties properties) {
        this.webClientBuilder = webClientBuilder;
        this.properties = properties;
    }

    public Mono<String> getToken() {
        Instant now = Instant.now();
        if (cachedToken.get() != null && tokenExpiry.get().isAfter(now)) {
            return Mono.just(cachedToken.get());
        }
        return refreshToken();
    }

    public Mono<String> refreshToken() {
        String username = properties.getApi().getUsername();
        String password = properties.getApi().getPassword();
        String baseUrl  = properties.getApi().getBaseUrl();

        String credentials = username + ":" + password;
        String encoded = Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        WebClient tempClient = webClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoded)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        return tempClient.get()
                .uri("/security/user/authenticate")
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .map(TokenResponse::token)
                .doOnNext(token -> {
                    cachedToken.set(token);
                    tokenExpiry.set(Instant.now().plus(properties.getJwt().getCacheTtl()));
                    log.info("✅ Wazuh JWT token refreshed and cached.");
                })
                .doOnError(err -> log.error("❌ Failed to refresh Wazuh JWT token: {}", err.getMessage()));
    }

    private record TokenResponse(Data data) {
        public String token() {
            return data.token();
        }
    }

    private record Data(String token) {}
}
