package com.example.mygrowth.domain.routine.controller;

import com.example.mygrowth.domain.aifeedback.dto.WeeklyReportDto;
import com.example.mygrowth.domain.aifeedback.service.WeeklyReportService;
import com.example.mygrowth.domain.routine.dto.RoutineSuccessRate;
import com.example.mygrowth.domain.routine.service.RoutineStatsService;
import com.example.mygrowth.global.config.auth.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="Routine Statistics", description = "Routine 통계 관리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/routines/statistics")
public class RoutineStatsController {
    private final RoutineStatsService routineStatsService;
    private final WeeklyReportService weeklyReportService;

    /**
     * 주간 피드백 api
     * @param customUserDetails 로그인 유저
     * @return 피드백 DTO
     */
    @GetMapping("/weekly-summary")
    @Operation(summary = "루틴 주간 피드백", description = "루틴 주간 AI 피드백을 출력합니다.")
    @ApiResponse(responseCode = "200", description = "루틴 주간 AI 피드백 출력 성공")
    public ResponseEntity<WeeklyReportDto> getWeeklySummary(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        WeeklyReportDto dto = weeklyReportService.getLatestWeeklyReport(customUserDetails.getUser());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    /**
     * 루틴 통계 결과 API
     * @param customUserDetails 로그인할 유저
     * @return 루틴 통계 결과 dto
     */
    @GetMapping("/success-rate")
    @Operation(summary = "루틴 통계", description = "루틴 주간 / 월간 통계를 출력합니다.")
    @ApiResponse(responseCode = "200", description = "루틴 주간/월간 통계 출력 성공")
    public ResponseEntity<RoutineSuccessRate>  getSuccessRate(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        RoutineSuccessRate responseDto = routineStatsService.getSuccessRate(customUserDetails.getUser());
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
