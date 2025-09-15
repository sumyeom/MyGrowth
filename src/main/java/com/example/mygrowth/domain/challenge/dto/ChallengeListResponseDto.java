package com.example.mygrowth.domain.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ChallengeListResponseDto {
    private final String title;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final boolean active;
}
