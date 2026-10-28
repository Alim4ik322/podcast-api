package com.podcast.api.controller;

import com.podcast.api.dto.HealthResponse;
import com.podcast.api.service.HealthService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping(value = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HealthResponse> health() {
        HealthResponse response = healthService.getHealth();
        
        // Если база упала, возвращаем 503 Service Unavailable
        if (!"up".equalsIgnoreCase(response.getStatus())) { // замените на ваш метод проверки состояния
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }
        
        return ResponseEntity.ok(response);
    }
}