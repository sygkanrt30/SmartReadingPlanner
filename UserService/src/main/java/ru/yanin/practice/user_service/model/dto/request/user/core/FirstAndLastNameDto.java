package ru.yanin.practice.user_service.model.dto.request.user.core;

import jakarta.validation.constraints.NotBlank;

public record FirstAndLastNameDto(@NotBlank String firstName, @NotBlank String lastName) {
}
