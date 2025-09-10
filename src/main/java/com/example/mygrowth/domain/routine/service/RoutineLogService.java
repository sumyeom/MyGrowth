package com.example.mygrowth.domain.routine.service;

import com.example.mygrowth.domain.routine.dto.RoutineCheckResultDto;
import com.example.mygrowth.domain.routine.entity.Routine;
import com.example.mygrowth.domain.routine.entity.RoutineLog;
import com.example.mygrowth.domain.routine.repository.RoutineLogRepository;
import com.example.mygrowth.domain.routine.repository.RoutineRepository;
import com.example.mygrowth.domain.user.entity.User;
import com.example.mygrowth.global.constant.ErrorCode;
import com.example.mygrowth.global.exception.ApiException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.mygrowth.domain.routine.util.RoutineValidationUtils.validateRepeatType;

@Service
@RequiredArgsConstructor
public class RoutineLogService {
    private final RoutineRepository routineRepository;
    private final RoutineLogRepository routineLogRepository;

    @Transactional
    public RoutineCheckResultDto routineCheckin(Long routineId, LocalDate targetDate, User loginUser) {
        // 루틴 확인
        Routine findRoutine = validateRoutineAccess(routineId, loginUser);

        validateCheckinDate(findRoutine, targetDate);

        return toggleRoutineLog(findRoutine, loginUser, targetDate);
    }

    private Routine validateRoutineAccess(Long routineId, User loginUser) {
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new ApiException(ErrorCode.ROUTINE_NOT_FOUND));

        if(!routine.getUser().getId().equals(loginUser.getId())) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        return routine;
    }

    private void validateCheckinDate(Routine routine, LocalDate targetDate) {
        LocalDate today = LocalDate.now();

//        if(targetDate.isAfter(today)) {
//            throw new ApiException(ErrorCode.INVALID_DATE);
//        }

        // 과거 너무 오래된 날짜 제한
        if(targetDate.isBefore(today.minusDays(50))) {
            throw new ApiException(ErrorCode.TOO_OLD_DATE);
        }

        // 루틴 반복 규칙 검증
        validateRepeatType(routine, targetDate);
    }

    private RoutineCheckResultDto toggleRoutineLog(Routine routine, User loginUser, LocalDate targetDate) {
        // 로그 조회
        Optional<RoutineLog> existLog = routineLogRepository
                .findByRoutineIdAndDate(routine.getId(), targetDate);

        // 존재하면 삭제
        if(existLog.isPresent()) {
            routineLogRepository.delete(existLog.get());
            return new RoutineCheckResultDto(false, "루틴 체크를 해제하였습니다.");
        }else{
            // 없으면 생성
            RoutineLog newLog = new RoutineLog(routine, loginUser, targetDate, true);
            routineLogRepository.save(newLog);

            // 연속 달성일 계산
            int streakDays = calculateStreakDays(routine.getId(), targetDate);

            return new RoutineCheckResultDto(true, "루틴을 완료했습니다.",streakDays);
        }
    }

    private int calculateStreakDays(Long routineId, LocalDate endDate) {
        // 연속 성공일 계산 로직
        List<RoutineLog> recentLogs = routineLogRepository.findByRoutineIdAndDateBetweenOrderByDateDesc(
                routineId,
                endDate.minusDays(365),
                endDate
        );

        int streak = 0;
        LocalDate checkDate = endDate;

        for(RoutineLog log : recentLogs) {
            if(log.getDate().equals(checkDate) && log.isSuccess()) {
                streak++;
                checkDate = checkDate.minusDays(1);
            }else if(log.getDate().equals(checkDate)){
                break;
            }
        }
        return streak;
    }
}
