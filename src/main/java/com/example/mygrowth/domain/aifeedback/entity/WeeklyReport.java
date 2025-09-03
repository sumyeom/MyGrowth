package com.example.mygrowth.domain.aifeedback.entity;

import com.example.mygrowth.domain.user.entity.User;
import com.example.mygrowth.global.common.BaseCreatedEntity;
import com.example.mygrowth.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Entity
@Table(name = "weekly_report")
@Getter
@NoArgsConstructor
public class WeeklyReport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 리포트 대상 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 리포트 주차
    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    // 성공률 (%)
    @Column(nullable = false)
    private double successRate;

    // 가장 성취율 낮은 요일
    @Enumerated(EnumType.STRING)
    private DayOfWeek weakDay;

    // AI가 생성한 한줄 피드백
    @Column(columnDefinition = "TEXT")
    private String aiFeedback;

    public WeeklyReport(User user, LocalDate startDate, LocalDate endDate, double successRate, DayOfWeek weakDay, String aiFeedback) {
        this.user = user;
        this.startDate = startDate;
        this.endDate = endDate;
        this.successRate = successRate;
        this.weakDay = weakDay;
        this.aiFeedback = aiFeedback;
    }


}
