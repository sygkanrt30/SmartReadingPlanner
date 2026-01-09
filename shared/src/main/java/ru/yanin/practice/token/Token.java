package ru.yanin.practice.token;

import java.time.Instant;
import java.util.UUID;

public record Token(
        UUID id,
        String subject,
        Role role,
        Instant createdAt,
        Instant expiresAt) {
}
