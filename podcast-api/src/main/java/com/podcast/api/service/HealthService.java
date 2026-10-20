package com.podcast.api.service;

import com.podcast.api.config.AppProperties;
import com.podcast.api.dto.HealthResponse;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.time.Duration;

@Service
public class HealthService {

    private final AppProperties appProperties;

    public HealthService(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public HealthResponse getHealth() {
        long uptimeMs = ManagementFactory.getRuntimeMXBean().getUptime();
        Duration uptime = Duration.ofMillis(uptimeMs);

        String uptimeFormatted = String.format("%d days, %02d:%02d:%02d",
                uptime.toDays(),
                uptime.toHoursPart(),
                uptime.toMinutesPart(),
                uptime.toSecondsPart());

        return new HealthResponse(
                "pass",
                appProperties.getAppName(),
                appProperties.getAppVersion(),
                appProperties.getAppEnv(),
                uptimeFormatted
        );
    }
}