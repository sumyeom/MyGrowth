package com.example.mygrowth.domain.auth.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginTokenResponseDto {
    private final String accessToken;
    private final boolean isFirstLogin;
}
