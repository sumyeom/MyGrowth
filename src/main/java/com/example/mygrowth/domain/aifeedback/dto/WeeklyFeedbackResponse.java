package com.example.mygrowth.domain.aifeedback.dto;

public record WeeklyFeedbackResponse(
        String summary,   // 이번 주 요약
        String weakDay,   // 가장 취약한 요일
        String advice     // 다음 주 전략 조언
) {}
