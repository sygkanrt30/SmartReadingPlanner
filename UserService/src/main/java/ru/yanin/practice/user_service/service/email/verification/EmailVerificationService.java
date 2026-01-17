package ru.yanin.practice.user_service.service.email.verification;

public interface EmailVerificationService {

    void sendCode(String email, Long userId);

    void verifyEmail(String email, String code);
}
