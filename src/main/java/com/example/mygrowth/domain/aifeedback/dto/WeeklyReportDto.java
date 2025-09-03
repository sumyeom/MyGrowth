package com.example.mygrowth.domain.aifeedback.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class WeeklyReportDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private double successRate;
    private DayOfWeek dayOfWeek;
    private String aiFeedback;


}
