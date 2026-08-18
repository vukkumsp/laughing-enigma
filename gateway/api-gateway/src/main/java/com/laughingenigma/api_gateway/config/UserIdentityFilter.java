package com.laughingenigma.api_gateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class UserIdentityFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {

        return exchange.getPrincipal()
                .flatMap(principal -> {

                    ServerHttpRequest request = exchange.getRequest()
                            .mutate()
                            .headers(headers ->
                                    headers.remove("X-Authenticated-User")
                            )
                            .header("X-Authenticated-User",
                                    principal.getName())
                            .build();

                    return chain.filter(
                            exchange.mutate()
                                    .request(request)
                                    .build()
                    );
                })
                .switchIfEmpty(chain.filter(exchange));
    }
}
