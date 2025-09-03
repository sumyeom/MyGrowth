package com.example.mygrowth.domain.aifeedback.service;

import com.example.mygrowth.domain.aifeedback.dto.WeeklyReportDto;
import com.example.mygrowth.domain.aifeedback.entity.WeeklyReport;
import com.example.mygrowth.domain.aifeedback.repository.WeeklyReportRepository;
import com.example.mygrowth.domain.user.entity.User;
import com.example.mygrowth.global.constant.ErrorCode;
import com.example.mygrowth.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WeeklyReportService {
    private final WeeklyReportRepository  weeklyReportRepository;

    @Transactional(readOnly = true)
    public WeeklyReportDto getLatestWeeklyReport(User loginUser){
        // 최근 주간 리포트 조회(가장 최신 주차)
        WeeklyReport report = weeklyReportRepository.findTopByUserIdOrderByEndDateDesc(loginUser.getId())
                .orElseThrow(() -> new ApiException(ErrorCode.REPORT_NOT_FOUND));

        return new WeeklyReportDto(
                report.getStartDate(),
                report.getEndDate(),
                report.getSuccessRate(),
                report.getWeakDay(),
                report.getAiFeedback()
        );

    }
}
