package com.example.mygrowth.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class UserProfileResponseDto {
    private String name;
    private String nickname;
    private String selfIntroduction;

}
