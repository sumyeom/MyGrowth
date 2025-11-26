package com.example.mygrowth.domain.challenge.controller;

import com.example.mygrowth.domain.challenge.dto.ChallengeUserRoutineRequestDto;
import com.example.mygrowth.domain.challenge.dto.ChallengeUserRoutineResponseDto;
import com.example.mygrowth.domain.challenge.service.ChallengeUserRoutineService;
import com.example.mygrowth.global.config.auth.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name="UserChallengeRoutine", description = "챌린지의 유저 루틴 관련 CRUD")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/challenges")
public class ChallengeUserRoutineController {
    private final ChallengeUserRoutineService challengeUserRoutineService;

    /**
     * 챌린지 루틴 생성 API
     * @param id 챌린지 id
     * @param requestDto 루틴 생성 dto
     * @param customUserDetails 로그인 유저
     * @return 루틴 생성 dto
     */
    @PostMapping("/{id}/my-routines")
    @Operation(summary = "챌린지 루틴 생성", description = "유저별 챌린지 루틴을 생성합니다.")
    @ApiResponse(responseCode = "200", description = "유저별 챌린지 루틴을 생성합니다.")
    public ResponseEntity<ChallengeUserRoutineResponseDto> createChallengeUserRoutine(
            @PathVariable Long id,
            @RequestBody ChallengeUserRoutineRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        ChallengeUserRoutineResponseDto responseDto = challengeUserRoutineService.createChallengeUserRoutine(id, requestDto, customUserDetails.getUser());
        return new  ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * 챌린지 루틴 조회 API
     * @param id 챌린지 id
     * @param customUserDetails 로그인 유저
     * @return 챌린지 루틴 정보 dto
     */
    @GetMapping("/{id}/my-routines")
    @Operation(summary = "챌린지 루틴 조회", description = "유저별 챌린지 루틴을 조회합니다.")
    @ApiResponse(responseCode="200",description = "유저별 챌린지 루틴을 생성합니다.")
    public ResponseEntity<ChallengeUserRoutineResponseDto> getChallengeUserRoutine(
        @PathVariable Long id,
        @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        ChallengeUserRoutineResponseDto responseDto = challengeUserRoutineService.getChallengeUserRoutine(id, customUserDetails.getUser());
        return new  ResponseEntity<>(responseDto, HttpStatus.OK);
    }

}
