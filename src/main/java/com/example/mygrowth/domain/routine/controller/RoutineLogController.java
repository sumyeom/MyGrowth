package com.example.mygrowth.domain.routine.controller;

import com.example.mygrowth.domain.routine.dto.RoutineCheckResultDto;
import com.example.mygrowth.domain.routine.entity.RoutineLog;
import com.example.mygrowth.domain.routine.service.RoutineLogService;
import com.example.mygrowth.global.config.auth.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Routine Log", description = "Routine 체크인 관련 CRUD")
@RestController
@RequestMapping("/api/routines")
@RequiredArgsConstructor
public class RoutineLogController {
    private final RoutineLogService routineLogService;

    /**
     * 루틴 체크인 API
     * @param routineId 루틴 id
     * @param date 채크인할 날짜
     * @param customUserDetails 로그인 유저
     * @return 루틴 체크인 정보 dto
     */
    @PostMapping("/{routineId}/checkin")
    @Operation(summary = "루틴 체크인", description = "체크인할 날짜로 루틴 체크인을 생성합니다.")
    @ApiResponse(responseCode = "200", description = "루틴 체크인 성공")
    public ResponseEntity<RoutineCheckResultDto> routineCheckin(
            @PathVariable Long routineId,
            @RequestParam(required = false) @Parameter(description = "조회할 날짜") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        RoutineCheckResultDto resultDto = routineLogService.routineCheckin(routineId, targetDate, customUserDetails.getUser());
        return new ResponseEntity<>(resultDto,HttpStatus.OK);
    }
}
