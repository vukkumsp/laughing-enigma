package com.laughingenigma.security_service.service;

import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final PrivateKey privateKey;
    private final Clock clock;

    public  JwtService(PrivateKey privateKey, Clock clock) {
        this.privateKey = privateKey;
        this.clock = clock;
    }

    public String generateToken(String username) {
        Instant now = clock.instant();

        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(900)))
                .signWith(privateKey)
                .compact();
    }
}
