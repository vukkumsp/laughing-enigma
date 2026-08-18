package com.laughingenigma.api_gateway.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController
public class IdentityTestController {

    @GetMapping("/test/identity")
    public Mono<String> identity(Mono<Principal> principal) {
        return principal.map(Principal::getName);
    }

    @GetMapping("/test/role")
    public Mono<String> role(@AuthenticationPrincipal Jwt jwt) {
        return Mono.just(jwt.getClaimAsString("role"));
    }

    @GetMapping("/test/authority")
    public Mono<String> authority(Authentication authentication) {
        return Mono.just(
                authentication.getAuthorities().toString()
        );
    }
}
