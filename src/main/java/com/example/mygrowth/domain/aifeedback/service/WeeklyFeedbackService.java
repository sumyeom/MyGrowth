package com.example.mygrowth.domain.aifeedback.service;

import com.example.mygrowth.domain.aifeedback.dto.UserWeeklyData;
import com.example.mygrowth.domain.aifeedback.dto.WeeklyFeedbackResponse;
import com.example.mygrowth.domain.aifeedback.dto.WeeklyStats;
import com.example.mygrowth.domain.routine.entity.RoutineLog;
import com.example.mygrowth.domain.routine.repository.RoutineLogRepository;
import com.example.mygrowth.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WeeklyFeedbackService {
    private final RoutineLogRepository routineLogRepository;
    private final AiFeedbackService aiFeedbackService;

    public WeeklyFeedbackResponse generateFeedback(User user){

        // 이번 주 기준 : 현재 요일로부터 7일전
        LocalDate today =  LocalDate.now();
        LocalDate weekStart = today.minusDays(6);
        LocalDate weekEnd = today;

        List<RoutineLog> logs = routineLogRepository.findByRoutine_User_IdAndDateBetween(
                user.getId(),weekStart,weekEnd
        );

        // 요일별 성공/실패 계산
        Map<String, Integer> dailySuccess = new HashMap<>();
        for(int i=0;i<7;i++){
            LocalDate date = weekStart.plusDays(i);
            int success = (int) logs.stream()
                    .filter(log -> log.getDate().equals(date) && log.isSuccess())
                    .count();
            dailySuccess.put(date.getDayOfWeek().name(), success);
        }

        // 총 성공률
        int totalSuccessCount = (int) logs.stream().filter(RoutineLog::isSuccess).count();
        int totalSuccessRate = logs.isEmpty() ? 0 : totalSuccessCount * 100 / logs.size();

        int targetRoutineCount = user.getRoutines().size();

        WeeklyStats stats = new WeeklyStats(dailySuccess, totalSuccessRate, targetRoutineCount);
        UserWeeklyData userWeeklyData = new UserWeeklyData(user.getEmail(), stats);

        // AI 호출
        return aiFeedbackService.generateWeeklyFeedback(userWeeklyData);

    }
}
