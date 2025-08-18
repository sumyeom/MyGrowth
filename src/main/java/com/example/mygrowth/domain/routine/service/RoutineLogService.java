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

@Service
@RequiredArgsConstructor
public class RoutineLogService {
    private final RoutineRepository routineRepository;
    private final RoutineLogRepository routineLogRepository;

    @Transactional
    public void routineCheckin(Long routineId, User loginUser) {
        // 루틴 확인
        Routine findRoutine = routineRepository.findById(routineId)
                .orElseThrow(() -> new ApiException(ErrorCode.ROUTINE_NOT_FOUND));

        if(!findRoutine.getUser().getId().equals(loginUser.getId())) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        // 오늘 날짜
        LocalDate today = LocalDate.now();

        // 로그 조회
        Optional<RoutineLog> existLog = routineLogRepository
                .findByRoutineIdAndDate(routineId, today);


        // 존재하면 삭제
        if(existLog.isPresent()) {
            routineLogRepository.delete(existLog.get());
        }else{
            // 없으면 생성
            RoutineLog newLog = new RoutineLog(findRoutine, today, true);
            routineLogRepository.save(newLog);
        }
    }
}
