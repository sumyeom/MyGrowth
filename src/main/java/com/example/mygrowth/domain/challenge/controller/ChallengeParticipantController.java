package com.example.mygrowth.domain.challenge.controller;

import com.example.mygrowth.domain.challenge.dto.ChallengeParticipantResponseDto;
import com.example.mygrowth.domain.challenge.service.ChallengeParticipantService;
import com.example.mygrowth.global.config.auth.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="ChallengeParticipant", description = "챌린지 참여 관련 CRUD")
@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
public class ChallengeParticipantController {
    private final ChallengeParticipantService challengeParticipantService;

    /**
     * 챌린지 참가 API
     * @param id 챌린지 id
     * @param customUserDetails 로그인 유저
     * @return 챌린지 참가 정보 dto
     */
    @PostMapping("/{id}/join")
    @Operation(summary = "챌린지 참가", description = "사용자가 챌린지에 참가합니다.")
    @ApiResponse(responseCode = "200", description = "챌린지 참가 성공")
    public ResponseEntity<ChallengeParticipantResponseDto> joinChallenge(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        ChallengeParticipantResponseDto responseDto = challengeParticipantService.joinChallenge(id, customUserDetails.getUser());
        return new  ResponseEntity<>(responseDto, HttpStatus.OK);
    }

}
