package com.podcast.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppProperties {

    @Value("${APP_NAME:Podcast Hosting & Streaming API}")
    private String appName;

    @Value("${APP_ENV:development}")
    private String appEnv;

    @Value("${APP_VERSION:1.0.0}")
    private String appVersion;

    @Value("${HTTP_PORT:8080}")
    private int httpPort;

    public String getAppName() { return appName; }
    public String getAppEnv() { return appEnv; }
    public String getAppVersion() { return appVersion; }
    public int getHttpPort() { return httpPort; }
}