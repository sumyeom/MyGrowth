package com.example.mygrowth.domain.routine.controller;

import com.example.mygrowth.domain.routine.entity.RoutineLog;
import com.example.mygrowth.domain.routine.service.RoutineLogService;
import com.example.mygrowth.global.config.auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/routine-logs/check")
@RequiredArgsConstructor
public class RoutineLogController {
    private final RoutineLogService routineLogService;

    @PostMapping()
    public ResponseEntity<Void> routineCheckin(
            @RequestParam Long routineId,
            @RequestParam(required = false) LocalDate date,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        routineLogService.routineCheckin(routineId, targetDate, customUserDetails.getUser());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
