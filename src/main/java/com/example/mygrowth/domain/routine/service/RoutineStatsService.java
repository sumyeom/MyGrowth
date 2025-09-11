package com.example.mygrowth.domain.routine.service;

import com.example.mygrowth.domain.routine.dto.RoutineStatsDto;
import com.example.mygrowth.domain.routine.dto.RoutineSuccessRate;
import com.example.mygrowth.domain.routine.entity.Routine;
import com.example.mygrowth.domain.routine.entity.RoutineLog;
import com.example.mygrowth.domain.routine.repository.RoutineLogRepository;
import com.example.mygrowth.domain.routine.repository.RoutineRepository;
import com.example.mygrowth.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoutineStatsService {
    private final RoutineRepository routineRepository;

    public RoutineSuccessRate getSuccessRate(User loginUser) {

        double successRateWeekly = 0;
        double successRateMonthly = 0;

        LocalDate today = LocalDate.now();
        // 이번 주 토요일
        LocalDate endDay = today.with(DayOfWeek.SATURDAY);
        // 저번 주 일요일
        LocalDate startDay = endDay.minusWeeks(1).with(DayOfWeek.SUNDAY);

        YearMonth yearMonth = YearMonth.from(today);

        // 이번달 시작
        LocalDate monthStartDay = yearMonth.atDay(1);

        // 이번달 종료
        LocalDate monthEndDay = yearMonth.atEndOfMonth();

        long totalDays = ChronoUnit.DAYS.between(monthStartDay, monthEndDay) + 1;
        double weeks = Math.ceil((double) totalDays / 7.0);
        long weekCount = (long) weeks;
        long weeklyGoal = 0;
        long weeklySuccess = 0;

        try{
            RoutineStatsDto weeklyStats = routineRepository.getRoutineStats(loginUser.getId(), startDay, endDay);
            RoutineStatsDto monthlyStats = routineRepository.getRoutineStats(loginUser.getId(), startDay, endDay);

            weeklyGoal = Optional.ofNullable(weeklyStats.getTotalGoalCount()).orElse(0L);
            weeklySuccess = Optional.ofNullable(weeklyStats.getTotalLogCount()).orElse(0L);
            long monthlyGoal = Optional.ofNullable(monthlyStats.getTotalGoalCount()).orElse(0L) * weekCount;
            long monthlySuccess = Optional.ofNullable(monthlyStats.getTotalLogCount()).orElse(0L);


            successRateWeekly = weeklySuccess == 0 ? 0.0 : (double) weeklySuccess / weeklyGoal * 100;
            successRateMonthly = monthlySuccess == 0 ? 0.0 : (double) monthlySuccess / monthlyGoal * 100;

            if(weeklySuccess == 0 || monthlySuccess == 0){
                log.info("사용자 {} 지난주 루틴 체크인 없음", loginUser.getEmail());
            }

        } catch(Exception e){
            log.error("{} 사용자 루틴 통계 생성 중 오류 : ",loginUser.getEmail(), e);
        }

        return new RoutineSuccessRate(successRateWeekly, successRateMonthly, weeklyGoal, weeklySuccess);
    }
}
