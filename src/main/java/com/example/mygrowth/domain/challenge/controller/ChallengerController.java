package com.example.mygrowth.domain.challenge.controller;

import com.example.mygrowth.domain.challenge.dto.ChallengeListResponseDto;
import com.example.mygrowth.domain.challenge.dto.ChallengeRequestDto;
import com.example.mygrowth.domain.challenge.dto.ChallengeResponseDto;
import com.example.mygrowth.domain.challenge.service.ChallengeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Challenge", description = "챌린지 관련 CRUD")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/challenges")
class ChallengerController {
    private final ChallengeService challengeService;

    /**
     * 챌린지 생성 API
     * @param requestDto 챌린지 생성 dto
     * @return 챌린지 정보 dto
     */
    @PostMapping
    @Operation(summary = "챌린지 생성 - Admin", description = "유저들이 참가할 챌린지를 생성합니다.")
    @ApiResponse(responseCode = "201", description = "챌린지 생성 성공")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ChallengeResponseDto> createChallenge(
            @RequestBody @Schema(implementation = ChallengeRequestDto.class) ChallengeRequestDto requestDto
    ){
        ChallengeResponseDto responseDto = challengeService.createChallenge(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * 챌린지 단건 조회 API
     * @param id 챌린지 id
     * @return 챌린지 정보 dto
     */
    @GetMapping("/{id}")
    @Operation(summary="챌린지 조회", description = "챌린지 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "챌린지 정보 단건 조회 성공")
    public ResponseEntity<ChallengeResponseDto> findByIdChallenge(
        @PathVariable Long id
    ){
        ChallengeResponseDto responseDto = challengeService.findByIdChallenge(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * 챌린지 리스트 조회 API
     * @param page 페이지 번호
     * @return 챌린지 리스트 dto
     */
    @GetMapping
    @Operation(summary="챌린지 조회", description = "챌린지 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "챌린지 전체 조회 성공")
    public ResponseEntity<Page<ChallengeListResponseDto>> findAllChallenges(
            @RequestParam(value = "page", defaultValue = "1") @Parameter(description = "페이지 번호") int page
    ){
        Page<ChallengeListResponseDto> responseDtos = challengeService.findAllChallenges(page);
        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }


    /**
     * 챌린지 수정 API
     * @param id 챌린지 id
     * @param requestDto 챌린지 정보 수정 dto
     * @return void
     */
    @PatchMapping("/{id}")
    @Operation(summary = "챌린지 수정 - Admin", description = "챌린지를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "챌린지 수정 성공")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateChallenge(
            @PathVariable Long id,
            @RequestBody @Schema(implementation = ChallengeRequestDto.class) ChallengeRequestDto requestDto
    ){
        challengeService.updateChallenge(id, requestDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 챌린지 삭제 API
     * @param id 챌린지 id
     * @return void
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "챌린지 삭제 - Admin", description = "챌린지를 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "챌린지 삭제 성공")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteChallenge(
            @PathVariable Long id
    ){
        challengeService.deleteChallenge(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
