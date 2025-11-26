package com.example.mygrowth.domain.challenge.repository;

import com.example.mygrowth.domain.challenge.entity.ChallengeParticipant;
import com.example.mygrowth.domain.challenge.entity.ChallengeUserRoutine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChallengeUserRoutineRepository extends JpaRepository<ChallengeUserRoutine, Long> {
    Optional<ChallengeUserRoutine> findByChallengeParticipant(ChallengeParticipant participant);
}
