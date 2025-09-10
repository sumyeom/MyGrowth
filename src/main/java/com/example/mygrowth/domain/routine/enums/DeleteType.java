package com.example.mygrowth.domain.routine.enums;

import lombok.Getter;

@Getter
public enum DeleteType {
    ALL_DAY("all_day"),
    AFTER_DAY("after_day"),
    ONLY_DAY("only_day");

    private final String value;

    DeleteType(String value) {
        this.value = value;
    }
}
