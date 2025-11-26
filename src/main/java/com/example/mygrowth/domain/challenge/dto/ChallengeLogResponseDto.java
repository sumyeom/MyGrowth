package com.example.mygrowth.domain.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ChallengeLogResponseDto {
    private final Long id;
    private final Long challengeParticipantId;
    private final LocalDate date;
    private final String content;
}
