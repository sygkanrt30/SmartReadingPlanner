package ru.yanin.practice.token;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ClaimName {
    ROLE("role"),
    USER_ID("user_id");

    private final String name;
}
