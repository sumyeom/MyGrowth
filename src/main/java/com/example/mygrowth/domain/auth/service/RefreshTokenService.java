package com.example.mygrowth.domain.auth.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RefreshTokenService {
    private final StringRedisTemplate redisTemplate;
    private final Duration REFRESH_TOKEN_TTL = Duration.ofDays(7);
    private final int COOKIE_MAX_AGE = 60 * 60 * 24 * 7;

    public RefreshTokenService(@Qualifier("refreshTokenRedisTemplate") StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveRefreshToken(String email, String refreshToken, HttpServletResponse response) {
        redisTemplate.opsForValue().set(getKey(email), refreshToken, REFRESH_TOKEN_TTL);
        addRefreshTokenCookie(response, refreshToken);
    }

    public String getRefreshToken(String email) {
        return redisTemplate.opsForValue().get(getKey(email));
    }

    public void removeRefreshToken(String email, HttpServletResponse response) {
        redisTemplate.delete(getKey(email));
        removeRefreshTokenCookie(response);
    }

    private String getKey(String email) {
        return "refresh_token:" + email;
    }

    private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.isHttpOnly();
        cookie.setSecure(true); // false 가능
        cookie.setPath("/");
        cookie.setMaxAge(COOKIE_MAX_AGE);
        response.addCookie(cookie);
    }

    private void removeRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refresh_token", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if(request.getCookies()!=null){
            for(Cookie cookie:request.getCookies()){
                if("refresh_token".equals(cookie.getName())){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
