package com.example.mygrowth.domain.challenge.repository;

import com.example.mygrowth.domain.challenge.entity.ChallengeParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeParticipantRepository extends JpaRepository<ChallengeParticipant, Long> {
    boolean existsByChallengeIdAndUserId(Long id, Long id1);

    long countByChallengeId(Long id);
}
