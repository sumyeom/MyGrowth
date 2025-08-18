package com.example.mygrowth.domain.routine.enums;

import lombok.Getter;

@Getter
public enum RepeatType {
    DAY_OF_WEEK("day_of_week"),
    MONTHLY("monthly"),
    YEARLY("yearly");

    private final String value;

    RepeatType(String value) {
        this.value = value;
    }
}
