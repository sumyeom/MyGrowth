package com.example.mygrowth.domain.routine.controller;

import com.example.mygrowth.domain.aifeedback.dto.WeeklyReportDto;
import com.example.mygrowth.domain.aifeedback.service.WeeklyReportService;
import com.example.mygrowth.domain.routine.dto.*;
import com.example.mygrowth.domain.routine.service.RoutineService;
import com.example.mygrowth.global.config.auth.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/routines")
public class RoutineController {
    private final RoutineService routineService;
    private final WeeklyReportService weeklyReportService;

    @PostMapping
    public ResponseEntity<RoutineCreateResponseDto> createRoutine(
            @Valid @RequestBody RoutineRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        RoutineCreateResponseDto responseDto = routineService.creatRoutine(requestDto, customUserDetails.getUser());
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoutineFindOneResponseDto> findRoutineById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        RoutineFindOneResponseDto responseDto = routineService.findRoutineById(id, customUserDetails.getUser());
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<RoutineFindResponseDto>> findRoutine(
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        List<RoutineFindResponseDto> responseDto = routineService.findRoutine(page, customUserDetails.getUser());
        if(responseDto == null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/by-date")
    public ResponseEntity<Page<RoutineFindResponseDto>> findRoutineByDate(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ){
        Page<RoutineFindResponseDto> responseDto = routineService.findRoutineByDate(page, date, customUserDetails.getUser());

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoutine(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        routineService.deleteRoutine(id, customUserDetails.getUser());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateRoutine(
            @PathVariable Long id,
            @RequestBody RoutineRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        routineService.updateRoutine(id, requestDto, customUserDetails.getUser());
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping("/weekly-summary")
    public ResponseEntity<WeeklyReportDto> getWeeklySummary(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        WeeklyReportDto dto = weeklyReportService.getLatestWeeklyReport(customUserDetails.getUser());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


}
