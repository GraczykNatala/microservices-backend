package com.example.gateway.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
@Component
public class JwtUtils {


public static final String SECRET = "2c8c2e88030791c05184ef724749983de9a7a90785a32f6b0d8e42acd09a7601";

public void validateToken(final String token){
    Jwts.parserBuilder()
            .setSigningKey(getSignKey())
            .build()
            .parseClaimsJws(token);
}

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
