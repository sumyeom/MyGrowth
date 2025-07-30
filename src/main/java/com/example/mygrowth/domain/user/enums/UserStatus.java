package com.example.mygrowth.domain.user.enums;

import lombok.Getter;

@Getter
public enum UserStatus {
    ACTIVE("active"),
    WITHDRAW("withdraw");

    private final String name;
    UserStatus(String name) {this.name = name;}
}
