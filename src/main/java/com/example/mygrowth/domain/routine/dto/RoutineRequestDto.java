package com.example.mygrowth.domain.routine.dto;

import com.example.mygrowth.domain.routine.enums.RepeatType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class RoutineRequestDto {
    @NotBlank(message = "루틴 제목을 입력해주세요")
    private final String title;

    @NotBlank(message = "루틴 설명을 입력해주세요")
    private final String description;

    @NotNull(message = "반복 타입을 입력해주세요")
    private final RepeatType repeatType;

    private final List<DayOfWeek> daysOfWeek;

    private final int dayOfMonth;

    private final String dayOfYear;

    private final int goalCount;

    private final boolean isPublic;

    private final LocalDate startDate;

    private final LocalDate endDate;

}
