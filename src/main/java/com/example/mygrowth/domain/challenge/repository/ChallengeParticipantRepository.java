package com.example.mygrowth.domain.challenge.repository;

import com.example.mygrowth.domain.challenge.entity.ChallengeParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ChallengeParticipantRepository extends JpaRepository<ChallengeParticipant, Long> {
    boolean existsByChallengeIdAndUserId(Long id, Long id1);

    long countByChallengeId(Long id);

    Optional<ChallengeParticipant> findByChallenge_IdAndUser_Id(Long challengeId, Long userId);
}
