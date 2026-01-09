package ru.yanin.practice.user_service.model;

import ru.yanin.practice.user_service.model.entity.Role;

import java.time.Instant;
import java.util.UUID;

public record Token(
        UUID id,
        String subject,
        Role role,
        Instant createdAt,
        Instant expiresAt) {
}