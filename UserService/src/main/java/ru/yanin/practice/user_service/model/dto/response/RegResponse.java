package ru.yanin.practice.user_service.model.dto.response;

public record RegResponse(
        String message,
        String token,
        String username,
        Long userId
) {}
