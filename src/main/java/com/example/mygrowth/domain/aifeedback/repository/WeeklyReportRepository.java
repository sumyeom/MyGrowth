package com.example.mygrowth.domain.aifeedback.repository;

import com.example.mygrowth.domain.aifeedback.entity.WeeklyReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeeklyReportRepository extends JpaRepository<WeeklyReport, Long> {
    Optional<WeeklyReport> findTopByUserIdOrderByEndDateDesc(Long id);
}
