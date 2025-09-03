package com.example.mygrowth.domain.aifeedback.controller;

import com.example.mygrowth.domain.aifeedback.dto.UserWeeklyData;
import com.example.mygrowth.domain.aifeedback.dto.WeeklyFeedbackResponse;
import com.example.mygrowth.domain.aifeedback.service.AiFeedbackService;
import com.example.mygrowth.domain.aifeedback.service.WeeklyFeedbackService;
import com.example.mygrowth.global.config.auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai/weekly-feedback")
public class WeeklyFeedbackController {
    private final WeeklyFeedbackService weeklyFeedbackService;

    @GetMapping
    public WeeklyFeedbackResponse getWeeklyFeedback(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        return weeklyFeedbackService.generateFeedback(customUserDetails.getUser());
    }
}
