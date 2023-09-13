package com.example.auth.service;

import io.jsonwebtoken.ExpiredJwtException;
import java.util.Map;

public interface JwtService {

    void validateToken(final String token) throws ExpiredJwtException, IllegalArgumentException;
    String generateToken(String username, int exp);
    String createToken(Map<String,Object> claims, String username,int exp);
    String getSubject(final String token);
    String refreshToken(final String token, int exp);
}
