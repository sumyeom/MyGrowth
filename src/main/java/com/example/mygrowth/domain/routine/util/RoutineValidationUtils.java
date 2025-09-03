package com.example.mygrowth.domain.routine.util;

import com.example.mygrowth.domain.routine.dto.RoutineRequestDto;
import com.example.mygrowth.domain.routine.entity.Routine;
import com.example.mygrowth.global.exception.ApiException;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;

public class RoutineValidationUtils {
    public static void validateRoutine(RoutineRequestDto dto) {
        switch (dto.getRepeatType()) {
            case DAY_OF_WEEK -> {
                if (dto.getDaysOfWeek() == null || dto.getDaysOfWeek().isEmpty()) {
                    throw new IllegalArgumentException("요일을 하나 이상 선택해야 합니다.");
                }
            }
            case MONTHLY -> {
                if (dto.getDayOfMonth() == 0 || dto.getDayOfMonth() < 1 || dto.getDayOfMonth() > 31) {
                    throw new IllegalArgumentException("매주 반복일은 1~31 사이여야 합니다.");
                }
            }
            case YEARLY -> {
                if (!isValidMonthDay(dto.getDayOfYear())) {
                    throw new IllegalArgumentException("유효하지 않은 날짜 형식입니다. 예: 02-29");
                }
            }
        }
    }

    public static void validateRepeatType(Routine routine, LocalDate targetDate) {
        switch (routine.getRepeatType()) {
            case DAY_OF_WEEK -> {
                // 요일 검증
                if(routine.getDaysOfWeek() == null || routine.getDaysOfWeek().isEmpty()) {
                    throw new IllegalArgumentException("요일을 하나 이상 선택해야 합니다.");
                }

                boolean valid = routine.getDaysOfWeek().stream()
                        .anyMatch(d -> d.name().equalsIgnoreCase(targetDate.getDayOfWeek().name()));

                if(!valid) {
                    throw new IllegalArgumentException("잘못된 요일입니다.");
                }
            }
            case MONTHLY -> {
                // 매달 같은 일자
                if(routine.getDayOfMonth() == 0 ||
                routine.getDayOfMonth() != targetDate.getDayOfMonth()) {
                    throw new IllegalArgumentException("잘못된 요일입니다.");
                }
            }
            case YEARLY -> {
                // 매년 같은 월-일
                String monthDay = targetDate.getMonthValue() + "-" + targetDate.getDayOfMonth();
                if (!routine.getDayOfYear().equals(monthDay)) {
                    throw new IllegalArgumentException("잘못된 요일입니다.");
                }
            }
        }
    }

    private static boolean isValidMonthDay(String monthDay) {
        try {
            LocalDate.parse("2024-" + monthDay); // 윤년으로 기본 검증
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
