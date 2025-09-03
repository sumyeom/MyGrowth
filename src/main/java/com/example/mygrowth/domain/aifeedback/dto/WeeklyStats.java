package com.example.mygrowth.domain.aifeedback.dto;

import java.util.Map;

public record WeeklyStats(
        Map<String, Integer> dailySuccess, // MON:1, TUE:0 등
        int totalSuccessRate,
        int targetRoutineCount
) {}
