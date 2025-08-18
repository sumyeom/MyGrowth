package com.example.mygrowth.domain.routine.repository;

import com.example.mygrowth.domain.routine.entity.RoutineLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface RoutineLogRepository extends JpaRepository<RoutineLog,Long> {
    Optional<RoutineLog> findByRoutineIdAndDate(Long routineId, LocalDate now);
}
