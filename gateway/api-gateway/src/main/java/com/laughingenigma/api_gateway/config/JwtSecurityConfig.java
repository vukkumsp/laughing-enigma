package com.laughingenigma.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class JwtSecurityConfig {
    @Value("${jwt.public-key}")
    private String publicKey;

    @Bean
    public ReactiveJwtDecoder jwtDecoder() throws Exception {

        byte[] keyBytes = Base64.getDecoder().decode(publicKey);

        X509EncodedKeySpec keySpec =
                new X509EncodedKeySpec(keyBytes);

        KeyFactory keyFactory =
                KeyFactory.getInstance("RSA");

        PublicKey key = keyFactory.generatePublic(keySpec);

        return NimbusReactiveJwtDecoder.withPublicKey((RSAPublicKey) key)
                .build();
    }
}
