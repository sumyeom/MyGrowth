package com.example.mygrowth.domain.routine.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class RoutineStatsDto {
    private final Long totalGoalCount;
    private final Long totalLogCount;


    public RoutineStatsDto(Long totalGoalCount, Long totalLogCount) {
        this.totalGoalCount = totalGoalCount;
        this.totalLogCount = totalLogCount;
    }
}
