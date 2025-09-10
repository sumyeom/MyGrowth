package com.example.mygrowth.domain.routine.repository;

import com.example.mygrowth.domain.routine.entity.RoutineLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoutineLogRepository extends JpaRepository<RoutineLog,Long> {
    Optional<RoutineLog> findByRoutineIdAndDate(Long routineId, LocalDate now);
    List<RoutineLog> findByRoutine_User_IdAndDateBetween(Long userId, LocalDate start, LocalDate end);

    List<RoutineLog> findByRoutineIdAndDateBetweenOrderByDateDesc(Long routineId, LocalDate localDate, LocalDate endDate);

    List<RoutineLog> findByRoutine_User_Id(Long id);
}
