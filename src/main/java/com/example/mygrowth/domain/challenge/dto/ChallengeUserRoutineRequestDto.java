package com.example.mygrowth.domain.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChallengeUserRoutineRequestDto {
    private final String title;
    private final String description;
}
