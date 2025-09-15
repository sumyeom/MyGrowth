package com.example.mygrowth.domain.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ChallengeResponseDto {
    private final String title;
    private final String description;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final Long targetCount;
    private final int maxParticipants;
    private final int currentParticipants;
    private final boolean active;
}
