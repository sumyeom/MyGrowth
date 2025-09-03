package com.example.mygrowth.domain.aifeedback.dto;

public record UserWeeklyData(
   String userEmail,
   WeeklyStats weeklyStats
) {}
