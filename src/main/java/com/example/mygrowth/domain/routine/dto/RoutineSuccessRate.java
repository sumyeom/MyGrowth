package com.example.mygrowth.domain.routine.dto;

import lombok.Getter;

@Getter
public class RoutineSuccessRate {
    private final double week;
    private final double month;
    private final long weeklyGoal;
    private final long weeklySuccess;

    public  RoutineSuccessRate(double week,double month, long weeklyGoal,long weeklySuccess){
        this.week = week;
        this.month = month;
        this.weeklyGoal = weeklyGoal;
        this.weeklySuccess = weeklySuccess;
    }

}
