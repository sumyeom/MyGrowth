package com.example.mygrowth.domain.challenge.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="challenge_user_routine")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeUserRoutine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_participant_id", unique = true)
    private ChallengeParticipant challengeParticipant;
}
