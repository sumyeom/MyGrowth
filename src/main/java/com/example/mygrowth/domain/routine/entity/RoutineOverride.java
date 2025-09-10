package com.example.mygrowth.domain.routine.entity;

import com.example.mygrowth.domain.routine.enums.RepeatType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name="routine_override")
@Getter
@NoArgsConstructor
public class RoutineOverride{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date; // 예외가 적용될 날짜
    private String title;   // 해당 날짜에 바뀐 제목
    private String description; // 필요시 바뀐 내용

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RepeatType repeatType;

    // 주간 반복 (요일 목록)
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "routine_override_days_week", joinColumns = @JoinColumn(name = "routine_override_id"))
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
    @JoinColumn(name = "routine_id")
    private Routine routine;

    public RoutineOverride(LocalDate date, String title, String description, RepeatType repeatType, List<DayOfWeek> daysOfWeek, Integer dayOfMonth, String dayOfYear, Integer goalCount, boolean isPublic, LocalDate startDate, LocalDate endDate,Routine routine) {
        this.date = date;
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
        this.routine = routine;
    }


}
