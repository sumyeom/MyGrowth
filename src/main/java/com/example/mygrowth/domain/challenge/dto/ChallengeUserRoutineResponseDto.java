package com.example.mygrowth.domain.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChallengeUserRoutineResponseDto {
    private final Long id;
    private final String title;
    private final String description;
}
