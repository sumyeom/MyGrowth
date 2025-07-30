package com.example.mygrowth.domain.auth.dto;

import com.example.mygrowth.domain.user.entity.User;
import lombok.Getter;

@Getter
public class SignupResponseDto {
    private final Long id;
    private final String name;
    private final String email;

    public SignupResponseDto(User user){
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
    }
}
