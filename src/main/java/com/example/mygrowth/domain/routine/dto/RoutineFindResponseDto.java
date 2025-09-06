package com.example.mygrowth.domain.routine.dto;

import com.example.mygrowth.domain.routine.entity.Routine;
import com.example.mygrowth.domain.routine.entity.RoutineLog;
import com.example.mygrowth.domain.routine.enums.RepeatType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
public class RoutineFindResponseDto {
    private final Long id;
    private final boolean isSuccess;
    private final String title ;

    public RoutineFindResponseDto(Long id, boolean isSuccess, String title) {
        this.id = id;
        this.isSuccess = isSuccess;
        this.title = title;
    }


}
