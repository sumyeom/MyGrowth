package com.example.mygrowth.domain.routine.controller;

import com.example.mygrowth.domain.routine.dto.RoutineCheckResultDto;
import com.example.mygrowth.domain.routine.entity.RoutineLog;
import com.example.mygrowth.domain.routine.service.RoutineLogService;
import com.example.mygrowth.global.config.auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/routines")
@RequiredArgsConstructor
public class RoutineLogController {
    private final RoutineLogService routineLogService;

    @PostMapping("/{routineId}/checkin")
    public ResponseEntity<RoutineCheckResultDto> routineCheckin(
            @PathVariable Long routineId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        RoutineCheckResultDto resultDto = routineLogService.routineCheckin(routineId, targetDate, customUserDetails.getUser());
        return new ResponseEntity<>(resultDto,HttpStatus.OK);
    }
}
