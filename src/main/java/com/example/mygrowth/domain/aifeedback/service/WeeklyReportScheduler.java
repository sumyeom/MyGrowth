package com.example.mygrowth.domain.aifeedback.service;

import com.example.mygrowth.domain.aifeedback.dto.UserWeeklyData;
import com.example.mygrowth.domain.aifeedback.dto.WeeklyFeedbackResponse;
import com.example.mygrowth.domain.aifeedback.entity.WeeklyReport;
import com.example.mygrowth.domain.aifeedback.repository.WeeklyReportRepository;
import com.example.mygrowth.domain.routine.entity.RoutineLog;
import com.example.mygrowth.domain.routine.repository.RoutineLogRepository;
import com.example.mygrowth.domain.user.entity.User;
import com.example.mygrowth.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeeklyReportScheduler {
    private final UserRepository userRepository;
    private final RoutineLogRepository routineLogRepository;
    private final WeeklyFeedbackService weeklyFeedbackService;
    private final WeeklyReportRepository weeklyReportRepository;

    // 매주 일요일 밤 23:59에 실행
    //@Scheduled(cron = "0 59 23 * * SUN")
    //@Scheduled(fixedDelay  = 300000)
    @Transactional
    public void generateWeeklyReports(){
        // 1. 지난주 날짜 범위 계산
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.minusWeeks(0).with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        List<User> users = userRepository.findAll();

        for(User user: users){

           try{
               // 지난주 로그 조회
               List<RoutineLog> logs = routineLogRepository.findByRoutine_User_IdAndDateBetween(
                       user.getId(), startOfWeek, endOfWeek
               );

               if(logs. isEmpty()){
                   log.info("사용자 {} 지난주 로그 없음 -> 스킵", user.getEmail());
                   continue;
               }

               // 성공율 계산
               int totalLogs = logs.size();
               long successCount = logs.stream().filter(RoutineLog::isSuccess).count();
               double successRate = (double) successCount * 100 / totalLogs;

               // 가장 성취율 낮은 낮은 요일 계산
               Map<DayOfWeek, Long> successByDay = logs.stream()
                       .collect(Collectors.groupingBy(
                               log -> log.getDate().getDayOfWeek(),
                               Collectors.filtering(RoutineLog::isSuccess, Collectors.counting())
                       ));

               DayOfWeek weakDay = successByDay.entrySet().stream()
                       .min(Comparator.comparingLong(Map.Entry::getValue))
                       .map(Map.Entry::getKey)
                       .orElse(null);

               // AI 피드백
               WeeklyFeedbackResponse feedback = weeklyFeedbackService.generateFeedback(user);

               // WeeklyReport 저장
               WeeklyReport report = new WeeklyReport(
                       user,
                       startOfWeek,
                       endOfWeek,
                       successRate,
                       weakDay,
                       feedback.message()
               );

               weeklyReportRepository.save(report);
               log.info("{} 사용자 주간 리포트 저장 완료", user.getEmail());

           } catch(Exception e){
               log.error("X {} 사용자 리포트 생성 중 오류", user.getEmail(), e);
           }
        }

    }
}
