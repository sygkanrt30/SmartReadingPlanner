package ru.yanin.shared.filter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@RequiredArgsConstructor
@Accessors(fluent = true)
public enum HeaderName {

    REQUEST_ID("X-Gateway-Request-ID"),
    PROCESSED("X-Gateway-Processed"),
    PROCESSING_TIME("X-Processing-Time"),

    USER_ID("X-User-ID"),
    USER_ROLES("X-User-Roles"),
    USER_USERNAME("X-User-Username"),

    RATE_LIMIT_POLICY("X-RateLimit-Policy"),
    INTERNAL_CODE("X-Internal-Code");

    private final String value;
}
