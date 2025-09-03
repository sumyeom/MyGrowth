package com.example.mygrowth.domain.routine.service;

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
import java.util.Optional;

import static com.example.mygrowth.domain.routine.util.RoutineValidationUtils.validateRepeatType;

@Service
@RequiredArgsConstructor
public class RoutineLogService {
    private final RoutineRepository routineRepository;
    private final RoutineLogRepository routineLogRepository;

    @Transactional
    public void routineCheckin(Long routineId, LocalDate targetDate, User loginUser) {
        // 루틴 확인
        Routine findRoutine = routineRepository.findById(routineId)
                .orElseThrow(() -> new ApiException(ErrorCode.ROUTINE_NOT_FOUND));

        if(!findRoutine.getUser().getId().equals(loginUser.getId())) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        LocalDate today = LocalDate.now();

        // 미래 날짜 체크 제한
        if(targetDate.isAfter(today)) {
            throw new ApiException(ErrorCode.INVALID_DATE);
        }

        // 루틴 반복 규칙 검증
        validateRepeatType(findRoutine, targetDate);

        // 로그 조회
        Optional<RoutineLog> existLog = routineLogRepository
                .findByRoutineIdAndDate(routineId, targetDate);


        // 존재하면 삭제
        if(existLog.isPresent()) {
            routineLogRepository.delete(existLog.get());
        }else{
            // 없으면 생성
            RoutineLog newLog = new RoutineLog(findRoutine, loginUser, targetDate, true);
            routineLogRepository.save(newLog);
        }
    }
}
