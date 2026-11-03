package ru.yanin.shared.cookie;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CookieName {
    HOST_AUTH_TOKEN("__Host-auth-token");

    private final String name;
}
