package ru.yanin.practice.apigateway.token;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
enum CookieName {
    HOST_AUTH_TOKEN("__Host-auth-token");

    private final String name;
}
