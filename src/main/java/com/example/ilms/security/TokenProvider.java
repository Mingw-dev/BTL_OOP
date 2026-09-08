package com.example.ilms.security;

import com.example.ilms.entity.User;
import com.example.ilms.service.JwtService;
import com.example.ilms.util.CookieUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TokenProvider {
    public static final String ACCESS_TOKEN_COOKIE = "access-token";
    public static final String REFRESH_TOKEN_COOKIE = "refresh-token";

    private final JwtService jwtService;

    @Value("${jwt.access-token.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    public TokenProvider(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());

        if (user.getRole() != null) {
            claims.put("role", user.getRole().getCode());
        }

        return jwtService.generateToken(claims, user.getId().toString(), accessTokenExpiration);
    }

    public String generateRefreshToken(User user) {
        return jwtService.generateToken(new HashMap<>(), user.getId().toString(), refreshTokenExpiration);
    }

    public ResponseCookie createAccessTokenCookie(String token) {
        long maxAgeSeconds = accessTokenExpiration / 1000;
        return CookieUtils.createHttpOnlyCookie(ACCESS_TOKEN_COOKIE, token, maxAgeSeconds);
    }

    public ResponseCookie createRefreshTokenCookie(String token) {
        long maxAgeSeconds = refreshTokenExpiration / 1000;
        return CookieUtils.createHttpOnlyCookie(REFRESH_TOKEN_COOKIE, token, maxAgeSeconds);
    }

    public ResponseCookie getCleanAccessTokenCookie() {
        return CookieUtils.deleteCookie(ACCESS_TOKEN_COOKIE);
    }

    public ResponseCookie getCleanRefreshTokenCookie() {
        return CookieUtils.deleteCookie(REFRESH_TOKEN_COOKIE);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        return jwtService.isTokenValid(token, userDetails);
    }

    public String extractId(String token) {
        return jwtService.extractId(token);
    }
}