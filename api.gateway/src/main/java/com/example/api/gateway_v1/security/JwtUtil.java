package com.example.api.gateway_v1.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Component
public class JwtUtil {

    private static final String SECRET = "MiClaveSuperSecretaParaJwtDebeTenerMinimo32Caracteres123456";

    private SecretKey getSigningKey() {
        return new SecretKeySpec(
                SECRET.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );
    }

    public Claims validarToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}