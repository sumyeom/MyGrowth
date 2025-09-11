package com.example.mygrowth.domain.routine.repository;

import com.example.mygrowth.domain.routine.dto.RoutineStatsDto;
import com.example.mygrowth.domain.routine.entity.Routine;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface RoutineRepository extends JpaRepository<Routine, Long> {
    Page<Routine> findByUserId(Pageable pageable, Long id);
    List<Routine> findByUserId(Long id);

    @Query("""
        SELECT new com.example.mygrowth.domain.routine.dto.RoutineStatsDto(SUM(DISTINCT r.goalCount),COUNT(rl.id))
        FROM Routine r
        LEFT JOIN RoutineLog rl ON rl.routine.id = r.id
            AND rl.date BETWEEN :startDate AND :endDate
        WHERE r.user.id = :userId
            AND r.startDate <= :endDate
            AND (r.endDate IS NULL OR r.endDate >= :startDate)
    """)
    RoutineStatsDto getRoutineStats(@Param("userId") Long userId,
                                    @Param("startDate")LocalDate startDate,
                                    @Param("endDate")LocalDate endDate);

    @Query("""
        SELECT MIN(r.startDate)
            FROM Routine r
            WHERE r.user.id = :userId
    """)
    LocalDate findMinStartDate(@Param("userId")Long userId);
}
