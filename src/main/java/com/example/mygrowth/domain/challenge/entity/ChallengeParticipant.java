package com.example.mygrowth.domain.challenge.entity;

import com.example.mygrowth.domain.challenge.enums.ChallengeStatus;
import com.example.mygrowth.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name="challenge_participant")
@Getter
@NoArgsConstructor
public class ChallengeParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate joinAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChallengeStatus status;

    @Column(nullable = false)
    private int logCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    public ChallengeParticipant(LocalDate joinAt, ChallengeStatus status, User user, Challenge challenge) {
        this.joinAt = joinAt;
        this.status = status;
        this.user = user;
        this.challenge = challenge;
    }

    public void increaseLogCount(){
        this.logCount++;
    }

    public void updateStatus(ChallengeStatus status){
        this.status = status;
    }
}
