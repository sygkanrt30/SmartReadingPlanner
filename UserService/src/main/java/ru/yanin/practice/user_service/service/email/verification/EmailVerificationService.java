package ru.yanin.practice.user_service.service.email.verification;

public interface EmailVerificationService {

    void generateAndSendCode(String email, Long userId);

    boolean verifyEmail(String email, String code);
}
