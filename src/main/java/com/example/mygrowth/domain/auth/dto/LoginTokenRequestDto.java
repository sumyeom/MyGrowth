package com.example.mygrowth.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginTokenRequestDto {
    @NotBlank(message = "이메일을 입력해주세요")
    private final String email;

    @NotBlank(message = "비밀번호를 입력해주세요")
    private final String password;
}
