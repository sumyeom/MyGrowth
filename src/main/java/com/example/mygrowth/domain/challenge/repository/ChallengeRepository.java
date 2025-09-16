package com.example.mygrowth.domain.challenge.repository;

import com.example.mygrowth.domain.challenge.entity.Challenge;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    // 비관적 락
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Challenge c WHERE c.id = :id")
    Optional<Challenge> findByIdWithPessimisticLock(@Param("id") Long id);
}
