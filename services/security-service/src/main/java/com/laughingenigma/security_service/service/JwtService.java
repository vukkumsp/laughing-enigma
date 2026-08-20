package com.laughingenigma.security_service.service;

import com.laughingenigma.security_service.entity.Role;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final Clock clock;

    public  JwtService(PrivateKey privateKey, PublicKey publicKey, Clock clock) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.clock = clock;
    }

    public String generateAccessToken(String username, Role role) {
        Instant now = clock.instant();

        return Jwts.builder()
                .subject(username)
                .claim("role", role.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(900)))
                .signWith(privateKey)
                .compact();
    }

    public String generateRefreshToken(String username) {
        Instant now = clock.instant();

        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(7 * 24 * 60 * 60)))
                .signWith(privateKey)
                .compact();
    }

    public boolean isRefreshTokenValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token);

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
