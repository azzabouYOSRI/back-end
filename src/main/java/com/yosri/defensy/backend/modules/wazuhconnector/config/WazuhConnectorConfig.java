package com.yosri.defensy.backend.modules.wazuhconnector.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class WazuhConnectorConfig {
    private final WazuhConnectorProperties properties;
    private final ApplicationContext applicationContext;

    @Bean(name = "wazuhConnectorProperties")
    public WazuhConnectorProperties wazuhConnectorProperties() {
        return properties;
    }
}
