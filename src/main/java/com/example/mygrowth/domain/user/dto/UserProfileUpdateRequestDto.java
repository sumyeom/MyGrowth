package com.example.mygrowth.domain.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserProfileUpdateRequestDto {
    private String name;
    private String nickname;
    private String selfIntroduction;
}
