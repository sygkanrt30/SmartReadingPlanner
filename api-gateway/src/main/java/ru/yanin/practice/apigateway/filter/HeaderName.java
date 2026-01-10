package ru.yanin.practice.apigateway.filter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@RequiredArgsConstructor
@Accessors(fluent = true)
enum HeaderName {

    REQUEST_ID("X-Gateway-Request-ID"),
    PROCESSED("X-Gateway-Processed"),
    PROCESSING_TIME("X-Processing-Time"),

    USER_ID("X-User-ID"),
    USER_ROLES("X-User-Roles"),
    USER_USERNAME("X-User-Username"),

    RATE_LIMIT_POLICY("X-RateLimit-Policy");


    private final String value;
}
