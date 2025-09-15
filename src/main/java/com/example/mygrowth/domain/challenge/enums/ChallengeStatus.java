package com.example.mygrowth.domain.challenge.enums;

import lombok.Getter;

@Getter
public enum ChallengeStatus {
    ONGOING("ongoing"),
    COMPLETED("completed"),
    FAILED("failed");

    private final String value;

    ChallengeStatus(String value) {this.value = value;}
}
