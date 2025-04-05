package com.yosri.defensy.backend.modules.wazuhconnector.config;

import com.yosri.defensy.backend.modules.wazuhconnector.service.WazuhTokenService;
import io.netty.handler.ssl.SslContextBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.InputStream;
import java.security.KeyStore;
import java.time.Duration;

@Slf4j
@Configuration
public class WazuhWebClientFactory {

    private final WazuhConnectorProperties properties;
    private final WazuhTokenService tokenService;
    private final ApplicationContext applicationContext;

    public WazuhWebClientFactory(
            @Qualifier("wazuh-com.yosri.defensy.backend.modules.wazuhconnector.config.WazuhConnectorProperties")
            WazuhConnectorProperties properties,
            WazuhTokenService tokenService,
            ApplicationContext applicationContext) {
        this.properties = properties;
        this.tokenService = tokenService;
        this.applicationContext = applicationContext;
    }

    @Bean(name = "wazuhWebClient")
    public WebClient wazuhWebClient() {
        return WebClient.builder()
                .baseUrl(properties.getApi().getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(buildSecureHttpClient()))
                .filter(bearerTokenInterceptor())
                .build();
    }

    private HttpClient buildSecureHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(properties.getSsl().getTrustStoreType());

            // Try direct file path first
            String absolutePath = "/Users/yosriazabou/IdeaProjects/back-end/src/main/resources/ssl/wazuh-truststore.jks";
            Resource trustStoreResource = new FileSystemResource(absolutePath);

            if (!trustStoreResource.exists()) {
                // Fall back to application context resolution
                log.info("File not found at absolute path, trying with application context: {}",
                        properties.getSsl().getTrustStore());
                trustStoreResource = applicationContext.getResource(properties.getSsl().getTrustStore());
            }

            if (!trustStoreResource.exists()) {
                throw new IllegalStateException("Truststore file not found at: " + absolutePath +
                        " or " + properties.getSsl().getTrustStore());
            }

            log.info("Loading truststore from: {}", trustStoreResource.getURI());

            try (InputStream trustStoreStream = trustStoreResource.getInputStream()) {
                trustStore.load(trustStoreStream, properties.getSsl().getTrustStorePassword().toCharArray());
            }

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);

            var sslContext = SslContextBuilder.forClient()
                    .trustManager(tmf)
                    .build();
            log.info("✅ SSL context initialized from truststore successfully");
            return HttpClient.create()
                    .secure(ssl -> ssl.sslContext(sslContext))
                    .responseTimeout(Duration.ofSeconds(10));
        } catch (Exception e) {
            log.error("❌ SSL setup failed for Wazuh WebClient: {}", e.getMessage(), e);
            throw new IllegalStateException("SSL configuration failed", e);
        }
    }

    private ExchangeFilterFunction bearerTokenInterceptor() {
        return ExchangeFilterFunction.ofRequestProcessor(request ->
                tokenService.getToken()
                        .map(token -> ClientRequest.from(request)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .build()
                        )
        );
    }
}
