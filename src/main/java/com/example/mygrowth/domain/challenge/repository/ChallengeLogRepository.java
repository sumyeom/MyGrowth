package com.example.mygrowth.domain.challenge.repository;

import com.example.mygrowth.domain.challenge.entity.ChallengeLog;
import com.example.mygrowth.domain.challenge.entity.ChallengeParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface ChallengeLogRepository extends JpaRepository<ChallengeLog, Integer> {

    boolean existsByChallengeParticipantAndDate(ChallengeParticipant participant, LocalDate now);
}
