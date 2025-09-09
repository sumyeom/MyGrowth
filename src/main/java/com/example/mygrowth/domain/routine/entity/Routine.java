package com.example.mygrowth.domain.routine.entity;

import com.example.mygrowth.domain.routine.enums.RepeatType;
import com.example.mygrowth.domain.user.entity.User;
import com.example.mygrowth.global.common.BaseCreatedEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "routine")
@Getter
@NoArgsConstructor
public class Routine extends BaseCreatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RepeatType repeatType;

    // 주간 반복 (요일 목록)
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "routine_days_week", joinColumns = @JoinColumn(name = "routine_id"))
    private List<DayOfWeek> daysOfWeek;

    // 월간 반복
    private Integer dayOfMonth;

    // 연간 반복 (MM-DD 현태 문자열 : "12-25")
    private String dayOfYear;

    // 주간 목표 횟수
    private Integer goalCount;

    private boolean isPublic;

    private LocalDate startDate;

    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "routine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoutineOverride> overrides = new ArrayList<>();

    public Routine(String title, String description, RepeatType repeatType, List<DayOfWeek> daysOfWeek, Integer dayOfMonth, String dayOfYear, Integer goalCount, boolean isPublic, LocalDate startDate, LocalDate endDate, User user) {
        this.title = title;
        this.description = description;
        this.repeatType = repeatType;
        this.daysOfWeek = daysOfWeek;
        this.dayOfMonth = dayOfMonth;
        this.dayOfYear = dayOfYear;
        this.goalCount = goalCount;
        this.isPublic = isPublic;
        this.startDate = startDate;
        this.endDate = endDate;
        this.user = user;
    }

    public void update(String title, String description, RepeatType repeatType, List<DayOfWeek> daysOfWeek, Integer dayOfMonth, String dayOfYear, Integer goalCount, boolean isPublic, LocalDate startDate, LocalDate endDate) {
        this.title = title;
        this.description = description;
        this.repeatType = repeatType;
        this.daysOfWeek = daysOfWeek;
        this.dayOfMonth = dayOfMonth;
        this.dayOfYear = dayOfYear;
        this.goalCount = goalCount;
        this.isPublic = isPublic;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void updateStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void updateEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

}
