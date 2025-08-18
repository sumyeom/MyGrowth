package com.example.mygrowth.domain.routine.util;

import com.example.mygrowth.domain.routine.dto.RoutineRequestDto;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;

public class RoutineValidationUtils {
    public static void validateRoutine(RoutineRequestDto dto) {
        switch (dto.getRepeatType()) {
            case DAY_OF_WEEK -> {
                if (dto.getDayOfWeek() == null || dto.getDayOfWeek().isEmpty()) {
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

    private static boolean isValidMonthDay(String monthDay) {
        try {
            LocalDate.parse("2024-" + monthDay); // 윤년으로 기본 검증
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
