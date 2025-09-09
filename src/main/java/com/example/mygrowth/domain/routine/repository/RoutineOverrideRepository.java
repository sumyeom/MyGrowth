package com.example.mygrowth.domain.routine.repository;

import com.example.mygrowth.domain.routine.entity.RoutineOverride;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface RoutineOverrideRepository extends JpaRepository<RoutineOverride, Integer> {
    Optional<RoutineOverride> findByRoutineIdAndDate(Long id, LocalDate date);
}
