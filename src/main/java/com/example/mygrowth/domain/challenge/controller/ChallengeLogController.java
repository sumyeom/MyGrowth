package com.example.mygrowth.domain.challenge.controller;


import com.example.mygrowth.domain.challenge.dto.ChallengeLogRequestDto;
import com.example.mygrowth.domain.challenge.dto.ChallengeLogResponseDto;
import com.example.mygrowth.domain.challenge.service.ChallengeLogService;
import com.example.mygrowth.global.config.auth.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name="ChallengeLog", description = "챌린지 참여 인증 관련 CRUD")
@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
public class ChallengeLogController {
    private final ChallengeLogService challengeLogService;

    @PostMapping("/{id}/log")
    @Operation(summary = "챌린지 참여 인증", description = "챌린지 참여에 대한 인증을 진행합니다.")
    @ApiResponse(responseCode = "200", description = "챌린지 인증 성공")
    public ResponseEntity<ChallengeLogResponseDto> createChallengeLog(
            @PathVariable Long id,
            @RequestBody ChallengeLogRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        ChallengeLogResponseDto responseDto = challengeLogService.createChallengeLog(id, requestDto, customUserDetails.getUser());
        return new  ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
