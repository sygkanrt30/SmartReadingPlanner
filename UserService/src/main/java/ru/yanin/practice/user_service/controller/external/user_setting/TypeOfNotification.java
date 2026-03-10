package ru.yanin.practice.user_service.controller.external.user_setting;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
enum TypeOfNotification {
    TELEGRAM("telegram"),
    EMAIL("email"),
    WEEKLY_REPORT("weekly_report"),
    READING_REMINDER("reading_reminder");


    private final String value;

    static TypeOfNotification fromValue(String value) {
        for (var typeOfNotification : TypeOfNotification.values()) {
            if (typeOfNotification.getValue().equals(value)) {
                return typeOfNotification;
            }
        }
        throw new IllegalArgumentException(String.format("""
                Invalid notification type '%s'.
                It must be one of %s
                """, value, Arrays.toString(TypeOfNotification.values())));
    }
}
