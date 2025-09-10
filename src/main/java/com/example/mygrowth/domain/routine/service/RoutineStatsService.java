package com.example.mygrowth.domain.routine.service;

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
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoutineStatsService {
    private final RoutineRepository routineRepository;
    private final RoutineLogRepository routineLogRepository;
    public RoutineSuccessRate getSuccessRate(String period, User loginUser) {
        double successRateWeekly = 0;
        double successRateMonthly = 0;
        double successRateAll = 0;
        switch(period) {
            case "week" -> {
                LocalDate today = LocalDate.now();
                // 이번 주 토요일
                LocalDate endDay = today.with(DayOfWeek.SATURDAY);

                // 저번 주 일요일
                LocalDate startDay = endDay.minusWeeks(1).with(DayOfWeek.SUNDAY);

                try{
                    List<Routine> routines = routineRepository.findByUserId(loginUser.getId()).stream()
                            .filter(r-> !r.getStartDate().isAfter(endDay))
                            .filter(r -> r.getEndDate()==null || !r.getEndDate().isBefore(startDay))
                            .toList();

                    int weeklyGoal = routines.stream()
                            .mapToInt(r-> Optional.ofNullable(r.getGoalCount()).orElse(0))
                            .sum();

                    List<RoutineLog> logs = routineLogRepository.findByRoutine_User_IdAndDateBetween(loginUser.getId(), startDay, endDay);

                    int weeklySuccess = (int) logs.stream()
                            .filter(RoutineLog::isSuccess)
                            .count();

                    successRateWeekly = weeklySuccess == 0 ? 0.0 : (double) weeklySuccess / weeklyGoal * 100;

                    if(logs.isEmpty()){
                        log.info("사용자 {} 지난주 루틴 체크인 없음", loginUser.getEmail());
                    }

                } catch(Exception e){
                    log.error("{} 사용자 루틴 통계 생성 중 오류 : ",loginUser.getEmail(), e);
                }
            }
            case "month" -> {
                LocalDate today = LocalDate.now();
                YearMonth yearMonth = YearMonth.from(today);

                // 이번달 시작
                LocalDate startDay = yearMonth.atDay(1);

                // 이번달 종료
                LocalDate endDay = yearMonth.atEndOfMonth();

                try{
                    List<Routine> routines = routineRepository.findByUserId(loginUser.getId()).stream()
                            .filter(r-> !r.getStartDate().isAfter(endDay))
                            .filter(r -> r.getEndDate()==null || !r.getEndDate().isBefore(startDay))
                            .toList();

                    int weeklyGoal = routines.stream()
                            .mapToInt(r-> Optional.ofNullable(r.getGoalCount()).orElse(0))
                            .sum();

                    List<RoutineLog> logs = routineLogRepository.findByRoutine_User_IdAndDateBetween(loginUser.getId(), startDay, endDay);

                    int monthlySuccess = (int) logs.stream()
                            .filter(RoutineLog::isSuccess)
                            .count();

                    successRateMonthly = monthlySuccess == 0 ? 0.0 : (double) monthlySuccess / weeklyGoal * 100;

                    if(logs.isEmpty()){
                        log.info("사용자 {} 지난주 루틴 체크인 없음", loginUser.getEmail());
                    }

                } catch(Exception e){
                    log.error("{} 사용자 루틴 통계 생성 중 오류 : ",loginUser.getEmail(), e);
                }
            }
            case "all" -> {
                List<Routine> routines = routineRepository.findByUserId(loginUser.getId());

                int weeklyGoal = routines.stream()
                        .mapToInt(r->Optional.ofNullable(r.getGoalCount()).orElse(0))
                        .sum();

                List<RoutineLog> logs = routineLogRepository.findByRoutine_User_Id(loginUser.getId());
                int allSuccess = (int) logs.stream()
                        .filter(RoutineLog::isSuccess)
                        .count();

                successRateAll = allSuccess == 0 ? 0.0 : (double) allSuccess / weeklyGoal * 100;
            }
        }
        return new RoutineSuccessRate(successRateWeekly, successRateMonthly, successRateAll);
    }
}
