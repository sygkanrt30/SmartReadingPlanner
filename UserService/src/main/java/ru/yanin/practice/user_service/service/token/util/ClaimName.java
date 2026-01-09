package ru.yanin.practice.user_service.service.token.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
enum ClaimName {
    ROLE("role");

    private final String name;
}
