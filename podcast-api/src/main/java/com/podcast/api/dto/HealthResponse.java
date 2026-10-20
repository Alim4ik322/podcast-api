package com.podcast.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HealthResponse {

    private String status;

    @JsonProperty("app_name")
    private String appName;

    private String version;
    private String environment;
    private String uptime;

    public HealthResponse() {}

    public HealthResponse(String status, String appName, String version,
                          String environment, String uptime) {
        this.status = status;
        this.appName = appName;
        this.version = version;
        this.environment = environment;
        this.uptime = uptime;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAppName() { return appName; }
    public void setAppName(String appName) { this.appName = appName; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }

    public String getUptime() { return uptime; }
    public void setUptime(String uptime) { this.uptime = uptime; }
}