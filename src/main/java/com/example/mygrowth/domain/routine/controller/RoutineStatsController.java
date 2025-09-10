package com.example.mygrowth.domain.routine.controller;

import com.example.mygrowth.domain.routine.dto.RoutineSuccessRate;
import com.example.mygrowth.domain.routine.service.RoutineStatsService;
import com.example.mygrowth.global.config.auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics")
public class RoutineStatsController {
    private final RoutineStatsService routineStatsService;
    @GetMapping("/success-rate")
    public ResponseEntity<RoutineSuccessRate>  getSuccessRate(
            @RequestParam(required = false)  String period,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        RoutineSuccessRate responseDto = routineStatsService.getSuccessRate(period, customUserDetails.getUser());
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
