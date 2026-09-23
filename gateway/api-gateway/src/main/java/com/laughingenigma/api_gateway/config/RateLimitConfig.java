package com.laughingenigma.api_gateway.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimitConfig {

    private static final Logger log =
            LoggerFactory.getLogger(RateLimitConfig.class);

    @PostConstruct
    public void test() {
        System.out.println("========== RateLimitConfig loaded ==========");
    }

    @Bean
    public KeyResolver ipKeyResolver() {

        return exchange -> {
            String ip = exchange.getRequest()
                    .getRemoteAddress()
                    .getAddress()
                    .getHostAddress();
            System.out.println("[ip-address id]: " + ip);
            log.info("[RATE-LIMITER] IP address: {}", ip);
            return Mono.just(ip);
        };

    }
}
