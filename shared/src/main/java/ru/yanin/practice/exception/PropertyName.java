package ru.yanin.practice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public enum PropertyName {
    ERROR_CODE("errorCode"),
    TIMESTAMP("timestamp");

    private final String value;
}
