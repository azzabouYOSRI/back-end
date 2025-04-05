package com.yosri.defensy.backend.modules.wazuhconnector;

import com.yosri.defensy.backend.modules.wazuhconnector.config.WazuhConnectorProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.modulith.ApplicationModule;

@ApplicationModule(
    allowedDependencies = {"etl", "ingestion"} // update as needed
)
@EnableConfigurationProperties(WazuhConnectorProperties.class)
public class WazuhConnectorModule {
    // ✅ Ensures module-wide registration of properties
}
