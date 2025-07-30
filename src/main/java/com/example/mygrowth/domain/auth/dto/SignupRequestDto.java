package com.example.mygrowth.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SignupRequestDto {

    @NotBlank(message = "이메일을 입력해주세요")
    @Email(message = "이메일 형식을 확인해주세요.")
    private final String email;

    @NotBlank(message = "이름을 입력해주세요")
    private final String name;

    @NotBlank(message = "닉네임을 입력해주세요")
    private final String nickname;

    @NotBlank(message = "비밀번호를 입력해주세요")
    private final String password;
}
