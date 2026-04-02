package com.jellisisland.docugap.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "docugap.scheduler")
public class SchedulerProperties {

    private boolean enabled;
    private String cron;
    private String defaultProjectKey;
    private String defaultSpaceKey;
}

