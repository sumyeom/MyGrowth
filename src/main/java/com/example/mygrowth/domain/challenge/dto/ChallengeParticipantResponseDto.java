package com.example.mygrowth.domain.challenge.dto;

import com.example.mygrowth.domain.challenge.entity.ChallengeParticipant;
import com.example.mygrowth.domain.challenge.enums.ChallengeStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ChallengeParticipantResponseDto {
    private Long participantId;
    private Long challengeId;
    private Long userId;
    private LocalDate joinAt;
    private ChallengeStatus status;

    public static ChallengeParticipantResponseDto from(ChallengeParticipant participant){
        return new ChallengeParticipantResponseDto(
                participant.getId(),
                participant.getChallenge().getId(),
                participant.getUser().getId(),
                participant.getJoinAt(),
                participant.getStatus()
        );
    }
}
