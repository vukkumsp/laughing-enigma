package com.laughingenigma.api_gateway.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.stereotype.Component;

@Component
public class DisabledFilterStripper {

    private final GatewayProperties props;
    private final boolean cbEnabled;
    private final boolean rlEnabled;

    public DisabledFilterStripper(
            GatewayProperties props,
            @Value("${spring.cloud.gateway.server.webflux.filter.circuit-breaker.enabled:true}") boolean cbEnabled,
            @Value("${spring.cloud.gateway.server.webflux.filter.request-rate-limiter.enabled:true}") boolean rlEnabled) {
        this.props = props;
        this.cbEnabled = cbEnabled;
        this.rlEnabled = rlEnabled;
    }

    @PostConstruct
    void strip() {
        props.getRoutes().forEach(route -> route.getFilters().removeIf(f ->
                (!cbEnabled && "CircuitBreaker".equals(f.getName())) ||
                        (!rlEnabled && "RequestRateLimiter".equals(f.getName()))));
    }
}
