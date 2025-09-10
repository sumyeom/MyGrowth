package com.example.mygrowth.domain.routine.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class RoutineSuccessRate {
    private final double week;
    private final double month;
    private final double all;

    public RoutineSuccessRate(double week,double month,double all){
        this.week = week;
        this.month = month;
        this.all = all;
    }

}
