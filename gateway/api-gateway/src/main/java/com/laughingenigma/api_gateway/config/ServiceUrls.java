package com.laughingenigma.api_gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.services")
public record ServiceUrls(
        ServiceUrl customer,
        ServiceUrl security,
        ServiceUrl event,
        ServiceUrl saga
) {
    public record ServiceUrl(String url) {}
}