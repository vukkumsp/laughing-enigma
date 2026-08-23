package com.laughingenigma.api_gateway.indicator;

import com.laughingenigma.api_gateway.config.ServiceUrls;
import com.laughingenigma.api_gateway.dto.ServiceHealth;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.ReactiveHealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component("microservices")
public class MicroservicesHealthIndicator implements ReactiveHealthIndicator {

    private final WebClient webClient;
    private final ServiceUrls serviceUrls;

    public MicroservicesHealthIndicator(
            WebClient.Builder builder,
            ServiceUrls serviceUrls) {
        this.webClient = builder.build();
        this.serviceUrls = serviceUrls;
    }

    @Override
    public Mono<Health> health() {

        Mono<ServiceHealth> customer = check(serviceUrls.customer().url() + "/actuator/health");
        Mono<ServiceHealth> security = check(serviceUrls.security().url() + "/actuator/health");
        Mono<ServiceHealth> event = check(serviceUrls.event().url() + "/actuator/health");
        Mono<ServiceHealth> saga = check(serviceUrls.saga().url() + "/actuator/health");

        return Mono.zip(customer, security, event, saga)
                .map(result -> {
                    ServiceHealth customerHealth = result.getT1();
                    ServiceHealth securityHealth = result.getT2();
                    ServiceHealth eventHealth = result.getT3();
                    ServiceHealth sagaHealth = result.getT4();

                    boolean allUp =
                            isUp(customerHealth)
                                    && isUp(securityHealth)
                                    && isUp(eventHealth)
                                    && isUp(sagaHealth);

                    Health.Builder health = allUp
                            ? Health.up()
                            : Health.down();

                    return health
                            .withDetail("customer-service", customerHealth)
                            .withDetail("security-service", securityHealth)
                            .withDetail("event-service", eventHealth)
                            .withDetail("saga-orchestrator", sagaHealth)
                            .build();
                });
    }

    private boolean isUp(ServiceHealth health) {
        return "UP".equalsIgnoreCase(health.status());
    }

    private Mono<ServiceHealth> check(String url) {
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(ServiceHealth.class)
                .onErrorResume(ex ->
                    Mono.just(new ServiceHealth("DOWN", null))
                );
    }
}
