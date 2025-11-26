package com.example.mygrowth.domain.aifeedback.service;

import com.example.mygrowth.domain.aifeedback.dto.UserWeeklyData;
import com.example.mygrowth.domain.aifeedback.dto.WeeklyFeedbackResponse;
import com.example.mygrowth.domain.aifeedback.dto.WeeklyStats;
import com.example.mygrowth.domain.routine.entity.RoutineLog;
import com.example.mygrowth.domain.routine.repository.RoutineLogRepository;
import com.example.mygrowth.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
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
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.SUNDAY);
        LocalDate weekEnd = today.with(DayOfWeek.SATURDAY);

        List<RoutineLog> logs = routineLogRepository.findByRoutine_User_IdAndDateBetween(
                user.getId(), weekStart, weekEnd
        );

        // 요일별 성공 횟수
        Map<String, Integer> dailySuccess = new HashMap<>();
        for (LocalDate date = weekStart; !date.isAfter(weekEnd); date = date.plusDays(1)) {
            LocalDate finalDate = date;
            int success = (int) logs.stream()
                    .filter(log -> log.getDate().equals(finalDate) && log.isSuccess())
                    .count();
            dailySuccess.put(date.getDayOfWeek().name(), success);
        }

        int totalSuccessCount = (int) logs.stream().filter(RoutineLog::isSuccess).count();
        int totalSuccessRate = logs.isEmpty() ? 0 : (int) ((double) totalSuccessCount / logs.size() * 100);

        int targetRoutineCount = user.getRoutines().size();

        WeeklyStats stats = new WeeklyStats(dailySuccess, totalSuccessRate, targetRoutineCount);
        UserWeeklyData userWeeklyData = new UserWeeklyData(user.getEmail(), stats);

        return aiFeedbackService.generateWeeklyFeedback(userWeeklyData);
    }
}
