package com.example.mygrowth.domain.auth.service;

import com.example.mygrowth.domain.auth.dto.LoginTokenResponseDto;
import com.example.mygrowth.global.provider.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtProvider jwtProvider;

    // Access Token 발급(1시간)
    public String generateAccessTokens(String email, String role) {
        return jwtProvider.generateAccessToken(email,role);
    }

    // Refrech Token 발급(7일)
    public String generateRefreshToken(String email) {
        return jwtProvider.generateRefreshToken(email);
    }

    // Refresh Token으로 새로운 Access Token 재발급
    public String reissueAccessToken(String refreshToken) {
        String email = jwtProvider.getEmailFromToken(refreshToken);
        String role = jwtProvider.getRoleFromToken(refreshToken);
        return jwtProvider.generateAccessToken(email,role);
    }

    // Access 토큰 만료 여부 확인
    public boolean isTokenExpired(String accessToken) {
        return jwtProvider.getExpirationFromToken(accessToken) > 0;
    }

    public String getEmailFromToken(String accessToken) {
        return jwtProvider.getEmailFromToken(accessToken);
    }

}
