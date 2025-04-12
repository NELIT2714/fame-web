package com.fame_gta5.backend.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.fame_gta5.backend.dto.tokens.TokenResponse;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
public class JwtTokensUtil {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.expiration.time}")
    private long expirationTime;

    public TokenResponse generateToken(Map<String, Object> claims) {
        long expirationMillis = System.currentTimeMillis() + (expirationTime * 60 * 1000L);
        Instant expiresAt = Instant.ofEpochMilli(expirationMillis);
        Date expirationDate = Date.from(expiresAt);

        String jwtToken = Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(new Date())
            .setExpiration(expirationDate)
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();

        return new TokenResponse(jwtToken, expiresAt);
    }

    public Claims getTokenData(String token) {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}