package ru.yanin.practice.user_service.model.dto.rabbit;

public record VerificationEvent(String email, String code) {
}
