package com.example.mygrowth.domain.routine.repository;

import com.example.mygrowth.domain.routine.entity.Routine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoutineRepository extends JpaRepository<Routine, Long> {
    Page<Routine> findByUserId(Pageable pageable, Long id);
    List<Routine> findByUserId(Long id);
}
