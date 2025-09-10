package com.example.mygrowth.domain.routine.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoutineCheckResultDto {
    private boolean isSuccess;
    private String message;
    private Integer streakDays;

    public RoutineCheckResultDto(boolean isSuccess, String message){
        this.isSuccess = isSuccess;
        this.message = message;
    }

    public RoutineCheckResultDto(boolean isSuccess, String message, Integer streakDays){
        this.isSuccess = isSuccess;
        this.message = message;
        this.streakDays = streakDays;
    }
}
