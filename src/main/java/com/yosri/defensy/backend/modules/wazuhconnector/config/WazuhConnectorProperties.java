package com.yosri.defensy.backend.modules.wazuhconnector.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "wazuh")
public class WazuhConnectorProperties {

    private Api api = new Api();
    private Polling polling = new Polling();
    private Ssl ssl = new Ssl();
    private Jwt jwt = new Jwt();

    @Data
    public static class Api {
        private String baseUrl;
        private String username;
        private String password;
    }

    @Data
    public static class Polling {
        private Duration interval;
    }

    @Data
    public static class Ssl {
        private String trustStore;
        private String trustStorePassword;
        private String trustStoreType;
    }

    @Data
    public static class Jwt {
        private boolean enabled;
        private String tokenEndpoint;
        private Duration cacheTtl;
    }
}
