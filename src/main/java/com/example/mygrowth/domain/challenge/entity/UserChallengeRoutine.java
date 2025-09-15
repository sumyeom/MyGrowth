package com.example.mygrowth.domain.challenge.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="user_challenge_routine")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserChallengeRoutine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_participant_id")
    private ChallengeParticipant challengeParticipant;
}
