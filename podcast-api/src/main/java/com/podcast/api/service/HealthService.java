package com.podcast.api.service;

import com.podcast.api.config.AppProperties;
import com.podcast.api.dto.HealthResponse;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.time.Duration;

@Service
public class HealthService {

    private final AppProperties appProperties;
    private final DataSource dataSource;

    public HealthService(AppProperties appProperties, DataSource dataSource) {
        this.appProperties = appProperties;
        this.dataSource = dataSource;
    }

    public HealthResponse getHealth() {
        long uptimeMs = ManagementFactory.getRuntimeMXBean().getUptime();
        Duration uptime = Duration.ofMillis(uptimeMs);

        String uptimeFormatted = String.format("%d days, %02d:%02d:%02d",
                uptime.toDays(),
                uptime.toHoursPart(),
                uptime.toMinutesPart(),
                uptime.toSecondsPart());

        // Проверяем соединение с базой данных
        String dbStatus = "down";
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(2)) {
                dbStatus = "up";
            }
        } catch (Exception e) {
            dbStatus = "down";
        }

        // Возвращаем объект ответа (поле status можно сделать "up", если база жива)
        HealthResponse response = new HealthResponse(
                dbStatus.equals("up") ? "up" : "down",
                appProperties.getAppName(),
                appProperties.getAppVersion(),
                appProperties.getAppEnv(),
                uptimeFormatted
        );
        
        // Если вам в DTO нужно передавать статус базы отдельно, 
        // убедитесь, что в HealthResponse есть соответствующее поле.
        
        return response;
    }
}