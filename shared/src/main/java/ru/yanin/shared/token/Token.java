package ru.yanin.shared.token;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record Token(
        UUID id,
        String username,
        Long userId,
        List<String> roles,
        Instant createdAt,
        Instant expiresAt) {
}
