package com.example.mygrowth.domain.routine.dto;

import com.example.mygrowth.domain.routine.enums.RepeatType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class RoutineCreateResponseDto {
    private final String title;
    private final String description;
    private final RepeatType repeatType;
    private final List<DayOfWeek> dayOfWeek;
    private final int dayOfMonth;
    private final String dayOfYear;
    private final int goalCount;
    private final boolean isPublic;
    private final LocalDate startDate;
    private final LocalDate endDate;

}
