package com.example.mygrowth.domain.challenge.repository;

import com.example.mygrowth.domain.challenge.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
}
