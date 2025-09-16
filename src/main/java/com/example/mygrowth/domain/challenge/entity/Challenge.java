package com.example.mygrowth.domain.challenge.entity;

import com.example.mygrowth.domain.challenge.dto.ChallengeRequestDto;
import com.example.mygrowth.global.common.BaseCreatedEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name="challenge")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Challenge extends BaseCreatedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Long targetCount;

    @Column(nullable = false)
    private int maxParticipants;

    @Column(nullable = false)
    private int currentParticipants;

    @Column(nullable = false)
    private boolean active;

    @Version
    private Long version;

    public Challenge(String title, String description, LocalDate startDate, LocalDate endDate, Long targetCount, int maxParticipants, boolean active) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.targetCount = targetCount;
        this.maxParticipants = maxParticipants;
        this.active = active;
    }

    public void updateChallenge(ChallengeRequestDto dto) {
        if(dto.getTitle() != null) this.title = dto.getTitle();
        if(dto.getDescription() != null) this.description = dto.getDescription();
        if(dto.getStartDate() != null) this.startDate = dto.getStartDate();
        if(dto.getEndDate() != null) this.endDate = dto.getEndDate();
        if(dto.getTargetCount() != null) this.targetCount = dto.getTargetCount();
        if(dto.getMaxParticipants() != 0) this.maxParticipants = dto.getMaxParticipants();
        this.active = dto.isActive(); // boolean은 기본값이 false라 null 체크 필요없음
    }

    public void incrementParticipants() {
        this.currentParticipants += 1;
    }
    public void decrementParticipants() {
        this.currentParticipants -= 1;
    }
}
