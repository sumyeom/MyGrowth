package com.example.mygrowth.domain.routine.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoutineCheckResultDto {
    private boolean isChecked;
    private String message;
    private Integer streakDays;

    public RoutineCheckResultDto(boolean isChecked, String message){
        this.isChecked = isChecked;
        this.message = message;
    }

    public RoutineCheckResultDto(boolean isChecked, String message, Integer streakDays){
        this.isChecked = isChecked;
        this.message = message;
        this.streakDays = streakDays;
    }
}
