package com.example.mygrowth.domain.routine.dto;

import com.example.mygrowth.domain.routine.enums.RepeatType;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Getter
public class RoutineFindOneResponseDto {
    private final Long id;
    private final String title;
    private final String description;
    private final RepeatType repeatType;
    private final List<DayOfWeek> daysOfWeek;
    private final int dayOfMonth;
    private final String dayOfYear;
    private final int goalCount;
    private final boolean isPublic;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public RoutineFindOneResponseDto(Long id, String title, String description, RepeatType repeatType, List<DayOfWeek> daysOfWeek, int dayOfMonth, String dayOfYear, int goalCount, boolean isPublic, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.repeatType = repeatType;
        this.daysOfWeek = daysOfWeek;
        this.dayOfMonth = dayOfMonth;
        this.dayOfYear = dayOfYear;
        this.goalCount = goalCount;
        this.isPublic = isPublic;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
