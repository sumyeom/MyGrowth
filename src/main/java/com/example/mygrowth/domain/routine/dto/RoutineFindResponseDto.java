package com.example.mygrowth.domain.routine.dto;

import com.example.mygrowth.domain.routine.entity.Routine;
import com.example.mygrowth.domain.routine.enums.RepeatType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RoutineFindResponseDto {
    private final Long id;
    private final String title ;
    private final RepeatType repeatType;

    public static RoutineFindResponseDto fromEntity(Routine routine) {
        return new RoutineFindResponseDto(routine.getId(), routine.getTitle(), routine.getRepeatType());
    }
}
